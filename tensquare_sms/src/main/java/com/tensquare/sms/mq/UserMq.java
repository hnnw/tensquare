package com.tensquare.sms.mq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "user", durable = "true"),
        exchange = @Exchange(
                value = "tensquare",
                ignoreDeclarationExceptions = "true",
                type = ExchangeTypes.TOPIC
        ),
        key = {"#.#"}))
public class UserMq {

    @RabbitHandler
    public void sendSms(Map<String,String> message){
        System.out.println("手机号："+message.get("mobile"));
        System.out.println("验证码："+message.get("code"));
    }
}
