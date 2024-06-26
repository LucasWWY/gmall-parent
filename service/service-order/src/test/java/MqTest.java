import com.example.gmall.common.mq.MqService;
import com.example.gmall.model.order.entity.OrderInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author lfy
 * @Description
 * @create 2022-12-23 15:12
 */
@Slf4j
@SpringBootTest
public class MqTest {


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MqService mqService;

    @Test
    void testSend(){
        //无论成功失败，confirm回调都会触发，如果消息不能抵达给queue，return回调就会触发

        //2. 确认回调
        /**
         * 成功： ack=true - rabbitTemplate.convertAndSend("hello","a","哈哈哈哈");
         * 失败：
         *  交换机不存在: ack=false - rabbitTemplate.convertAndSend("??","a","哈哈哈哈");
         *  队列不存在：  ack=true - rabbitTemplate.convertAndSend("hello","ab","哈哈哈哈")
         * 因为confirm回调来自exchange，所以队列不存在但交换机存在的情况仍然是true
         * correlationData： 关联的数据
         * ack：  回复状态
         * cause: 原因
         */
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData,
                                           boolean ack,
                                           String cause)->{
            log.info("confirm回调： data:{},ack:{},cause:{}",correlationData,ack,cause);
            //发不成功的消息要记录到后台数据库，等待人工处理
        });

        //3. return回调
        /**
         * 成功：不触发回调
         * 失败：
         *   队列不存在： replyCode：312 提示错误
         * message: 当前消息
         * replyCode: 回复码
         * replyText：回复文本
         * exchange: 交换机
         * routingKey：路由键
         */
        rabbitTemplate.setReturnCallback((Message message,
                                          int replyCode,
                                          String replyText,
                                          String exchange,
                                          String routingKey)->{
            log.info("return回调： message:{},replyCode:{},replyText:{},exchange:{},routingKey:{}", message,replyCode,replyText,exchange,routingKey);
            //TODO 发不成功的消息要记录到后台数据库，等待人工处理
        });

        //4. 设置重试模板
        rabbitTemplate.setRetryTemplate(new RetryTemplate()); //不再有回调逻辑

        //1. 向broker发送消息
        rabbitTemplate.convertAndSend("hello","a","哈哈哈哈");
        System.out.println("发送完成...");
    }

    //测试使用了@EnableMqService后是否能自动注入依赖
    @Test
    void testSend2(){
        OrderInfo orderInfo = new OrderInfo();
        mqService.send(orderInfo,"hello","a");
    }
}
