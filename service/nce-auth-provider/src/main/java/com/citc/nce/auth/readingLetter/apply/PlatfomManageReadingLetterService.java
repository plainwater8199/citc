package com.citc.nce.auth.readingLetter.apply;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
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
 * 对接网关阅信模板及短链申请/删除相关接口
 */
@Component
@Slf4j
public class PlatfomManageReadingLetterService {

    @Value("${platform.createYxTemplate_url}")
    private String createReadingLetterTemplateUrl;

    @Value("${platform.deleteYxTemplate_url}")
    private String deleteReadingLetterTemplateUrl;

    @Value("${platform.applyShortUrlAddress}")
    private String applyShortUrlAddress;

    @Value("${platform.queryShortUrlAddress}")
    private String queryShortUrlAddress;

    @SneakyThrows
    public TemplateDataResp templateManage(Object obj, AccountManagementResp account, String url) {
        String resultString = getResultString(obj, account, url);
        return JSONObject.parseObject(resultString, TemplateDataResp.class);
    }

    //新增阅信
    public TemplateDataResp createReadingLetterTemplate(Object obj, AccountManagementResp account) {
        return templateManage(obj, account, createReadingLetterTemplateUrl);
    }

    //删除阅信
    public TemplateDataResp deleteReadingLetterTemplate(String id, AccountManagementResp account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        return templateManage(jsonObject, account, deleteReadingLetterTemplateUrl);
    }


    //新增阅信+短链
    public TemplateDataResp createReadingLetterShortUrl(Object obj, AccountManagementResp account) {
        return templateManage(obj, account, applyShortUrlAddress);
    }

    //查询短链
    public TemplateDataResp queryReadingLetterShortUrl(String id, AccountManagementResp account) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        return templateManage(jsonObject, account, queryShortUrlAddress);
    }

    @Async("applicationTaskExecutor")
    public void deleteTemplateAsyn(String id, AccountManagementResp account) {
        try {
            TemplateDataResp templateDataResp = deleteReadingLetterTemplate(id, account);
            log.info("删除蜂动模板结果：{}，id:{}", JSONObject.toJSONString(templateDataResp), id);
        } catch (Exception e) {
            log.error("从蜂动删除模板出错", e);
        }
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
        log.info("请求地址为： {}", url);
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
