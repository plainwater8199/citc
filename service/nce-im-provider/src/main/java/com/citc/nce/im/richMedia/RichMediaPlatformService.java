package com.citc.nce.im.richMedia;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.csp.mediasms.template.MediaSmsTemplateApi;
import com.citc.nce.auth.csp.mediasms.template.enums.AuditStatus;
import com.citc.nce.auth.csp.mediasms.template.enums.OperatorPlatform;
import com.citc.nce.auth.csp.mediasms.template.vo.MediaSmsTemplateAuditUpdateVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.robot.req.RichMediaResult;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.res.RichMediaSendParam;
import com.citc.nce.robot.res.RichMediaSendParamRes;
import com.citc.nce.robot.vo.TemplateReq;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 8.0富媒体平台
 */
@Service
@Slf4j
public class RichMediaPlatformService {


    @Value("${richMediaPlatform.baseUrl}")
    private String baseUrl;

    @Value("${richMediaPlatform.templateReportUrl}")
    private String templateReportUrl;

    @Value("${richMediaPlatform.messageSendUrl}")
    private String messageSendUrl;

    @Value("${richMediaPlatform.sendPnyUrl}")
    private String sendPnyUrl;

    @Value("${richMediaPlatform.updateTemplateStatusUrl}")
    private String updateTemplateStatusUrl;

    @Autowired
    private MediaSmsTemplateApi templateApi;


    @SneakyThrows
    public RichMediaResultArray messageSend(RichMediaSendParamRes richMediaSendParamRes) {
        RichMediaSendParam richMediaSendParam = richMediaSendParamRes.getRichMediaSendParam();
        CloseableHttpClient client = createSSLClientDefault();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("mobile", richMediaSendParam.getMobile()));
        params.add(new BasicNameValuePair("templateNumber", richMediaSendParam.getTemplateNumber()));
        String exeUrl = baseUrl + messageSendUrl;
        HttpPost httpPost = new HttpPost(exeUrl);
        buildHeader(richMediaSendParamRes.getAppId(), richMediaSendParamRes.getAppSecret(), httpPost);
        log.info("发送富媒体请求信息：{}", params);
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("receive body is {}", string);
        return JSONObject.parseObject(string, RichMediaResultArray.class);
    }

    @SneakyThrows
    public RichMediaResultArray sendPny(RichMediaSendParamRes richMediaSendParamRes) {
        RichMediaSendParam richMediaSendParam = richMediaSendParamRes.getRichMediaSendParam();
        CloseableHttpClient client = createSSLClientDefault();
        String requestBody = JSONObject.toJSONString(richMediaSendParam);
        HttpEntity entity = new StringEntity(requestBody, ContentType.create("application/json", "utf-8"));
        String exeUrl = baseUrl + sendPnyUrl;
        HttpPost httpPost = new HttpPost(exeUrl);
        buildHeader(richMediaSendParamRes.getAppId(), richMediaSendParamRes.getAppSecret(), httpPost);
        log.info("发送富媒体请求信息：{}", requestBody);
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("receive body is {}", string);
        return JSONObject.parseObject(string, RichMediaResultArray.class);
    }


    @SneakyThrows
    public void updateTemplateStatus(String templateNumber, String appId, String appSecret) {
        CloseableHttpClient client = createSSLClientDefault();
        String exeUrl = baseUrl + updateTemplateStatusUrl;
        HttpPost httpPost = new HttpPost(exeUrl);
        buildHeader(appId, appSecret, httpPost);
        UpdateTemplateReq updateTemplateReq = new UpdateTemplateReq();
        updateTemplateReq.setTemplateNumber(templateNumber);
        updateTemplateReq.setState(3);
        String requestBody = JSONObject.toJSONString(updateTemplateReq);
        log.info("删除模板请求信息：{}", requestBody);
        HttpEntity entity = new StringEntity(requestBody, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        RichMediaResult richMediaResult = getRichMediaResult(response);
        if (!richMediaResult.getSuccess()) {
            throw new BizException(820501009, richMediaResult.getMessage());
        }
    }

    @SneakyThrows
    public String templateReport(TemplateReq templateReq) {
        CloseableHttpClient client = createSSLClientDefault();
        String exeUrl = baseUrl + templateReportUrl;
        HttpPost httpPost = new HttpPost(exeUrl);
        buildHeader(templateReq.getAppId(), templateReq.getAppSecret(), httpPost);
        String requestBody = JSONObject.toJSONString(templateReq);
        log.info("报备模板请求信息：{}", requestBody);
        HttpEntity entity = new StringEntity(requestBody, ContentType.create("application/json", "utf-8"));
        httpPost.setEntity(entity);
        HttpResponse response = client.execute(httpPost);
        RichMediaResult richMediaResult = getRichMediaResult(response);
        if (!richMediaResult.getSuccess()) {
            throw new BizException(820501009, richMediaResult.getMessage());
        }
        return richMediaResult.getResult().getString("templateNumber");
    }

    /**
     * 亿美软通富媒体模板报备状态回调
     */
    public String eMayTemplateAuditCallback(TemplateAuditCallbackVo callbackVo) {
        MediaSmsTemplateAuditUpdateVo auditUpdateVo = new MediaSmsTemplateAuditUpdateVo();
        auditUpdateVo.setPlatformTemplateId(callbackVo.getTemplateNumber());
        auditUpdateVo.setAudits(Arrays.asList(
                new MediaSmsTemplateAuditUpdateVo.TemplateAudit(OperatorPlatform.CMCC, AuditStatus.byValue(callbackVo.getCmccState()), callbackVo.getCmccStateReason()),
                new MediaSmsTemplateAuditUpdateVo.TemplateAudit(OperatorPlatform.CUCC, AuditStatus.byValue(callbackVo.getCuccState()), callbackVo.getCuccStateReason()),
                new MediaSmsTemplateAuditUpdateVo.TemplateAudit(OperatorPlatform.CTCC, AuditStatus.byValue(callbackVo.getCtccState()), callbackVo.getCtccStateReason())
        ));
        templateApi.updateAuditStatus(auditUpdateVo);
        return "SUCCESS";
    }

    private RichMediaResult getRichMediaResult(HttpResponse response) throws IOException {
        HttpEntity responseEntity = response.getEntity();
        String string = EntityUtils.toString(responseEntity);
        log.info("receive body is {}", string);
        return JSONObject.parseObject(string, RichMediaResult.class);
    }


    private void buildHeader(String appId, String appSecret, HttpPost httpPost) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = sdf.format(now);
        httpPost.addHeader("AUTH_APPID", appId);
        httpPost.addHeader("AUTH_TIMESTAMP", timeStamp);
        String signatureStr = appId + appSecret + timeStamp;
        String signature = DigestUtils.md5Hex((signatureStr).getBytes());
        httpPost.addHeader("AUTH_SIGN", signature);
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


}
