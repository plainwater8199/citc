package com.citc.nce.aim.utils.api;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.config.AimTestConfig;

import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.utils.api.vo.SmsTestRequest;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Component
@Log4j2
@RequiredArgsConstructor
@EnableConfigurationProperties(AimTestConfig.class)
public class AimCallUtils {
    private final AimTestConfig aimTestConfig;

    public void doValidate(String accessKey, String accessSecret) {
//        String status;
//        int resCode;
//        try {
//            String validateAccess = validateAccess(accessKey, accessSecret);
//            log.info("#### AimProjectServiceImpl validateAccess {}", validateAccess);
//            String[] resultInfo = validateAccess.split("~");
//            status = resultInfo[0];
//            String responseInfoStr = resultInfo[1];
//            JSONObject jsonObject = JsonUtils.string2Obj(responseInfoStr, JSONObject.class);
//            resCode = jsonObject.getInteger("code");
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            throw new BizException(AimError.VALIDATE_ACCESS_SERV_ERROR);
//        }
//        if (AimConstant.VALIDATE_ACCESS_SUCCESS.equals(status)) {
//            if (AimConstant.VALIDATE_ACCESS_ERROR == resCode) {
//                throw new BizException(AimError.VALIDATE_ACCESS_ERROR);
//            } else if (AimConstant.VALIDATE_ACCESS_LIMIT == resCode) {
//                throw new BizException(AimError.VALIDATE_ACCESS_LIMIT);
//            } else if (AimConstant.SMS_SERVER_FAILURE == resCode) {
//                throw new BizException(AimError.SMS_SERVER_FAILURE);
//            }
//        } else {
//            throw new BizException(AimError.VALIDATE_ACCESS_SERV_ERROR);
//        }
    }

    /**
     * 生成项目Id
     * AIM + yyyyMMdd + Id数字(9位）
     * AIM20230608000000000(20位）
     *
     * @return string
     */
    public String generateProjectId() {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(pattern);
        return AimConstant.PROJECT_ID_PREFIX + localDate;
    }



    public static Map<String, List<String>> getHeaderValueMap(HttpServletRequest request) {
        Map<String, List<String>> headerMap = new HashMap<>();
        String headerName;
        Enumeration<String> headerValue;
        List<String> arr;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            headerValue = request.getHeaders(headerName);
            arr = new ArrayList<>();
            while (headerValue.hasMoreElements()) {
                arr.add(headerValue.nextElement());
            }
            headerMap.put(headerName, arr);
        }
        return headerMap;
    }

    public String validateAccess(String accessKey, String accessSecret) throws Exception {
        String url = aimTestConfig.getValidateAccessUrl();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessKey", accessKey);
        jsonObject.put("accessSecret", accessSecret);
        String body = JSON.toJSONString(jsonObject);
        log.info("AIM通道验证地址：{}",url);
        String timestamp = DateUtils.obtainDateStr(new Date(),"yyyyMMddHHmmss");
        String signStr = "myaim"+timestamp;
        String signature = DigestUtils.sha256Hex(signStr);
        URI newUri =  new URI(url);
        HttpMethod httpMethod = HttpMethod.resolve("POST");
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(20000);
        ClientHttpRequest call = clientHttpRequestFactory.createRequest(newUri, httpMethod);
        StreamUtils.copy(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), call.getBody());
        HttpHeaders headers = call.getHeaders();
        headers.add("Content-Type","application/json");
        headers.add("timestamp",timestamp);
        headers.add("signature",signature);
        ClientHttpResponse clientHttpResponse = call.execute();
        int status = clientHttpResponse.getStatusCode().value();
        String response = StreamUtils.copyToString(new ByteArrayInputStream(obtainBodyInputStream(clientHttpResponse).toByteArray()), StandardCharsets.UTF_8);
        return status+"~"+response;
    }

    public String callSMS(String calling, List<String> phones) throws Exception {
        String url = aimTestConfig.getUrl();
        StringBuilder calledSB = new StringBuilder();
        for(String item : phones){
            if(calledSB.length() == 0){
                calledSB.append(item);
            }else{
                calledSB.append(",").append(item);
            }
        }
        String called = calledSB.toString();
        SmsTestRequest request = new SmsTestRequest();
        request.setCalled(called);
        request.setCalling(calling);
        String body = JSON.toJSONString(request);
        log.info("AIM测试地址：{}",url);
        String timestamp = DateUtils.obtainDateStr(new Date(),"yyyyMMddHHmmss");
        String signStr = "myaim"+timestamp;
        String signature = DigestUtils.sha256Hex(signStr);
        URI newUri =  new URI(url);
        HttpMethod httpMethod = HttpMethod.resolve("POST");
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(20000);
        ClientHttpRequest call = clientHttpRequestFactory.createRequest(newUri, httpMethod);
        StreamUtils.copy(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)), call.getBody());
        HttpHeaders headers = call.getHeaders();
        headers.add("Content-Type","application/json");
        headers.add("timestamp",timestamp);
        headers.add("signature",signature);
        ClientHttpResponse clientHttpResponse = call.execute();
        int status = clientHttpResponse.getStatusCode().value();
        String response = StreamUtils.copyToString(new ByteArrayInputStream(obtainBodyInputStream(clientHttpResponse).toByteArray()), StandardCharsets.UTF_8);
        return status+"~"+response;

    }

    private static ByteArrayOutputStream obtainBodyInputStream(ClientHttpResponse clientHttpResponse) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            InputStream body = clientHttpResponse.getBody();
            HttpHeaders headers = clientHttpResponse.getHeaders();
            InputStream rawInputStream;
            boolean isGzip = false;
            for (Map.Entry<String, List<String>> item : headers.entrySet()) {
                if (item.getValue().contains("gzip")) {
                    //For GZip response
                    isGzip = true;
                    break;
                }
            }
            if(!isGzip){
                rawInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(clientHttpResponse.getBody()));
            }else{
                GZIPInputStream gzin = new GZIPInputStream(body);
                rawInputStream = new ByteArrayInputStream(StreamUtils.copyToByteArray(gzin));
            }
            byte[] buffer = new byte[1024];
            int len;
            while ((len = rawInputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return outputStream;
    }
}
