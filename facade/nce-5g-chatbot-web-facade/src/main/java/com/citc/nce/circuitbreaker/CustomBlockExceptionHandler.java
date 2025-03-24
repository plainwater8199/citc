package com.citc.nce.circuitbreaker;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CustomBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        log.error("熔断异常:{}",request.getRequestURI(), e);
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        RestResult<Object> error = RestResult.error(GlobalErrorCode.DEGRADE_EXCEPTION_ERROR.getCode(), "服务器繁忙,请稍后再试");
        response.getWriter().write(JsonUtils.obj2String(error));
    }
}