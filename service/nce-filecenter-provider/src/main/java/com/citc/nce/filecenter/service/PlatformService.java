package com.citc.nce.filecenter.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.filecenter.configure.PlatformUrlConfigure;
import com.citc.nce.filecenter.exp.FileExp;
import com.citc.nce.filecenter.platform.vo.ReceiveData;
import com.citc.nce.filecenter.platform.vo.UpReceiveData;
import com.citc.nce.filecenter.util.Sha256Util;
import com.citc.nce.filecenter.vo.UploadResp;
import jodd.util.RandomString;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(PlatformUrlConfigure.class)
public class PlatformService {


    private final PlatformUrlConfigure platformUrlConfigure;

    @SneakyThrows
    public UpReceiveData upToConsumer(File file, String token, File thumbnail) {
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(platformUrlConfigure.getUploadUrl());
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.addHeader("accessToken", token);
        httpPost.addHeader("fileType", "media");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);
        if (ObjectUtil.isNotEmpty(thumbnail)) {
            builder.addBinaryBody("thumbnail", thumbnail);
        }
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("platform upload file result is {}", resultString);
        UpReceiveData upReceiveData = JSONObject.parseObject(resultString, UpReceiveData.class);
        if (upReceiveData.getCode() != 200) {
            throw new BizException(820201008, upReceiveData.getMessage());
        }
        return upReceiveData;
    }

    @SneakyThrows
    public UpReceiveData upToSupplier(File file, Boolean isThumbNail, String openId, String appId, String nonce, String secret) {
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(platformUrlConfigure.getSupplierUploadUrl());
        String signStr = DigestUtil.sha256Hex(appId + openId + secret + nonce);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.addHeader("openId", openId);
        httpPost.addHeader("appId", appId);
        httpPost.addHeader("nonce", nonce);
        httpPost.addHeader("sign", signStr);
        httpPost.addHeader("fileType", "media");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);
        builder.addTextBody("isThumbNail", String.valueOf(isThumbNail));
        builder.setCharset(StandardCharsets.UTF_8);
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("platform - supplier upload file result is {}", resultString);
        UpReceiveData upReceiveData = JSONObject.parseObject(resultString, UpReceiveData.class);
        if (upReceiveData.getCode() != 200) {
            // throw new BizException(FileExp.PLATFORM_ERROR);
            throw new BizException(upReceiveData.getMessage());
        }
        return upReceiveData;
    }

    @SneakyThrows
    public void examine(UploadResp uploadResp, String url) {
        try (DefaultHttpClient httpClient = new DefaultHttpClient()) {
            HttpPost httpPost = new HttpPost(url);
            Map<String, String> param = new HashMap<>();
            param.put("type", "media");
            param.put("", "");
            HttpEntity entity = new StringEntity(JSONObject.toJSONString(param), ContentType.create("application/json", "utf-8"));
            httpPost.setEntity(entity);
            httpPost.addHeader("signature", "");
            httpPost.addHeader("timestamp", System.currentTimeMillis() + "");
            httpPost.addHeader("signature", "");
            httpClient.execute(httpPost);
        }
    }

    @SneakyThrows
    public ReceiveData getToken(String appId, String appKey) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String timeStamp = sdf.format(now);
        RandomString randomString = new RandomString();
        String random = randomString.randomAlpha(8);
        CloseableHttpClient client = createSSLClientDefault();
        HttpPost httpPost = new HttpPost(platformUrlConfigure.getGetTokenUrl());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appId", appId);
        paramMap.put("nonce", timeStamp + random);
        String signature = Sha256Util.getSHA256(appId + appKey + timeStamp + random);
        paramMap.put("signature", signature);
        ReceiveData receiveData = getReceiveData(client, httpPost, paramMap);
        if (receiveData.getCode() != 200) {
            throw new BizException(FileExp.TOKEN_ERROR);
        }
        return receiveData;
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
        } catch (Exception e) {
            throw new BizException(FileExp.CLIENT_ERROR);
        }
    }

    /**
     * 平台侧文件删除
     *
     * @param tid   文件tid
     * @param token token
     */
    @SneakyThrows
    public void deletedFile(String tid, String token) {
        log.info("删除平台测文件id {}", tid);
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(platformUrlConfigure.getDeleteUrl());
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.addHeader("accessToken", token);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("tid", tid);
        ReceiveData receiveData = getReceiveData(client, httpPost, paramMap);
        if (StringUtils.equals(receiveData.getMessage(), "文件还未审核通过不能删除")) {
            throw new BizException(FileExp.DELETE_EXAMINE_ERROR);
        }
        if (receiveData.getCode() != 0 && receiveData.getCode() != 501) {// 501表示临时文件已过期,不需要删除
            throw new BizException(FileExp.DELETE_ERROR);
        }
    }

    /**
     * 平台侧文件删除
     *
     * @param fileId 文件id
     */
    @SneakyThrows
    public void deletedSupplierFile(String fileId, Boolean isThumbNail, String openId, String appId, String nonce, String secret) {
        log.info("删除供应商平台测文件id {}", fileId);
        CloseableHttpClient client = createSSLClientDefault();
        String signStr = DigestUtil.sha256Hex(appId + openId + secret + nonce);
        URIBuilder uriBuilder = new URIBuilder(platformUrlConfigure.getSupplierDeleteUrl());
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        httpPost.addHeader("openId", openId);
        httpPost.addHeader("appId", appId);
        httpPost.addHeader("nonce", nonce);
        httpPost.addHeader("sign", signStr);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("tid", fileId);
        paramMap.put("isThumbNail", String.valueOf(isThumbNail));
        ReceiveData receiveData = getReceiveData(client, httpPost, paramMap);
        if (receiveData.getCode() != 0) {
            throw new BizException(FileExp.DELETE_ERROR);
        }
    }

    private ReceiveData getReceiveData(CloseableHttpClient client, HttpPost httpPost, Map<String, String> paramMap) throws IOException {
        HttpEntity entity = new StringEntity(JSONObject.toJSONString(paramMap), ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("receive body is {}", string);
        return JSONObject.parseObject(string, ReceiveData.class);
    }
}
