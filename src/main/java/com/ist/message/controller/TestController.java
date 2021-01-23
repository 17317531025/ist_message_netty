package com.ist.message.controller;

import com.ist.message.domain.ShopApp;
import com.ist.message.service.ShopAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @Autowired
    private ShopAppService shopAppService;
    @RequestMapping(value = "/test")
    public String index(){
//        ResultVO resultVO = restTemplate.postForObject("http://127.0.0.1:8010/ist_im/sendMessage", "{\"receiverId\":\"2\",\"type\":\"1\",\"data\":[{\"content\":\"test\",\"sender\":\"1\",\"msgType\":1,\"contentType\":1,\"no\":1,\"senderName\":\"test1\",\"talker\":\"2\"}]}", ResultVO.class);
//        System.out.println(resultVO.toString());
        return "index";
    }

    @RequestMapping(value = "/upload")
    public String upload(){
        return "upload";
    }

    @RequestMapping(value="/getAppIdSecret")
    public @ResponseBody
    String getAppIdSecret(@RequestParam("appId") String appId) {
        ShopApp appInfo = shopAppService.queryShopApp(appId);
//        if (null == appInfo){
//            return appId + "不存在";
//        }
        return appInfo.getAppSecret();
    }

}
