package com.example.gmall.service.order.biz;

import com.example.gmall.mq.ware.WareStockResultMsg;
import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.model.order.vo.OrderConfirmRespVO;
import com.example.gmall.model.order.vo.OrderSplitReps;
import com.example.gmall.model.order.vo.OrderSubmitVO;

import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 14:13
 */
public interface OrderBizService {

    /**
     * 获取订单确认数据
     * @return
     */
    OrderConfirmRespVO getOrderConfirmData();


    /**
     * 下订单
     * @param submitVO
     * @param tradeNo
     * @return
     */
    Long submitOrder(OrderSubmitVO submitVO, String tradeNo);

    /**
     * 关闭订单
     * @param id
     * @param userId
     */
    void closeOrder(Long id, Long userId);

    /**
     * 订单修改为已支付
     * @param out_trade_no
     * @param userId
     */
    void payedOrder(String out_trade_no, Long userId);

    void updateOrderStockStatus(WareStockResultMsg result);

    /**
     * 拆单
     * @param orderId
     * @param json
     * @return
     */
    List<OrderSplitReps> orderSplit(Long orderId, String json);

    /**
     * 保存秒杀单
     * @param info
     * @return
     */
    Long saveSeckillOrder(OrderInfo info);
}
