package com.citc.nce.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.citc.nce.common.core.exception.ErrorCode;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.helper.WalnutAuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.AccessDeniedException;
import java.util.Map;

public class WalnutAccessInterceptor implements HandlerInterceptor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private WalnutAuthHelper authHelper;

    @PostConstruct
    public void init() {
        authHelper = new WalnutAuthHelper();
    }


    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws IOException {

        if (!check(req)) {
            //write(req, resp, GlobalErrorCode.FORBIDDEN);
            Writer writer = resp.getWriter();
            writer.write(JSON.toJSONString(RestResult.error(GlobalErrorCode.FORBIDDEN.getCode(),"Invalid identity")));
            writer.flush();
            return false;
        }
        return true;
    }

    private boolean check(HttpServletRequest req) {
        Map pathVariables = (Map) req.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String chatbotId = (String)pathVariables.get("chatbotId");
        System.out.println(chatbotId);
//        String uri = req.getRequestURI();
        String signature = req.getHeader(Constants.HttpHeader.SIGNATURE);
        String nonce = req.getHeader(Constants.HttpHeader.NONCE);
        String timestamp = req.getHeader(Constants.HttpHeader.TIMESTAMP);
        if(signature==null||nonce==null||timestamp==null){
            return false;
        }
        // 验证签名
        try {
            authHelper.checkSignature(chatbotId,signature,nonce,timestamp);
            return true;
        }  catch (Exception e) {
            logger.error("验签时发生异常", e);
        }
        return false;
    }


}
