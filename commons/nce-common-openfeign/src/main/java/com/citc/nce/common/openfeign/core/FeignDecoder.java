package com.citc.nce.common.openfeign.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/30 14:23
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class FeignDecoder implements Decoder {
    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        Reader reader = null;
        try {
            reader = response.body().asReader(Charset.forName("utf-8"));
            String string = IOUtils.toString(reader);
            JSONObject restJson = JSON.parseObject(string);
            JSONObject data = restJson.getJSONObject("data");
            String typeName = type.getTypeName();
            Class<?> typeClass = Class.forName(typeName);
            return data.toJavaObject(typeClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
