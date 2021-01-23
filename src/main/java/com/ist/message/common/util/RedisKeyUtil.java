package com.ist.message.common.util;


import com.ist.message.common.CodeConstant;

/**
 * @Author: ligun
 */
public class RedisKeyUtil {
    public static String getUIDWebSocketURL(String userID){
        String redisKey = CodeConstant.REDIS_USER_USERID_SOCKET_SESSION_REL_PREFIX + userID;
        return redisKey;
    }
}
