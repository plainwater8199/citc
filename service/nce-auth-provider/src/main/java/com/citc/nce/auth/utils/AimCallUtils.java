package com.citc.nce.auth.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.config.AimTestConfig;
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
import java.util.*;
import java.util.zip.GZIPInputStream;

@Component
@Log4j2
@RequiredArgsConstructor
@EnableConfigurationProperties(AimTestConfig.class)
public class AimCallUtils {

    private final AimTestConfig aimTestConfig;


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
        String timestamp = DateUtil.dateToString(new Date(),"yyyyMMddHHmmss");
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
