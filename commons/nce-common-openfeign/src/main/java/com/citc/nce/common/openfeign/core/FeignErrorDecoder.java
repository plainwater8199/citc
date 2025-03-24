package com.citc.nce.common.openfeign.core;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizExposeStatusException;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.util.JsonUtils;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/30 14:21
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final static List<Integer> throwCode = Arrays.asList(820201008,408);


    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("enter feign error decode:{}", methodKey);
        Reader reader = null;
        try {
            if (response.status() == 600||response.status() == 408) {
                reader = response.body().asReader(Charset.forName("utf-8"));
                String string = IOUtils.toString(reader);
                log.error("resp body:{}", string);
                RestResult restResult = JsonUtils.string2Obj(string, RestResult.class);
                if(ObjectUtil.isNotEmpty(restResult)&&throwCode.contains(restResult.getCode()))
                {
                    return new BizExposeStatusException(restResult.getCode(),restResult.getMsg());
                }
                return new BizException(restResult.getCode(), restResult.getMsg());
            }
        } catch (Exception e) {
            log.error("methodKey exception", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return FeignException.errorStatus(methodKey, response);

    }
}
