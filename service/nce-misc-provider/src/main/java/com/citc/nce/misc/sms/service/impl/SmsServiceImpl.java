package com.citc.nce.misc.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.exp.MiscExp;
import com.citc.nce.misc.sms.configure.DyzConfigure;
import com.citc.nce.misc.sms.service.SmsService;
import com.citc.nce.misc.sms.vo.req.SendSmsInfo;
import com.citc.nce.misc.sms.vo.resp.SendSmsResp;
import com.citc.nce.misc.utils.HttpsUtil;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashMap;

@Service
@Slf4j
@RefreshScope
@RequiredArgsConstructor
@EnableConfigurationProperties(DyzConfigure.class)
public class SmsServiceImpl implements SmsService {


    private final DyzConfigure dyzConfigure;


    @Override
    public SendSmsResp sendSms(SendSmsInfo smsInfo) {
        SendSmsResp sendSmsResp = new SendSmsResp();
        String mobile = smsInfo.getMobile();
        String content = smsInfo.getContent();
        if (!Strings.isNullOrEmpty(mobile) && mobile.length() == 11) {
            try (CloseableHttpClient client = HttpsUtil.createSSLClientDefault()){
                /**
                 *  统一短信发送接口
                 * */
                HashMap<String, Object> map = new HashMap<>();
                map.put("app_id", dyzConfigure.getAppId());
                map.put("app_secret", dyzConfigure.getAppSecret());
                map.put("sms_type", 0);
                map.put("mobile", mobile);
                map.put("content", content);
                log.warn("统一短信短信发送接口请求：{}", JsonUtils.obj2String(map));
                log.info("发送地址为：{}",dyzConfigure.getSendSms());
                HttpPost httpPost = new HttpPost(dyzConfigure.getSendSms());
                HttpEntity entity = new StringEntity(JSONObject.toJSONString(map), ContentType.create("application/json", "utf-8"));
                httpPost.setEntity(entity);
                HttpResponse response = client.execute(httpPost);
                HttpEntity responseEntity = response.getEntity();
                String string = EntityUtils.toString(responseEntity);
                Integer code = JSON.parseObject(string).getInteger("code");
                log.info("短信发送接口返回：{}", string);
                sendSmsResp.setResultMsg(string);
                sendSmsResp.setSuccess(code == 0);
            }catch (Exception e){
                log.error("发送短信失败",e);
                throw new BizException(MiscExp.SEND_ERROR);
            }

        }
        return sendSmsResp;
    }


}
