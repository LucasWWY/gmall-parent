package com.example.gmall.service.order.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.gmall.common.authen.UserAuthUtils;
import com.example.gmall.common.config.mq.MqService;
import com.example.gmall.common.constant.MqConst;
import com.example.gmall.common.constant.RedisConst;
import com.example.gmall.common.execption.GmallException;
import com.example.gmall.common.result.ResultCodeEnum;
import com.example.gmall.feign.cart.CartFeignClient;
import com.example.gmall.feign.product.ProductSkuDetailFeignClient;
import com.example.gmall.feign.user.UserFeignClient;
import com.example.gmall.feign.ware.WareFeignClient;
import com.example.gmall.model.cart.entity.CartItem;
import com.example.gmall.model.enums.OrderStatus;
import com.example.gmall.model.enums.PaymentWay;
import com.example.gmall.model.enums.ProcessStatus;
import com.example.gmall.model.order.entity.OrderDetail;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.model.order.vo.OrderConfirmRespVO;
import com.example.gmall.model.order.vo.OrderSplitReps;
import com.example.gmall.model.order.vo.OrderSubmitVO;
import com.example.gmall.model.order.vo.SkuWare;
import com.example.gmall.model.user.entity.UserAddress;
import com.example.gmall.mq.logistic.OrderLogisticMsg;
import com.example.gmall.mq.ware.WareStockResultMsg;
import com.example.gmall.service.order.biz.OrderBizService;
import com.example.gmall.service.order.service.OrderDetailService;
import com.example.gmall.service.order.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:13
 */
@EnableTransactionManagement
@Slf4j
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    ProductSkuDetailFeignClient skuDetailFeignClient;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    MqService mqService;

    @Override
    public OrderConfirmRespVO getOrderConfirmData() {
        OrderConfirmRespVO vo = new OrderConfirmRespVO();

        //1、商品列表： 远程找购物车要到所有选中的商品
        List<CartItem> data = cartFeignClient.getChecked().getData();
        //【小心丢失请求头风险（隐式透传），一定要开启feign拦截器(通过@Enable注解)】
        List<OrderConfirmRespVO.SkuDetail> collect = data.stream()
                .map(item -> {
                    OrderConfirmRespVO.SkuDetail detail = new OrderConfirmRespVO.SkuDetail(); //SkuDetail是静态内部类，只拿需要的数据封装
                    detail.setSkuId(item.getSkuId());
                    detail.setImgUrl(item.getImgUrl());
                    detail.setSkuName(item.getSkuName());
                    detail.setSkuNum(item.getSkuNum());
                    //商品的实时价格
                    BigDecimal price = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    detail.setOrderPrice(price);
                    //查询这个商品的库存状态
                    //虽然ware-manager没有在nacos注册，但是feignClient仍然可以正常使用 一个请求只要浏览器能发 feignClient就能发
                    String hasStock = wareFeignClient.hasStock(item.getSkuId(), item.getSkuNum());
                    detail.setHasStock(hasStock);
                    return detail;
                }).collect(Collectors.toList());
        vo.setDetailArrayList(collect);

        //2、总数量
        Integer totalNum = collect.stream()
                .map(OrderConfirmRespVO.SkuDetail::getSkuNum)
                .reduce((o1, o2) -> o1 + o2)
                .get();
        vo.setTotalNum(totalNum);

        //3、总金额
        BigDecimal totalAmount = collect.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        vo.setTotalAmount(totalAmount);

        //4、收货地址列表
        Long userId = Long.valueOf(UserAuthUtils.getRequest().getHeader(RedisConst.USER_ID_HEADER)); //userId有可能不在的情况 在进入这个网址前已经被网关拦截检验是否登录了
        List<UserAddress> addresses = userFeignClient.getUserAddress(userId).getData();
        vo.setUserAddressList(addresses);

        //5、流水号
        //1）、开启整个订单的追踪功能
        //2）、防止订单重复提交（网络卡顿，流水号相同的订单可能被提交了多次；除非刷新重新调用当前方法，生成新流水号）
        //String orderNo = UUID.randomUUID().toString();
        String tradeNo = "example-"+System.currentTimeMillis()+"-"+userId;
        //给客户端放一个流水号
        vo.setTradeNo(tradeNo);
        //给redis放一个防重复提交token：流水号
        redisTemplate.opsForValue().set(RedisConst.REPEAT_TOKEN +tradeNo,"1",2,
                TimeUnit.MINUTES);

        return vo;
    }

    @Transactional
    @Override
    public Long submitOrder(OrderSubmitVO submitVO, String tradeNo) {
        //前端发送来的数据由order_info + order_detail构成
        //1、给order_info保存订单基本信息
        //2、给order_detail保存订单明细信息

        //校验：后端永远不要相信前端带来的所有数据，能校验就校验
        //参数校验：邮箱格式 是不是数字 交给jsr303校验功能来做 OrderApiController (@Valid) + OrderSubmitVO (@NotEmpty...)
        //业务校验：
        //   - 校验令牌：防止重复提交
        //   - 校验库存：防止大量无库存订单，虽然不防并发
        //   - 校验价格：实时价格，前端价格不可靠

        //超卖：
        //正常下单是允许超卖：无需提前锁库存，提前锁会导致恶意占库存
        //秒杀业务不允许超卖：锁库存、扣库存
        //正常业务用支付锁库存的方式

        //1、校验令牌：防止重复提交
        //在getOrderConfirmData()生成订单数据的时候，已经提前在redis中放置了一个防重复提交的token：流水号
        Boolean delete = redisTemplate.delete(RedisConst.REPEAT_TOKEN + tradeNo);
        if(!delete){ //删除成功，说明第一次提交
            throw new GmallException(ResultCodeEnum.REPEAT_REQUEST);
        }

        //2、校验库存
        List<OrderSubmitVO.OrderDetailListDTO> noStockSku = submitVO.getOrderDetailList()
                .stream()
                .filter(item -> "0".equals(wareFeignClient.hasStock(item.getSkuId(), item.getSkuNum()))) //前端数据不是有hasStock字段为什么不用呢？因为前端请求可能被拦截篡改
                .collect(Collectors.toList());
        if(noStockSku!=null && noStockSku.size()>0){
            String skuNames = noStockSku.stream()
                    .map(OrderSubmitVO.OrderDetailListDTO::getSkuName)
                    .reduce((o1, o2) -> o1 + "；" + o2)
                    .get();
            GmallException exception = new GmallException(skuNames + "； 没有库存", ResultCodeEnum.NO_STOCK.getCode());
            throw exception;
        }

        //3、校验价格
        List<OrderSubmitVO.OrderDetailListDTO> priceChangeSkus = submitVO.getOrderDetailList()
                .stream()
                .filter(item -> {
                    BigDecimal orderPrice = item.getOrderPrice();
                    BigDecimal latestPrice = skuDetailFeignClient.getPrice(item.getSkuId()).getData();
                    return Math.abs(orderPrice.subtract(latestPrice).doubleValue()) >= 0.0001; //filter只要true false
                }).collect(Collectors.toList());

        if(priceChangeSkus!=null && priceChangeSkus.size()>0){
            String skuNames = priceChangeSkus.stream()
                    .map(OrderSubmitVO.OrderDetailListDTO::getSkuName)
                    .reduce((o1, o2) -> o1 + "；" + o2)
                    .get();
            GmallException exception = new GmallException(skuNames + "； 价格变化，请刷新页面重新确认", ResultCodeEnum.PRICE_CHANGE.getCode());
            //为什么要刷新页面？不刷新页面，因为第一次submit后，redis中的防重复提交token：流水号已经被删除了，刷新一次重新调用getOrderConfirmData重新向redis添加token
            throw  exception;
        }

        //所有都没有问题了，接下来把订单数据保存到数据库
        //1、给order_info保存订单基本信息
        OrderInfo orderInfo = prepareOrderInfo(submitVO,tradeNo);
        orderInfoService.save(orderInfo);
        //插入时 要根据分片算法决定去哪个分片，分片设置了自增id使用雪花算法生成 application-sharding.yaml
        //key-generators:
        //oid_gen:
        //type: SNOWFLAKE
        //注意：订单id是对内使用的，为了不泄漏内部信息，对外使用流水号，二者都是唯一的
        Long orderId = orderInfo.getId();

        //1.1 延时任务：订单限时有效 失效关闭
        //提交订单时，每个order被设置了订单有效时间：30min以后关闭 见prepareOrderInfo()
        //这里给每个订单绑定了延时任务，30min后开启ScheduledExecutorService管理的线程池中的一个线程，执行关闭订单操作
        //不同于定时任务，定时任务需要扫描全部数据；延时任务绑定在每个order上，到时自动开启
        //ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
        //service.schedule(()->{
        //    closeOrder(orderInfo);
        //},30,TimeUnit.MINUTES);
        //1.2 MQ实现自动关单

        //2、给order_detail保存订单明细信息
        List<OrderDetail> orderDetails = prepareOrderDetails(submitVO,orderInfo);
        orderDetailService.saveBatch(orderDetails);

        //3、删除购物车中选中的商品
        cartFeignClient.deleteChecked();

        //4、发送订单创建成功消息
        mqService.send(orderInfo, MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_CREATE_RK);

        return orderId;
    }

    /**
     * 根据前端带来的VO数据，得到orderInfo数据
     * @param submitVO
     * @param tradeNo
     * @return
     */
    private OrderInfo prepareOrderInfo(OrderSubmitVO submitVO, String tradeNo) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setConsignee(submitVO.getConsignee());
        orderInfo.setConsigneeTel(submitVO.getConsigneeTel());
        orderInfo.setDeliveryAddress(submitVO.getDeliveryAddress());
        orderInfo.setOrderComment(submitVO.getOrderComment());

        //订单总额： = 原价金额 - 优惠金额
        BigDecimal totalAmount = submitVO.getOrderDetailList()
                .stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                //走到这一步说明前面的校验都通过了，不然会被调用此方法前拦截抛错，特别是价格没有问题，所以这里可以直接用订单的价格
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        orderInfo.setTotalAmount(totalAmount);
x
        //订单状态（对外 给客户看）
        orderInfo.setOrderStatus(OrderStatus.UNPAID.toString());
        //用户id
        Long userId = Long.valueOf(UserAuthUtils.getRequest().getHeader(RedisConst.USER_ID_HEADER));
        orderInfo.setUserId(userId);
        //支付方式
        orderInfo.setPaymentWay(PaymentWay.ONLINE.toString());
        //对外流水号
        orderInfo.setOutTradeNo(tradeNo); //对内使用雪花算法生成的orderId，跟第三方系统对接使用流水号
        //交易体
        String skuName = submitVO.getOrderDetailList().get(0).getSkuName(); //随机拿的
        orderInfo.setTradeBody(skuName);
        //创建时间
        orderInfo.setCreateTime(new Date());
        //失效时间  30min不支付，订单就要被关闭
        Date date = new Date(System.currentTimeMillis() + 30 * 60 * 1000);
        orderInfo.setExpireTime(date);
        //处理状态（对内 后台管理员）
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        //未发货 没有单号
        orderInfo.setTrackingNo("");
        //未拆单
        orderInfo.setParentOrderId(null);
        //图片
        String imgUrl = submitVO.getOrderDetailList().get(0).getImgUrl();
        orderInfo.setImgUrl(imgUrl);
        //配送地址都有 不管
        orderInfo.setProvinceId(0L);
        orderInfo.setOperateTime(new Date());

        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));

        //原价金额
        orderInfo.setOriginalTotalAmount(totalAmount);
        //运费
        orderInfo.setFeightFee(new BigDecimal("0"));

        return orderInfo;
    }

    //准备order_detail数据
    private List<OrderDetail> prepareOrderDetails(OrderSubmitVO submitVO,
                                                  OrderInfo orderInfo) {
        List<OrderDetail> details = submitVO.getOrderDetailList()
                .stream()
                .map(item -> {
                    OrderDetail orderDetail = new OrderDetail();
                    //插入时 要根据分片算法决定去哪个分片，分片设置了自增id使用雪花算法生成 application-sharding.yaml
                    //key-generators:
                    //oid_gen:
                    //type: SNOWFLAKE
                    //注意：订单id是对内使用的，为了不泄漏内部信息，对外使用流水号，二者都是唯一的
                    orderDetail.setOrderId(orderInfo.getId());
                    orderDetail.setUserId(orderInfo.getUserId());
                    orderDetail.setSkuId(item.getSkuId());
                    orderDetail.setSkuName(item.getSkuName());
                    orderDetail.setImgUrl(item.getImgUrl());
                    orderDetail.setOrderPrice(item.getOrderPrice());
                    orderDetail.setSkuNum(item.getSkuNum());
                    orderDetail.setCreateTime(new Date());
                    orderDetail.setSplitTotalAmount(item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())));
                    //有些优惠只针对特定商品，所以要拆分算单独某一产品的总价
                    orderDetail.setSplitActivityAmount(new BigDecimal("0"));
                    orderDetail.setSplitCouponAmount(new BigDecimal("0"));
                    return orderDetail;
                })
                .collect(Collectors.toList());

        return details;
    }

    @Override
    public void closeOrder(Long id, Long userId) {

        ProcessStatus closed = ProcessStatus.CLOSED;
        //只有订单未支付的情况下才需要关闭；
        //process_status=CLOSED
        //order_status=CLOSED
        //能来关单都是超了30min的订单消息； 是幂等的
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, closed.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus, closed.name())
                .eq(OrderInfo::getId, id)
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOrderStatus, OrderStatus.UNPAID.name()) //这两个eq保证关单的幂等
                .eq(OrderInfo::getProcessStatus, ProcessStatus.UNPAID.name())//这两个eq保证关单的幂等
                .update();
        log.info("订单：{},关闭：{}",id,update);
    }

    @Override
    public void payedOrder(String out_trade_no, Long userId) {
        //关单消息和支付消息如果同时抵达，无论谁先执行，最终结果都应该是以支付状态为准的。
        //1、关单先运行，改成已关闭。支付后运行就应该改回来为已支付
        //2、支付先运行，改为已支付。关单后运行就什么都不做
        //订单是未支付或者是已关闭，都可以改为已支付
        //update OrderStatus = 已支付 and ProcessStatus=已支付
        // where out_trade_no=? and user_id=? and
        // OrderStatus IN (未支付，已关闭) and ProcessStatus(未支付、已关闭)
        ProcessStatus payed = ProcessStatus.PAID;
        //修改订单为已支付状态
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, payed.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus, payed.name())
                .eq(OrderInfo::getUserId, userId)
                .eq(OrderInfo::getOutTradeNo, out_trade_no)
                .in(OrderInfo::getOrderStatus, OrderStatus.UNPAID.name(), OrderStatus.CLOSED.name())
                .in(OrderInfo::getProcessStatus, ProcessStatus.UNPAID.name(), ProcessStatus.CLOSED.name())
                .update();
        log.info("修改{}订单，已支付状态：{}",out_trade_no,update);

    }

    @Override
    public void updateOrderStockStatus(WareStockResultMsg result) {
        //1、最终订单要修改成的状态
        ProcessStatus status = ProcessStatus.WAITING_DELEVER;
        switch (result.getStatus()) {
            case "DEDUCTED": status = ProcessStatus.WAITING_DELEVER; break; //扣减就是等待发货
            case "OUT_OF_STOCK": status = ProcessStatus.STOCK_EXCEPTION; break; //扣减失败就是等待调货
        }

        OrderInfo orderInfo = orderInfoService.getById(result.getOrderId());

        //注意：一旦使用消息队列，就和http没有任何关系了，我们以前透传的所有东西都不能用；
        //如果想要后来用的字段，发消息的时候就必须带上。
        //2、修改订单状态
        orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus,status.getOrderStatus().name())
                .set(OrderInfo::getProcessStatus,status.name())
                .eq(OrderInfo::getId,orderInfo.getId())
                .eq(OrderInfo::getUserId,orderInfo.getUserId())
                .eq(OrderInfo::getOrderStatus,OrderStatus.PAID.name())
                .eq(OrderInfo::getProcessStatus,ProcessStatus.PAID.name())
                .update();
        log.info("订单库存状态更新完成");

        //下电子面单，进行发货
        if("DEDUCTED".equals(result.getStatus())){
            OrderLogisticMsg msg = new OrderLogisticMsg();
            msg.setOrderId(orderInfo.getId());
            msg.setUserId(orderInfo.getUserId());
            //给等待物流配送的订单队列发送消息
            mqService.send(msg,MqConst.ORDER_EVENT_EXCHANGE,MqConst.ORDER_LOGISTIC_RK);
        }


    }

    @Override
    public List<OrderSplitReps> orderSplit(Long orderId, String json) {
        //[{"wareId":"1","skuIds":["50"]},{"wareId":"2","skuIds":["49"]}]
        //大订单（orderId）拆分成  子订单（根据大订单中所有商品的库存分布，拆分成子订单，把子订单都存到数据库）
        OrderInfo parentOrder = orderInfoService.getById(orderId);

        //拿到大订单中所有商品
        List<OrderDetail> orderDetails = orderDetailService.lambdaQuery()
                .eq(OrderDetail::getOrderId, parentOrder.getId())
                .eq(OrderDetail::getUserId, parentOrder.getUserId())
                .list();

        //1、得到大订单中所有商品的库存分布
        List<SkuWare> skuWares = JSON.parseObject(json, new TypeReference<List<SkuWare>>() {
        });

        AtomicInteger i = new AtomicInteger(0);
        //2、拆分子订单
        List<OrderInfo> childOrders = skuWares.stream()
                .map(item->{
                    OrderInfo childOrder = new OrderInfo();

                    childOrder.setConsignee(parentOrder.getConsignee());
                    childOrder.setConsigneeTel(parentOrder.getConsigneeTel());
                    //子订单总额：子订单负责的商品的总额
                    List<Long> skuIds = item.getSkuIds(); //当前子订单负责的所有商品id
                    //拿到子订单负责的所有商品
                    List<OrderDetail> childDetails = orderDetails.stream()
                            .filter(obj -> skuIds.contains(obj.getSkuId()))
                            .collect(Collectors.toList());
                    childOrder.setOrderDetails(childDetails);

                    //计算子订单总额
                    BigDecimal totalAmount = childDetails.stream()
                            .map(o1 -> o1.getOrderPrice().multiply(new BigDecimal(o1.getSkuNum())))
                            .reduce((o1, o2) -> o1.add(o2))
                            .get();
                    childOrder.setTotalAmount(totalAmount);

                    childOrder.setOrderStatus(parentOrder.getOrderStatus());
                    childOrder.setUserId(parentOrder.getUserId());
                    childOrder.setPaymentWay(parentOrder.getPaymentWay());
                    childOrder.setDeliveryAddress(parentOrder.getDeliveryAddress());
                    childOrder.setOrderComment(parentOrder.getOrderComment());
                    childOrder.setOutTradeNo(i.getAndIncrement()+"_"+parentOrder.getOutTradeNo());

                    childOrder.setTradeBody(childDetails.get(0).getSkuName());
                    childOrder.setCreateTime(new Date());
                    childOrder.setExpireTime(parentOrder.getExpireTime());
                    childOrder.setProcessStatus(parentOrder.getProcessStatus());
                    childOrder.setTrackingNo("");
                    childOrder.setParentOrderId(parentOrder.getId());
                    childOrder.setImgUrl(childDetails.get(0).getImgUrl());
                    childOrder.setOperateTime(new Date());
                    childOrder.setActivityReduceAmount(new BigDecimal("0"));
                    childOrder.setCouponAmount(new BigDecimal("0"));
                    childOrder.setOriginalTotalAmount(totalAmount);
                    childOrder.setFeightFee(new BigDecimal("0"));
                    childOrder.setWareId(item.getWareId());

                    //准备返回数据

                    return childOrder;
                }).collect(Collectors.toList());

        for (OrderInfo orderInfo : childOrders) {
            //保存子订单
            orderInfoService.save(orderInfo);
            Long id = orderInfo.getId();

            //保存子订单的明细
            List<OrderDetail> details = orderInfo.getOrderDetails().stream()
                    .map(item -> {
                        item.setOrderId(id); //回填子订单id
                        return item;
                    }).collect(Collectors.toList());
            orderDetailService.saveBatch(details);
        }



        //把父订单改为已拆分
        boolean update = orderInfoService.lambdaUpdate()
                .set(OrderInfo::getOrderStatus, OrderStatus.SPLIT.name())
                .set(OrderInfo::getProcessStatus, ProcessStatus.SPLIT.name())
                .eq(OrderInfo::getId, parentOrder.getId())
                .eq(OrderInfo::getUserId, parentOrder.getUserId())
                .update();

        List<Long> ids = childOrders.stream().map(item -> item.getId()).collect(Collectors.toList());
        log.info("拆单完成：大订单：{} 拆分为：{}",parentOrder.getId(),ids);



        //准备响应结果
        List<OrderSplitReps> collect = childOrders.stream()
                .map(item -> {
                    OrderSplitReps reps = new OrderSplitReps();
                    reps.setOrderId(item.getId());
                    reps.setUserId(item.getUserId());
                    reps.setConsignee(item.getConsignee());
                    reps.setConsigneeTel(item.getConsigneeTel());
                    reps.setOrderComment(item.getOrderComment());
                    reps.setOrderBody(item.getTradeBody());
                    reps.setDeliveryAddress(item.getDeliveryAddress());
                    reps.setPaymentWay("2");
                    reps.setWareId(item.getWareId());

                    //订单明细
                    List<OrderDetail> details = item.getOrderDetails();

                    //List<Sku> details
                    List<OrderSplitReps.Sku> skuList = details.stream().map(o1 -> {
                        OrderSplitReps.Sku sku = new OrderSplitReps.Sku();
                        sku.setSkuId(o1.getSkuId());
                        sku.setSkuNum(o1.getSkuNum());
                        sku.setSkuName(o1.getSkuName());
                        return sku;
                    }).collect(Collectors.toList());
                    reps.setDetails(skuList);
                    return reps;
                }).collect(Collectors.toList());

        return collect;
    }

    @Override
    public Long saveSeckillOrder(OrderInfo info) {
        //1、保存订单
        boolean save = orderInfoService.save(info);
        Long id = info.getId();
        //2、保存订单明细
        List<OrderDetail> details = info.getOrderDetails()
                .stream()
                .map(item -> {
                    item.setOrderId(id); //回填订单id
                    return item;
                }).collect(Collectors.toList());
        orderDetailService.saveBatch(details);

        //TODO 独立再设计一套MQ队列交换机等
        return id;
    }



    //    @PostMapping("/xxxxx")
    public String submitOrdertest(@RequestParam("tradeNo") String tradeNo){
        //1、利用删除比对机制： 缺点：必须提前放好东西
        Boolean delete = redisTemplate.delete("repeat:token:" + tradeNo);
        if(delete){
            //删除成功； 说明我是第一个人，执行业务
        }else {
            //删不成功;  请求次数过于频繁稍后再试...
        }

        //2、利用占坑机制（分布式锁不带解锁机制）防重
        Boolean absent = redisTemplate.opsForValue().setIfAbsent(tradeNo, "1");
        if(absent){
            //占坑成功。处理业务
        }else {
            // 请求次数过于频繁稍后再试...
        }

        //3、利用 计数机制
        Long increment = redisTemplate.opsForValue().increment(tradeNo);
        if(increment > 1){
            // 说明已经增过： 请求次数过于频繁稍后再试...
        }else {
            // 说明我是第一个人，执行业务
        }

        //4、客户端保证防重复提交是不靠谱的。

        return "";

    }
}
