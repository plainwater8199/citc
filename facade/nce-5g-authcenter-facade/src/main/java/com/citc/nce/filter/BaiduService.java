package com.citc.nce.filter;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.configure.BaiduSensitiveCheckConfigure;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(BaiduSensitiveCheckConfigure.class)
public class BaiduService {

    private final BaiduSensitiveCheckConfigure baiduSensitiveCheckConfigure;

    @Resource
    RedisTemplate<String,String> redisTemplate;


    /**
     * 百度文本审核接口
     * @return 返回的json字符串
     */
    @SneakyThrows
    public JSONObject getBaiduToken(){
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(baiduSensitiveCheckConfigure.getAccessTokenUrl());
        uriBuilder.addParameter("grant_type", "client_credentials");
        uriBuilder.addParameter("client_id", baiduSensitiveCheckConfigure.getApiKey());
        uriBuilder.addParameter("client_secret", baiduSensitiveCheckConfigure.getSecretKey());
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        JSONObject jsonObject = JSONObject.parseObject(resultString);
        if (StringUtils.isEmpty(jsonObject.getString("access_token"))){
            throw new BizException(BaiduExp.TOKEN_ERROR);
        }
        return jsonObject;
    }


    @SneakyThrows
    public JSONObject textCensorUrl(String content){
        String token = getToken();
        String param = "text=" + URLEncoder.encode(content, "utf-8");
        String result = HttpUtil.post(baiduSensitiveCheckConfigure.getTextCensorUrl(), token, param);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (StringUtils.isEmpty(jsonObject.getString("conclusion"))){
            throw new BizException(BaiduExp.TEXT_ERROR);
        }
        return jsonObject;
    }


    @SneakyThrows
    public JSONObject imageCensor(byte[] imgData){
        String token = getToken();
        String encode = Base64Util.encode(imgData);
        String imgParam = URLEncoder.encode(encode, "UTF-8");
        String param = "image=" + imgParam;
        String result = HttpUtil.post(baiduSensitiveCheckConfigure.getImageCensorUrl(), token, param);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (StringUtils.isEmpty(jsonObject.getString("conclusion"))){
            throw new BizException(BaiduExp.TEXT_ERROR);
        }
        return jsonObject;
    }



    private String getToken(){
        String token = "";
        if (null == redisTemplate.opsForValue().get(baiduSensitiveCheckConfigure.getApiKey())){
            JSONObject receiveData = getBaiduToken();
            token = receiveData.getString("access_token");
            redisTemplate.opsForValue().set(baiduSensitiveCheckConfigure.getApiKey(),token,10, TimeUnit.HOURS);
        }else {
            token = redisTemplate.opsForValue().get(baiduSensitiveCheckConfigure.getApiKey());
        }
        return token;
    }



    /**
     * 获取https客户端
     * @return https客户端
     */
    private CloseableHttpClient createSSLClientDefault(){
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
            throw new BizException(BaiduExp.CLIENT_ERROR);
        }
    }

}
