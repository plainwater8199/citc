package com.citc.nce.messsagecenter.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.filecenter.platform.vo.ReceiveData;
import com.citc.nce.messsagecenter.service.MessageCenterService;
import com.citc.nce.messsagecenter.vo.MessageDataVo;
import com.citc.nce.messsagecenter.vo.WithdrawVo;
import com.citc.nce.robot.exception.MsgErrorCode;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import com.citc.nce.utils.Sha256Util;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MessageCenterServiceImpl implements MessageCenterService {

    @Value("${platform.sendMessageUrl}")
    private String sendMessageUrl;

    @Value("${platform.getToken_url}")
    private String getToken_url;

    @Value("${platform.revoke_url}")
    private String revoke_url;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @Override
    public MessageData sendMessage(Object obj, AccountManagementResp account) {
        return sendShortMessage(obj, account);
    }

    @SneakyThrows
    public MessageData sendShortMessage(Object obj, AccountManagementResp account) {
        String resultString = getResultString(obj, account, sendMessageUrl);
        return JSONObject.parseObject(resultString, MessageData.class);
    }

    @Override
    @SneakyThrows
    public boolean withdraw(MsgRecordDo msgRecordDo, AccountManagementResp account) {
        WithdrawVo withdrawVo = new WithdrawVo();
        List<String> phones = new ArrayList<>();
        String phoneNum = msgRecordDo.getPhoneNum();
        phones.add(phoneNum);
        withdrawVo.setDestinationAddress(phones);
        withdrawVo.setMessageId(msgRecordDo.getMessageId());
        String resultString = getResultString(withdrawVo, account, revoke_url);
        MessageDataVo messageData = JSONObject.parseObject(resultString, MessageDataVo.class);
        return messageData.getCode() == 0;
    }

    @SneakyThrows
    private String getResultString(Object obj, AccountManagementResp account, String url) throws URISyntaxException, IOException {
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(url);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        String jsonString = JSONObject.toJSONString(obj);
        log.info("发起网关请求 url: {}, body: {}", url, jsonString);
        String token = getToken(account.getAppId(), account.getAppKey());
        httpPost.addHeader("accessToken", token);
        HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("收到网关响应: {}" , resultString);
        return resultString;
    }

    private String getToken(String appId, String appKey) {
        String token;
        if (null == redisTemplate.opsForValue().get(appId)) {
            ReceiveData receiveData = getPlatformToken(appId, appKey);
            token = receiveData.getData().getToken();
            redisTemplate.opsForValue().set(appId, token, 2, TimeUnit.HOURS);
        } else {
            token = redisTemplate.opsForValue().get(appId);
        }
        return token;
    }

    @SneakyThrows
    public ReceiveData getPlatformToken(String appId, String appKey) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String timeStamp = sdf.format(now);
        String random = "123456aa";
        CloseableHttpClient client = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(getToken_url);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("nonce", timeStamp + random);
        String signature = Sha256Util.getSHA256(appId + appKey + timeStamp + random);
        paramMap.put("signature", signature);
        try {
            ReceiveData receiveData = getReceiveData(client, httpPost, paramMap);
            if (ObjectUtil.isEmpty(receiveData.getCode()) || receiveData.getCode() != 200) {
                throw new BizException(GlobalErrorCode.SERVER_BUSY.getCode(), "获取5G gateway token错误：");
            }
            return receiveData;
        } catch (BizException b) {
            throw b;
        } catch (Exception e) {
            log.error("获取token失败：", e);
            throw new BizException(GlobalErrorCode.SERVER_BUSY);
        }

    }

    private ReceiveData getReceiveData(CloseableHttpClient client, HttpPost httpPost, Map<String, String> paramMap) throws IOException {
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("获取token返回值：{}", string);
        return JSONObject.parseObject(string, ReceiveData.class);
    }

    /**
     * 获取https客户端
     *
     * @return https客户端
     */
    private CloseableHttpClient createSSLClientDefault() {
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
            sslContext.init(null, new TrustManager[]{x509mgr}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException(MsgErrorCode.CLIENT_ERROR);
        } catch (KeyManagementException e) {
            throw new BizException(MsgErrorCode.CLIENT_ERROR);
        }
    }
}
