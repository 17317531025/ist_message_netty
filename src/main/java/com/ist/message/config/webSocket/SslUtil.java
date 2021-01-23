package com.ist.message.config.webSocket;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @Author: sunhaitao
 */
public class SslUtil {
    public static SSLContext createSSLContext(String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS"); /// "JKS"
        InputStream ksInputStream = new FileInputStream("D:\\program files (x86)\\java\\jdk1.8.0_131\\bin\\3483632_brofarm.cn.jks"); /// 证书存放地址
        ks.load(ksInputStream, password.toCharArray());
        //KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
        kmf.init(ks, password.toCharArray());
        //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }

}
