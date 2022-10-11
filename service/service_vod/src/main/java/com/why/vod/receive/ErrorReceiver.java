package com.why.vod.receive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.why.commonconst.RabbitConst.*;

@Slf4j
@Component
public class ErrorReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QUEUE_ERROR_VIDEO),
            exchange = @Exchange(name = EXCHANGE_ERROR_VIDEO),
            key = {ROUTING_KEY_ERROR_VIDEO}
    ))
    public void handleErrorMessage(Message message){
        log.info("处理了失败消息:" + new String(message.getBody()));
        System.out.println("处理了失败消息:" + new String(message.getBody()));
    }
}
