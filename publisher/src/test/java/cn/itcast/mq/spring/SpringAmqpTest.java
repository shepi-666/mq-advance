package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        // 准备消息
        String routingKey = "simple.test";
        String message = "hello, spring amqp!";

        // 发送消息
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 准备confirmCallback
        correlationData.getFuture().addCallback(confirm -> {
            // 判断结果
            if (confirm.isAck()) {
                log.info("消息成功投递到交换机!消息Id{}", correlationData.getId());
            } else {
                log.error("消息投递到交换机失败，消息Id{}", correlationData.getId());
            }
        }, throwable -> {
            // 记录日志
            log.error("消息发送失败", throwable);
        });

        rabbitTemplate.convertAndSend("amq.topic", routingKey, message, correlationData);
    }

    @Test
    public void testDurableMessage() {
        // 准备消息
        Message mess = MessageBuilder.withBody("hello, world".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        // 发送消息
        rabbitTemplate.convertAndSend("simple.queue", mess);
    }
}
