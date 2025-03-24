package com.citc.nce.common.openfeign.core;


import cn.hutool.core.codec.Base64;
import com.citc.nce.common.Constants;
import com.citc.nce.common.openfeign.util.FeignUtils;
import com.citc.nce.common.trace.TraceContext;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/5/30 15:55
 * @Version: 1.0
 * @Description:
 */
@Slf4j
public class FeignHeaderInterceptor implements RequestInterceptor {
    @Override

    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(Constants.HttpHeader.TOKEN, SessionContextUtil.getToken());
        requestTemplate.header(Constants.HttpHeader.TRACE_ID, TraceContext.getTrace());
        if (null != SessionContextUtil.getUser()) {
            FeignUtils.createJsonHeader(requestTemplate, Constants.HttpHeader.USER, Base64.encode(JsonUtils.obj2String(SessionContextUtil.getUser())));
        }
    }
}
