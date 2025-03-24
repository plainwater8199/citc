package com.citc.nce.common.web.utils;

import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author jiancheng
 */
@Slf4j
public class ResponseUtils {
    private ResponseUtils() {
    }

    public static void sendJson(HttpServletResponse response, RestResult<?> result) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write(JsonUtils.obj2String(result));
        } catch (IOException e) {
            log.error("IO异常，响应失败");
        }
    }

}
