package com.citc.nce.misc.utils;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.exp.MiscExp;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/7 16:11
 * @Version 1.0
 * @Description:
 */
public class HttpsUtil {


    private static final Logger log = LoggerFactory.getLogger(HttpsUtil.class);

    //绕过证书
    public static HttpClient wrapClient() {
        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(20000)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(
                    ctx, NoopHostnameVerifier.INSTANCE);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(ssf)
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();
            return httpclient;
        } catch (Exception e) {
            e.printStackTrace();
            return HttpClients.createDefault();
        }
    }

    /**
     * https请求，跳过ssl认证
     *
     * @param url
     * @param param
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String post(String url, String param) throws ClientProtocolException, IOException {
        CloseableHttpClient httpclient = (CloseableHttpClient) wrapClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Accept-Encoding", "");
        httpPost.setEntity(new StringEntity(param, "utf-8"));
        log.info("多因子短信发送接口url:{}", url);
        log.info("多因子短信发送接口参数{}", param);
        CloseableHttpResponse response = httpclient.execute(httpPost);
        String jsonstr = EntityUtils.toString(response.getEntity());
        log.info("多因子短信发送接口返回结果{}", jsonstr);
        log.info("addResource result is : " + jsonstr + "\n");
        return jsonstr;
    }

    /**
     * 获取https客户端
     * @return https客户端
     */
    public static CloseableHttpClient createSSLClientDefault(){
        try {
            X509TrustManager x509mgr = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {
                }
                public void checkServerTrusted(X509Certificate[] xcs, String string) {
                }
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { x509mgr }, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException(MiscExp.CLIENT_ERROR);
        }
    }
}
