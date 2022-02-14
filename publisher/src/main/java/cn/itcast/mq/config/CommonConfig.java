package cn.itcast.mq.config;

import com.rabbitmq.client.ReturnCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取RabbitTemplate对象
        RabbitTemplate rabbit = applicationContext.getBean(RabbitTemplate.class);
        // 配置returnCallback
        rabbit.setReturnCallback((message, i, s, s1, s2) -> {
            log.error("消息队列发送失败, 响应码{}, 失败原因{}, 交换机{}, 路由{}, 消息{}", i, s, s1, s2, message.toString());
            // 消息的重发，或者通知管理员
        });

    }
}
