package com.ist.message.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ist.message.common.CodeConstant;
import com.ist.message.common.util.RedisUtil;
import com.ist.message.domain.ShopApp;
import com.ist.message.service.ShopAppService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.Resource;
import java.util.*;

public class BaseController {
    @Resource(name = "all_log")
    protected Logger logger;
    @Autowired
    private ShopAppService shopAppService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 终端交互通用签名校验
     * @return
     */
    public boolean  vaildAppSign(Object request){
        Map<String,Object> params = JSONObject.parseObject(JSON.toJSONString(request));

        //整理请求参数
        Map<String, String> resultMap = new HashMap<String, String>();
        List<String> keyList = new ArrayList<String>(params.keySet());
        for (String k : keyList) {
//            String[] value = params.get(k);
            resultMap.put(k, params.get(k).toString());
        }
        String appId = resultMap.remove("appId");
        String appSecret = (String) redisUtil.get(CodeConstant.REDIS_APP_KEY_PREFIX + appId);
        if (StringUtils.isEmpty(appSecret)){
            ShopApp appInfo=shopAppService.queryShopApp(appId);
            if(null == appInfo){
                logger.warn("应用信息不存在,签名校验失败");
                return false;
            }
            appSecret = appInfo.getAppSecret();
            //放入缓存
            redisUtil.set(CodeConstant.REDIS_APP_KEY_PREFIX + appId,appSecret,CodeConstant.RedisSaveTime.REDIS_SAVE_TIME_24h);
        }
        //获取基本信息
        String sign=resultMap.remove("sign");
        String substr="appId="+appId+"&time="+resultMap.remove("time")+appSecret;
        //排序参数
        keyList = new ArrayList<String>(resultMap.keySet());
        Collections.sort(keyList);
        StringBuilder sb = new StringBuilder();
        //拼接参数
        for (String k : keyList) {
            String value = resultMap.get(k);
            sb.append(k+"="+value+"&");
        }
        sb.append(substr);
        //本地签名生成
        String localSign= DigestUtils.md5Hex(sb.toString());
        //比对签名
        if(!StringUtils.equalsIgnoreCase(localSign, sign)){
            logger.warn("组装签名数据:"+sb.toString() + ",签名校验失败,localSign:"+localSign+",sign:"+sign);
            return false;
        }
        return true;
    }

    public boolean vaildAppSign(Map<String, String[]> params){
        //整理请求参数
//        Map<String, String> resultMap = new HashMap<String, String>();
//        List<String> keyList = new ArrayList<String>(params.keySet());
//        for (String k : keyList) {
//            String[] value = params.get(k);
//            resultMap.put(k, value[0]);
//        }
//        String appId = resultMap.remove("appId");
//        String appSecret = (String) redisUtil.get(CodeConstant.REDIS_APP_KEY_PREFIX + appId);
//        if (StringUtils.isEmpty(appSecret)){
//            ShopApp appInfo=shopAppService.queryShopApp(appId);
//            if(null == appInfo){
//                logger.warn("应用信息不存在,签名校验失败");
//                return false;
//            }
//            appSecret = appInfo.getAppSecret();
//            //放入缓存
//            redisUtil.set(CodeConstant.REDIS_APP_KEY_PREFIX + appId,appSecret,CodeConstant.REDIS_SAVE_TIME_24h);
//        }
//        //获取基本信息
//        String sign=resultMap.remove("sign");
//        String substr="appId="+appId+"&time="+resultMap.remove("time")+appSecret;
//        //排序参数
//        keyList = new ArrayList<String>(resultMap.keySet());
//        Collections.sort(keyList);
//        StringBuilder sb = new StringBuilder();
//        //拼接参数
//        for (String k : keyList) {
//            String value = resultMap.get(k);
//            sb.append(k+"="+value+"&");
//        }
//        sb.append(substr);
//        //本地签名生成
//        String localSign= DigestUtils.md5Hex(sb.toString());
//        //比对签名
//        if(!StringUtils.equalsIgnoreCase(localSign, sign)){
//            logger.warn("组装签名数据:"+sb.toString() + ",签名校验失败,localSign:"+localSign+",sign:"+sign);
//            return false;
//        }
        return true;
    }

}
