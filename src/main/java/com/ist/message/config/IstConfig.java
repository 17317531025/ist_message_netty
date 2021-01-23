package com.ist.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Setter
@Getter
public class IstConfig {

    @Value("${spring.profiles.active}")
    private String springProfilesActive;
    @Value("${server.port}")
    private String serverPort;
    @Value("${current.node.url}")
    private String currentNodeUrl;
    @Value("${ist.svc.url}")
    private String istSvcUrl;
    @Value("${msg.isInsertSwitch:0}")
    private String IsInsertMsgSwitch;
    @Value("${msg.isKafkaSwitch:0}")
    private Integer isKafka;
    @Value("${msg.kafkaTopic:chatMessage}")
    private String kafkaTopic;

    @Value("${weixin.pay.appId}")
    private String weiXinPayAppId;
    @Value("${weixin.pay.appSecret}")
    private String weiXinPayAppSecret;
    @Value("${weixin.pay.access_url}")
    private String weiXinPayAccessUrl;
    @Value("${weixin.pay.msgCheck}")
    private String msgCheckUrl;
    @Value("${weixin.msgCheck}")
    private Integer switchCheck;
}
