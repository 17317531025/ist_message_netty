//package com.ist.message.controller;
//
//import com.ist.message.common.ResultConstant;
//import com.ist.message.config.annotation.TokenCheck;
//import com.ist.message.controller.model.*;
//import com.ist.message.service.GroupService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//
//@RestController
//@Api(value = "聊天群组相关")
//public class GroupController extends BaseController{
//    @Autowired
//    private GroupService groupService;
//    @RequestMapping(value = "addGroup",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
//    @ApiOperation(value = "加群", notes = "群主id(groupId)不能为空，被邀请者(talker)不为空，邀请者(inviteUserId)不为空")
//    public BaseResp addGroup(HttpServletRequest request, @Valid AddGroupReq req,BindingResult bindingResult){
//        BaseResp resp = new BaseResp();
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                resp.setCode(ResultConstant.PARAM_ERROR_CODE);
//                resp.setMsg(msg);
//                return resp;
//            }
//            if (vaildAppSign(request.getParameterMap())){
//                resp = groupService.addGroup(req);
//            }else {
//                resp.setCode(ResultConstant.SIGN_ERROR_CODE);
//                resp.setMsg(ResultConstant.SIGN_ERROR_MSG);
//            }
//        }catch (Exception e){
//            logger.error("GroupController.addGroup",e);
//        }
//        return resp;
//    }
//
//    @RequestMapping(value = "createGroup",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
//    @ApiOperation(value = "创建群组", notes = "optUserId不能为空，组名不能为空")
//    public BaseResp createGroup(HttpServletRequest request, @Valid  CreateGroupReq req, BindingResult bindingResult){
//        BaseResp resp = new BaseResp();
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                resp.setCode(ResultConstant.PARAM_ERROR_CODE);
//                resp.setMsg(msg);
//                return resp;
//            }
//            if (vaildAppSign(request.getParameterMap())){
//                resp = groupService.createGroup(req);
//            }else {
//                resp.setCode(ResultConstant.SIGN_ERROR_CODE);
//                resp.setMsg(ResultConstant.SIGN_ERROR_MSG);
//            }
//        }catch (Exception e){
//            logger.error("GroupController.createGroup",e);
//        }
//        return resp;
//    }
//    @RequestMapping(value = "getGroupInfobyIds",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
//    @ApiOperation(value = "查询群基本信息", notes = "groupIds不能为空")
//    @TokenCheck
//    public BaseApiResp getGroupInfobyIds(HttpServletRequest request, @Valid  GetGroupInfoByIdsReq req, BindingResult bindingResult){
//        BaseApiResp resp = new BaseApiResp();
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                resp.setCode(ResultConstant.PARAM_ERROR_CODE);
//                resp.setMsg(msg);
//                return resp;
//            }
//            if (vaildAppSign(request.getParameterMap())){
//                resp = groupService.getGroupInfobyIds(req);
//            }else {
//                resp.setCode(ResultConstant.SIGN_ERROR_CODE);
//                resp.setMsg(ResultConstant.SIGN_ERROR_MSG);
//            }
//        }catch (Exception e){
//            logger.error("GroupController.getGroupInfobyIds",e);
//        }
//        return resp;
//    }
//
//    @RequestMapping(value = "getGroupMemInfobyIds",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
//    @ApiOperation(value = "查询群成员信息", notes = "groupIds不能为空")
//    @TokenCheck
//    public BaseApiResp getGroupMemInfobyIds(HttpServletRequest request, @Valid  GetGroupMemInfobyIdsReq req, BindingResult bindingResult){
//        BaseApiResp resp = new BaseApiResp();
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                resp.setCode(ResultConstant.PARAM_ERROR_CODE);
//                resp.setMsg(msg);
//                return resp;
//            }
//            if (vaildAppSign(request.getParameterMap())){
//                resp = groupService.getGroupMemInfobyIds(req);
//            }else {
//                resp.setCode(ResultConstant.SIGN_ERROR_CODE);
//                resp.setMsg(ResultConstant.SIGN_ERROR_MSG);
//            }
//        }catch (Exception e){
//            logger.error("GroupController.getGroupMemInfobyIds",e);
//        }
//        return resp;
//    }
//
//    @RequestMapping(value = "getGroupMembyId",produces="application/json;charset=UTF-8",method = RequestMethod.POST)
//    @ApiOperation(value = "查询群成员列表,群消息处理查询成员列表使用", notes = "groupId不能为空")
//    @TokenCheck
//    public BaseApiResp getGroupMembyId(HttpServletRequest request, @Valid  GetGroupMembyIdReq req, BindingResult bindingResult){
//        BaseApiResp resp = new BaseApiResp();
//        try {
//            if (bindingResult.hasErrors()){
//                String msg = bindingResult.getFieldError().getDefaultMessage();
//                resp.setCode(ResultConstant.PARAM_ERROR_CODE);
//                resp.setMsg(msg);
//                return resp;
//            }
//            if (vaildAppSign(request.getParameterMap())){
//                resp = groupService.getGroupMembyId(req);
//            }else {
//                resp.setCode(ResultConstant.SIGN_ERROR_CODE);
//                resp.setMsg(ResultConstant.SIGN_ERROR_MSG);
//            }
//        }catch (Exception e){
//            logger.error("GroupController.getGroupMembyId",e);
//        }
//        return resp;
//    }
//}
