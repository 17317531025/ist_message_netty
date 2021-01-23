//package com.ist.message.controller;
//import com.ist.message.common.ResultConstant;
//import com.ist.message.common.pojo.ResultVO;
//import com.ist.message.controller.model.SendMsgReq;
//import com.ist.message.domain.dto.Response;
//import com.ist.message.service.ChatService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//
//@RestController
//@Slf4j
//public class MssageSocketController {
//    @Autowired
//    private ChatService chatService;
//    @RequestMapping(value = "sendMessage")
//    public Response sendMessage(HttpServletRequest request, @Valid SendMsgReq req, BindingResult bindingResult){
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                return new ResultVO(ResultConstant.PARAM_ERROR_CODE,msg);
//            }
//            log.info("sendMessage==>"+req);
//            Response resultVO = chatService.urlReceiveMsg(req.getMessage(), req.getReceiverId());
//            return resultVO;
//        }catch (Exception e){
//            log.error("SendMssageController.sendMessage",e);
//        }
//
//        return new ResultVO();
//    }
//
//    @RequestMapping(value = "closeSession")
//    public @ResponseBody ResultVO closeSession(@RequestParam(name = "userId") String  userId){
//        if (StringUtils.isNoneBlank(userId)){
//            chatService.closeSession(userId);
//        }
//        return new ResultVO();
//    }
//
//    @RequestMapping(value = "testAb")
//    public @ResponseBody ResultVO testAb(){
//        return new ResultVO();
//    }
//}
