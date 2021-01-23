package com.ist.message.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.ist.message.common.CodeConstant;
import com.ist.message.common.Exception.MessageException;
import com.ist.message.common.ResultConstant;
import com.ist.message.common.util.DateUtil;
import com.ist.message.common.util.HttpClientUtil;
import com.ist.message.common.util.RedisUtil;
import com.ist.message.config.IstConfig;
import com.ist.message.config.annotation.TokenCheck;
import com.ist.message.controller.model.UserVerifyTokenResp;
import com.ist.message.domain.ShopApp;
import com.ist.message.service.ShopAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Autowired
    private IstConfig istConfig;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ShopAppService shopAppService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = ((HandlerMethod) handler);

            if (handlerMethod.getMethod().isAnnotationPresent(TokenCheck.class)) {
                String tokenId = request.getParameter("tokenId");
                String userId = request.getParameter("userId");
                String appId = request.getParameter("appId");
                if(StringUtils.isEmpty(tokenId)){
                    throw new MessageException(ResultConstant.PARAM_ERROR_CODE,"tokenId不能为空");
                }
                if(StringUtils.isEmpty(userId)){
                    throw new MessageException(ResultConstant.PARAM_ERROR_CODE,"用户ID不能为空");
                }
                if(StringUtils.isEmpty(appId)){
                    throw new MessageException(ResultConstant.PARAM_ERROR_CODE,"应用ID不能为空");
                }

                StringBuilder sb = new StringBuilder(istConfig.getIstSvcUrl() + CodeConstant.IstSvc.USER_VERIFY_TOKEN);
                log.info("token校验开始,tokenId: >>>: "+tokenId);
                Map<String,String> params = new HashMap<>();
                String time = DateUtil.getString(new Date(),DateUtil.PATTERN_DATE_TIME);
                params.put("tokenId",tokenId);
                params.put("userId",userId);
                params.put("time",time);
                params.put("appId",appId);
                String sign = getSign(params);
                sb.append("?tokenId="+tokenId+"&userId="+userId+"&appId="+appId+"&time=" + time + "&sign=" + sign);
                try {
                    String result = HttpClientUtil.doGet(sb.toString());
                    UserVerifyTokenResp UserVerifyTokenResp = JSONObject.parseObject(result, com.ist.message.controller.model.UserVerifyTokenResp.class);
                    if(UserVerifyTokenResp!=null) {
                        if(UserVerifyTokenResp.getCode().equals("0")) {
                            if(UserVerifyTokenResp.getUserId().equals(userId)) {
                                return true;
                            }else {
                                throw new MessageException(ResultConstant.TOKENID_ERROR_CODE,ResultConstant.TOKEN_ERROR_MSG);
                            }
                        }else {
                            log.warn("token校验失败,tokenId: >>>: "+tokenId);
                            throw new MessageException(UserVerifyTokenResp.getCode(),UserVerifyTokenResp.getMsg());
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    log.warn("token校验失败,tokenId: >>>: "+tokenId);
                }
                log.warn("token校验失败,tokenId: >>>: "+tokenId);
                throw new MessageException(ResultConstant.TOKENID_ERROR_CODE,ResultConstant.TOKEN_ERROR_MSG);
            }
        }
        return true;
    }
    public String getSign(Map<String,String> resultMap){
        String appId = resultMap.remove("appId");
        List<String> keyList =  new ArrayList<String>(resultMap.keySet());
        Collections.sort(keyList);
        String appSecret = (String) redisUtil.get(CodeConstant.REDIS_APP_KEY_PREFIX + appId);
        if (StringUtils.isEmpty(appSecret)){
            ShopApp appInfo=shopAppService.queryShopApp(appId);
            if(null == appInfo){
                log.warn("应用信息不存在,签名校验失败");
                return "";
            }
            appSecret = appInfo.getAppSecret();
            //放入缓存
            redisUtil.set(CodeConstant.REDIS_APP_KEY_PREFIX + appId,appSecret,CodeConstant.RedisSaveTime.REDIS_SAVE_TIME_24h);
        }
        StringBuilder sb = new StringBuilder();
        String substr="appId="+appId+"&time="+resultMap.remove("time")+appSecret;
        //拼接参数
        for (String k : keyList) {
            String value = resultMap.get(k);
            sb.append(k+"="+value+"&");
        }
        sb.append(substr);
        //本地签名生成
        String localSign= DigestUtils.md5Hex(sb.toString());
        return localSign;
    }
}
