package com.citc.nce.auth.csp.menu.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.configure.MenuPlatformUrlConfigure;
import com.citc.nce.auth.utils.SHA256Utils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.filecenter.platform.vo.ReceiveData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(MenuPlatformUrlConfigure.class)
public class PlatformService {

    @Resource
    RedisTemplate<String,String> redisTemplate;

    private final MenuPlatformUrlConfigure menuPlatformUrlConfigure;

    @SneakyThrows
    public MessageData updateMenu(Object obj, AccountManagementResp account){
        String resultString = getResultString(obj, account,menuPlatformUrlConfigure.getUpdateMenuUrl());
        return JSONObject.parseObject(resultString, MessageData.class);
    }

    private String getResultString(Object obj, AccountManagementResp account, String url) throws URISyntaxException, IOException {
        log.info("同步菜单数据：" + obj);
        CloseableHttpClient client = createSSLClientDefault();
        log.info("向 "+ account.getAccountType() + " 运营商同步菜单");
        URIBuilder uriBuilder = new URIBuilder(url);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        String jsonString = JSONObject.toJSONString(obj);
        log.info("请求体数据为： {}",jsonString);
        String token = getToken(account.getAppId(), account.getAppKey());
        httpPost.addHeader("accessToken",token);
        HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("同步菜单的结果为  " + resultString);
        return resultString;
    }


    @SneakyThrows
    public ReceiveData getPlatformToken(String appId,String appKey) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String timeStamp = sdf.format(now);
        String random = "123456aa";
        CloseableHttpClient client = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(menuPlatformUrlConfigure.getGetTokenUrl());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appId",appId);
        paramMap.put("nonce",timeStamp+random);
        String signature = SHA256Utils.getSHA256(appId + appKey + timeStamp + random);
        paramMap.put("signature",signature);
        ReceiveData receiveData = getReceiveData(client, httpPost, paramMap);
        if (ObjectUtil.isEmpty(receiveData.getCode()) || receiveData.getCode() != 200){
            throw new BizException(GlobalErrorCode.SERVER_BUSY.getCode(),"获取5G gateway token错误：");
        }
        return receiveData;
    }

    private ReceiveData getReceiveData(CloseableHttpClient client, HttpPost httpPost, Map<String, String> paramMap) throws IOException {
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        return JSONObject.parseObject(string, ReceiveData.class);
    }

    private String getToken(String appId,String appKey){
        log.info("appId is {}  appKey is {}",appId,appKey);
        String token = "";
        if (null == redisTemplate.opsForValue().get(appId)){
            ReceiveData receiveData = getPlatformToken(appId,appKey);
            token = receiveData.getData().getToken();
            redisTemplate.opsForValue().set(appId,token,2, TimeUnit.HOURS);
        }else {
            token = redisTemplate.opsForValue().get(appId);
        }
        return token;
    }

    /**
     * 获取https客户端
     * @return https客户端
     */
    private  CloseableHttpClient createSSLClientDefault(){
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
            throw new BizException(820103015,"客户端创建失败");
        }
    }


}
