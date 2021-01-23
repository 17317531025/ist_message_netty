package com.ist.message.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http请求基础工具类
 */
public class HttpUtil {
	private static Logger logger = LoggerFactory.getLogger("allLogger");
	public static String charset = "UTF-8";

	/**
	 * post 发送json
	 * @param reqUrl
	 * @param object
	 * @return
	 */
	public static String doPostJson(String reqUrl, Object object) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(reqUrl);
			ObjectMapper om = new ObjectMapper();
			String reqStr = om.writeValueAsString(object);
			httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
			httpPost.setEntity(new StringEntity(reqStr,"utf-8"));
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				result = EntityUtils.toString(resEntity);
				logger.info("Http响应码:" +statusCode + "|" + result);
				// 消耗掉response
				EntityUtils.consume(resEntity);
			}else {
				logger.info("Http响应码:" +statusCode);
			}
		}catch(Exception e) {
			logger.error("",e);
		} finally {
			HttpClientUtils.closeQuietly(httpclient);
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}
	
	/**
	 * 普通post请求
	 * @param reqUrl
	 * @param paramsMap
	 * @return
	 */
	public static String doPost(String reqUrl, Map<String, String> paramsMap) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(reqUrl);
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			if(paramsMap!=null) {
				Iterator<String> it = paramsMap.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					String value = paramsMap.get(key);
					nvps.add(new BasicNameValuePair(key,value));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				result = EntityUtils.toString(resEntity);
				logger.info("Http响应码:" +statusCode + "|" + result);
				// 消耗掉response
				EntityUtils.consume(resEntity);
			}else {
				logger.info("Http响应码:" +statusCode);
			}
		}catch(Exception e) {
			logger.error("",e);
		} finally {
			HttpClientUtils.closeQuietly(httpclient);
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}
	public static String doPost(String reqUrl, Map<String, String> paramsMap,String resultCharset,int socketTimeOut,int connectTimeOut) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(reqUrl);
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			if(paramsMap!=null) {
				Iterator<String> it = paramsMap.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					String value = paramsMap.get(key);
					nvps.add(new BasicNameValuePair(key,value));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).build();
			httpPost.setConfig(requestConfig);
			response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				result = EntityUtils.toString(resEntity,resultCharset);
				logger.info("Http响应码:" +statusCode + "|" + result);
				// 消耗掉response
				EntityUtils.consume(resEntity);
			}else {
				logger.info("Http响应码:" +statusCode);
			}
		}catch(Exception e) {
			logger.error("",e);
		} finally {
			HttpClientUtils.closeQuietly(httpclient);
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}
	/**
	 * 普通get请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doGet(String url, Map<String, String> params){
		if(params != null) {
			int i = 1;
			for (String s : params.keySet()) {
				String value = params.get(s);
				if (s!=null && value!=null) {
					if( i == 1){
						url +="?"+s+"="+params.get(s);
					}else{
						url +="&"+s+"="+params.get(s);
					}
					i++;
				}
			}
		}
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {			
			HttpGet httpGet = new HttpGet(url);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpGet.setConfig(requestConfig);
			response = httpclient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity resEntity = response.getEntity();
				result = EntityUtils.toString(resEntity);
				logger.info("Http响应码:" +statusCode + "|" + result);
				// 消耗掉response
				EntityUtils.consume(resEntity);
			}else {
				logger.info("Http响应码:" +statusCode);
			}
		}catch(Exception e) {
			logger.error("",e);
		} finally {
			HttpClientUtils.closeQuietly(httpclient);
			HttpClientUtils.closeQuietly(response);
		}
		return result;
	}
	public static String doXmlPost(String urlStr, String xmlStr) {
		byte[] xmlData = xmlStr.getBytes();
		BufferedReader br = null;
		HttpURLConnection urlCon = null;
		try {
			// 获得URL链接
			URL url = new URL(urlStr);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestMethod("POST");
			urlCon.setRequestProperty("Accept-Charset", charset);
			urlCon.setRequestProperty("Content-Type", "text/xml");
			urlCon.setRequestProperty("Content-length",String.valueOf(xmlData.length));
			DataOutputStream printout = new DataOutputStream(urlCon.getOutputStream());
			printout.write(xmlData);
			printout.flush();
			printout.close();
			//读取响应
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			String lines;
			StringBuffer sb = new StringBuffer("");
			while ((lines = br.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sb.append(lines + "\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("HttpClientUtil.doXmlPost exception:", e);
		}
		finally
		{
			try{
				br.close();
			}
			catch(Exception e){}
		}
		return null;
	}
}
