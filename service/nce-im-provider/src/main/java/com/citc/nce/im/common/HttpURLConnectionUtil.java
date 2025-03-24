package com.citc.nce.im.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpURLConnectionUtil {
    private static Logger log = LoggerFactory.getLogger(HttpURLConnectionUtil.class);


    /**
     * Http get请求
     * @param httpUrl 连接
     * @return 响应数据
     */
    /**
     * Http get请求
     *
     * @param httpUrl 连接
     * @return 响应数据
     */
    public static String doGet(String httpUrl) {
        String result = "";
        log.info(httpUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.code() == 200){
                result = response.body().string();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            if (response != null) {
                // body 必须被关闭，否则会发生资源泄漏；
                response.body().close();
            }
        }
        log.info(result);
        return result;
    }
    /**
     * Http post请求
     * @param httpUrl 连接
     * @param param 参数
     * @return
     */
    public static String doPost(String httpUrl, List<JSONObject> headMap, String param, String contentType) {
        if (!StringUtils.hasText(contentType)){
               contentType = "application/json;charset=utf-8";
        }
        String result = "";
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = null;
        RequestBody body = null;
        int formData = 0;
        FormBody.Builder formBody = null;
        if ("application/json;charset=utf-8".equals(contentType)){
            mediaType = MediaType.parse("application/json");
            body = RequestBody.create(mediaType, param);
        }
        if ("text/plain;charset=utf-8".equals(contentType)){
            mediaType = MediaType.parse("text/plain");
            body = RequestBody.create(mediaType, param);
        }
        if (contentType.contains("multipart/form-data")){
            mediaType = MediaType.parse("multipart/form-data");
            formBody = new FormBody.Builder();
            Map map = (Map) JSONObject.parse(param);
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                formBody.add(entry.getKey(), entry.getValue());
            }
            formData = 1;
        }
        Request.Builder builder = new Request.Builder();
        if (1 == formData) {
            builder.url(httpUrl)
                    .post(formBody.build())
                    .addHeader("accept", "*/*")
                    .addHeader("connection", "Keep-Alive")
                    .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                    .addHeader("Content-Type", contentType)
                    .build();
        } else {
            builder.url(httpUrl)
                    .post(body)
                    .addHeader("accept", "*/*")
                    .addHeader("connection", "Keep-Alive")
                    .addHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)")
                    .addHeader("Content-Type", contentType)
                    .build();

        }
        if(CollectionUtil.isNotEmpty(headMap)){
            for (JSONObject object : headMap) {
                builder.addHeader(object.getString("name"),object.getJSONObject("value").getString("value"));
            }
        }
        Request request = builder.build();
        Response response = null;
        try {
            log.info("request header is {}",request.headers());
            log.info("request body is {}",param);
            response = client.newCall(request).execute();
            if (response.code() == HttpStatus.HTTP_OK || response.code() == HttpStatus.HTTP_CREATED){
                result = response.body().string();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            if (response != null) {
                // body 必须被关闭，否则会发生资源泄漏；
                response.body().close();
            }
        }
        log.info(result);
        return result;
    }
}
