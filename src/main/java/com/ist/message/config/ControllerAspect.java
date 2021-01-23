package com.ist.message.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * web请求统一打印请求响应
 */
@Aspect
@Component
public class ControllerAspect {

	@Resource(name = "all_log")
	private Logger logger;
	@Pointcut("execution(public * com.ist.message.controller..*.*(..))")
	public void webReqLog(){}
	
	@Around("webReqLog()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
			long start = System.currentTimeMillis();
			 // 接收到请求，记录请求内容
			 ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			 HttpServletRequest request = attributes.getRequest();
			 // 记录下请求内容
//			 logger.info("URI : " + request.getRequestURI());
//			 logger.info("HTTP_METHOD : {},IP:{}",request.getMethod(),getIpAddr(request));
//			 logger.info("CLASS_METHOD : {}.{}",pjp.getSignature().getDeclaringTypeName(),pjp.getSignature().getName());
			 String requestStr = null;
			 if(pjp.getArgs()!=null && pjp.getArgs().length>0) {
				 for (int i = 0,len = pjp.getArgs().length; i < len; i++) {
					 Object o = pjp.getArgs()[i];
					 if(o instanceof HttpServletRequest) {
						 Map<String,String> params = new HashMap<String,String>(); //request.getParameterMap();
					        Enumeration<String> paramNames = request.getParameterNames();
					        while(paramNames.hasMoreElements()){
					            String name = paramNames.nextElement();
					            params.put(name, request.getParameter(name));
					        }
					        requestStr =  params.toString();
					        break;
					 }
				}
			 }else {
				 Map<String,String> params = new HashMap<String,String>(); //request.getParameterMap();
			     Enumeration<String> paramNames = request.getParameterNames();
			     while(paramNames.hasMoreElements()){
				     String name = paramNames.nextElement();
				     params.put(name, request.getParameter(name));
			     }
			     requestStr =  params.toString();
			 }
			 Object result = pjp.proceed();
			 long end = System.currentTimeMillis();
			 ObjectMapper om = new ObjectMapper();
			 String resp = om.writeValueAsString(result);
			if(requestStr!=null) {
				logger.info("URI:{},HTTP_METHOD:{},IP:{},CLASS_METHOD:{}.{},REQUEST:{},response:{},cost:{}",request.getRequestURI(),request.getMethod(),getIpAddr(request) ,pjp.getSignature().getDeclaringTypeName(),pjp.getSignature().getName(),requestStr,resp,(end - start));
			}else {
				logger.info("URI:{},HTTP_METHOD:{},IP:{},CLASS_METHOD:{}.{},REQUEST:{},response:{},cost:{}",request.getRequestURI(),request.getMethod(),getIpAddr(request),pjp.getSignature().getDeclaringTypeName(),pjp.getSignature().getName(),Arrays.toString(pjp.getArgs()),resp,(end - start));
			}
			 return result;
	 }
	 
	 public String getIpAddr(HttpServletRequest request) {
	        String ip = request.getHeader("x-forwarded-for");
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("Proxy-Client-IP");
	        }
	        if (ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
	            ip = request.getHeader("WL-Proxy-Client-IP");
	        }
	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	            ip = request.getRemoteAddr();
	        }
	        if(StringUtils.isNotBlank(ip)){
	        	String[] remoteIps = ip.split(",");
				String remoteIp = remoteIps[0];
				return remoteIp;
	        }
	        return ip;
	    }

}
