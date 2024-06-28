import com.example.gmall.model.order.entity.OrderInfo;
import com.example.gmall.service.order.mapper.OrderInfoMapper;
import com.example.gmall.service.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 11:23
 */
@SpringBootTest
public class ShardingTest {


    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderInfoService orderInfoService;

    @Test
    void testQuery(){
        List<OrderInfo> list = orderInfoService.lambdaQuery()
                .eq(OrderInfo::getUserId, 249L) //带了分片键
                .eq(OrderInfo::getId, 249L) //不带分片键，会引起全库全表扫描，效率低下
                .list();
        System.out.println(list);
    }


    @Test
    void testInsert(){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setConsignee("11");
        orderInfo.setConsigneeTel("11");
        orderInfo.setTotalAmount(new BigDecimal("11"));
        orderInfo.setOrderStatus("11");
        orderInfo.setUserId(3L); //分片键
        orderInfo.setPaymentWay("11");
        orderInfo.setDeliveryAddress("11");
        orderInfo.setOrderComment("11");
        orderInfo.setOutTradeNo("11");
        orderInfo.setTradeBody("11");
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date());
        orderInfo.setProcessStatus("11");
        orderInfo.setTrackingNo("11");
        orderInfo.setParentOrderId(0L);
        orderInfo.setImgUrl("11");
        orderInfo.setProvinceId(0L);
        orderInfo.setOperateTime(new Date());
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));
        orderInfo.setOriginalTotalAmount(new BigDecimal("0"));
        orderInfo.setFeightFee(new BigDecimal("0"));
        orderInfo.setRefundableTime(new Date());

        orderInfoMapper.insert(orderInfo);
        System.out.println("插入完成");
    }
}
