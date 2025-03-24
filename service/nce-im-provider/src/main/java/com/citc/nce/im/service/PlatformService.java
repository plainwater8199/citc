package com.citc.nce.im.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.filecenter.platform.vo.ReceiveData;
import com.citc.nce.filecenter.vo.UploadReq;
import com.citc.nce.filecenter.vo.UploadResp;
import com.citc.nce.im.entity.RobotPhoneResult;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.group.Sha256Util;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.util.MyFileUtil;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.robot.vo.WithdrawReq;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.im.broadcast.utils.BroadcastConstants._5G_SENT_CODE_SUCCESS;

@Service
@Slf4j
public class PlatformService {

    @Value("${platform.sendMessageUrl}")
    private String sendMessageUrl;

    @Value("${platform.sendSupplierMessageUrl}")
    private String sendSupplierMessageUrl;

    @Value("${platform.getToken_url}")
    private String getToken_url;

    @Value("${platform.revoke_url}")
    private String revoke_url;

    @Value("${platform.download_url}")
    private String download_url;

    @Value("${platform.supplier_download_url}")
    private String supplier_download_url;

    @Resource
    FileApi fileApi;

    @Resource
    AccountManagementApi accountManagementApi;

    @Resource
    RedisTemplate<String, String> redisTemplate;

    @SneakyThrows
    public MessageData sendMessage(Object obj, AccountManagementResp account) {
        if (SupplierConstant.OWNER.equals(account.getSupplierTag())) {
            return JSONObject.parseObject(getResultString(obj, account, sendMessageUrl), MessageData.class);
        } else if (SupplierConstant.FONTDO.equals(account.getSupplierTag())) {
            return JSONObject.parseObject(getSupplierResultString(obj, account, sendSupplierMessageUrl), MessageData.class);
        }//以后的供应商
        return null;
    }

    //通知网关发送供应商消息
    public String getSupplierResultString(Object obj, AccountManagementResp account, String url) throws URISyntaxException, IOException, NoSuchAlgorithmException {
        log.info("请求体数据为： {}", JSON.toJSONString(obj));
        String agentId = account.getAgentId();
        String appId = account.getAppId();
        String once = Long.toString(System.currentTimeMillis());
        String input = appId + agentId + account.getAppKey() + once;
        String sign = DigestUtil.sha256Hex(input);
        String result = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("openId", agentId)
                .header("appId", appId)
                .header("nonce", once)
                .header("sign", sign)
                .body(JSON.toJSONString(obj))
                .execute().body();
        log.info("发送至网关的结果为  " + result);
        return result;
    }

    //通知网关发送运营商消息
    private String getResultString(Object obj, AccountManagementResp account, String url) throws URISyntaxException, IOException {
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(url);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        String jsonString = JSONObject.toJSONString(obj);
        log.info("发起网关请求：url: {} body: {}", url, jsonString);
        String token = getToken(account.getAppId(), account.getAppKey());
        httpPost.addHeader("accessToken", token);
        HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("收到网关响应: {}", resultString);
        return resultString;
    }

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
        log.info("获取token url:{}", getToken_url);
        ReceiveData receiveData = null;
        try {
            receiveData = getReceiveData(client, httpPost, paramMap);
        } catch (IOException e) {
            log.error("获取token失败:{}", e.getMessage(), e);
        }
        if (receiveData == null || ObjectUtil.isEmpty(receiveData.getCode()) || receiveData.getCode() != 200) {
            throw new BizException(GlobalErrorCode.SERVER_BUSY.getCode(), "获取5G gateway token错误：");
        }
        return receiveData;
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

    private String getToken(String appId, String appKey) {
        String token = "";
        if (null == redisTemplate.opsForValue().get(appId)) {
            ReceiveData receiveData = getPlatformToken(appId, appKey);
            token = receiveData.getData().getToken();
            redisTemplate.opsForValue().set(appId, token, 2, TimeUnit.HOURS);
        } else {
            token = redisTemplate.opsForValue().get(appId);
        }
        return token;
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
            throw new BizException(SendGroupExp.CLIENT_ERROR);
        } catch (KeyManagementException e) {
            throw new BizException(SendGroupExp.CLIENT_ERROR);
        }
    }

    @SneakyThrows
    public boolean withdraw(RobotPhoneResult phoneResult, AccountManagementResp account) {
        WithdrawReq withdrawReq = new WithdrawReq();
        List<String> phones = new ArrayList<>();
        String phoneNum = phoneResult.getPhoneNum();
        phones.add(phoneNum);
        withdrawReq.setDestinationAddress(phones);
        withdrawReq.setMessageId(phoneResult.getMessageId());
        String resultString = getResultString(withdrawReq, account, revoke_url);
        MessageData messageData = JSONObject.parseObject(resultString, MessageData.class);
        return messageData.getCode() == _5G_SENT_CODE_SUCCESS;
    }

    @SneakyThrows
    public List<String> downloadFromPlatform(String fileTid, String chatbotId, String fileFormat) {
        List<String> result = downloadFromPlatformGetFileInfo(fileTid, chatbotId, fileFormat).stream().map(UploadResp::getUrlId).collect(Collectors.toList());
        return result;
    }

    @SneakyThrows
    public List<UploadResp> downloadFromPlatformGetFileInfo(String fileTid, String chatbotId, String fileFormat) {
        log.info("downloadFromPlatformGetFileInfo");
        log.info("用户上行的文件tid为 {}", fileTid);
        log.info("用户上行的文件chatbotId为 {}", chatbotId);
        AccountManagementResp accountResp = accountManagementApi.getAccountManagementByAccountId(chatbotId);
        CloseableHttpClient client = createSSLClientDefault();
        HttpPost httpPost = null;
        if (Objects.equals(CSPChatbotSupplierTagEnum.OWNER.getValue(), accountResp.getSupplierTag())) {
            String token = getToken(accountResp.getAppId(), accountResp.getAppKey());
            httpPost = new HttpPost(download_url);
            //accessToken
            httpPost.addHeader("accessToken", token);
        } else {
            httpPost = new HttpPost(supplier_download_url);
            String nonce = String.valueOf(DateUtil.currentSeconds());
            String signStr = DigestUtil.sha256Hex(accountResp.getAppId() + accountResp.getAgentId() + accountResp.getAppKey() + nonce);
            httpPost.addHeader("openId", accountResp.getAgentId());
            httpPost.addHeader("appId", accountResp.getAppId());
            httpPost.addHeader("nonce", nonce);
            httpPost.addHeader("sign", signStr);
        }
        Map<String, String> map = new HashMap<>();
        //请求体
        map.put("tid", fileTid);
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(map), ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        response.getEntity().writeTo(bos);
        byte[] bytes = bos.toByteArray();
        //接收的二进制转化为为文件
        File file = MyFileUtil.bytesToFile(bytes, fileFormat);
        CommonsMultipartFile multipartFile = MyFileUtil.getMultipartFile(file);
        UploadReq uploadReq = new UploadReq();
        uploadReq.setFile(multipartFile);
        //根据chatbotId查找用户名
        uploadReq.setCreator(accountResp.getCreator());
        uploadReq.setSceneId("codeincodeservice");
        List<UploadResp> result = fileApi.uploadFile(uploadReq);
        log.info("上传到Minio的文件uuid集合为 {}", result);
        if (CollectionUtils.isEmpty(result)) {
            throw new BizException(SendGroupExp.FILE_UPLOAD_ERROR);
        }
        return result;
    }

}
