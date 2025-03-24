package com.citc.nce.auth.messagetemplate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.entity.TemplateAuditReq;
import com.citc.nce.auth.messagetemplate.entity.TemplateDataResp;
import com.citc.nce.auth.mobile.exp.MobileExp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.HttpsUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * 对接网关模板相关接口
 */
@Component
@Slf4j
public class PlatfomManageTemplateService {

    @Value("${platform.createTemplate_url}")
    private String createTemplateUrl;

    @Value("${platform.updateTemplate_url}")
    private String updateTemplateUrl;

    @Value("${platform.deleteTemplate_url}")
    private String deleteTemplateUrl;

    @SneakyThrows
    public TemplateDataResp templateManage(Object obj, AccountManagementResp account, String url) {
        String resultString = getResultString(obj, account, url);
        return JSONObject.parseObject(resultString, TemplateDataResp.class);
    }

    public TemplateDataResp createTemplate(Object obj, AccountManagementResp account) {
        return templateManage(obj, account, createTemplateUrl);
    }

    public TemplateDataResp deleteTemplate(String id, AccountManagementResp account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        return templateManage(jsonObject, account, deleteTemplateUrl);
    }

    @Async("applicationTaskExecutor")
    public void deleteTemplateAsyn(String id, AccountManagementResp account) {
        try {
            TemplateDataResp templateDataResp = deleteTemplate(id, account);
            log.info("删除蜂动模板结果：{}，id:{}", JSONObject.toJSONString(templateDataResp), id);
        } catch (Exception e) {
            log.error("从蜂动删除模板出错", e);
        }
    }

    public TemplateDataResp updateTemplate(Object obj, AccountManagementResp account, String url) {
        return templateManage(obj, account, updateTemplateUrl);
    }

    public boolean isNotInstanceOf(Object obj, Class<?> clazz) {
        return !(clazz.isInstance(obj));
    }

    private String getResultString(Object obj, AccountManagementResp account, String url) throws URISyntaxException, IOException {
        CloseableHttpClient client = createSSLClientDefault();
        URIBuilder uriBuilder = new URIBuilder(url);
        HttpPost httpPost = new HttpPost(uriBuilder.build());
        String jsonString = "";
        if (isNotInstanceOf(obj, String.class)) {
            jsonString = JSONObject.toJSONString(obj);
        }else {
            jsonString = (String) obj;
        }
        log.info("请求体数据为： {}", jsonString);
        String timeStamp = Long.toString(System.currentTimeMillis());
        httpPost.addHeader("sign", getSign(account.getAppId(), account.getAppKey(), account.getAgentId(), timeStamp));
        httpPost.addHeader("appId", account.getAppId());
        httpPost.addHeader("openId", account.getAgentId());
        httpPost.addHeader("nonce", timeStamp);

        HttpEntity entity = new StringEntity(jsonString, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String resultString = EntityUtils.toString(responseEntity);
        log.info("发送至网关的结果为  " + resultString);
        client.close();
        return resultString;
    }

    @SneakyThrows
    public String getSign(String appId, String appKey, String agentId, String random) {
        return com.citc.nce.common.util.SHA256Utils.getSHA256(appId + agentId + appKey + random);

    }

    /**
     * 获取https客户端
     *
     * @return https客户端
     */
    private CloseableHttpClient createSSLClientDefault() {
        try {
            return HttpsUtil.createSSLClientDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new BizException(MobileExp.CLIENT_ERROR);
        } catch (KeyManagementException e) {
            throw new BizException(MobileExp.CLIENT_ERROR);
        }
    }

}
