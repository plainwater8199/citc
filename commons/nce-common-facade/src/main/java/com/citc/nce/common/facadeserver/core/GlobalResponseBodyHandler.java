package com.citc.nce.common.facadeserver.core;

import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import com.citc.nce.common.facadeserver.annotations.WrapResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import springfox.documentation.spring.web.plugins.Docket;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 全局响应结果（ResponseBody）处理器
 */
@RestControllerAdvice(basePackages = {"com.citc.nce"})
@Slf4j
public class GlobalResponseBodyHandler implements ResponseBodyAdvice {
    private static final byte[] b = new byte[0];

    @Autowired
    private ObjectMapper objectMapper;

    public GlobalResponseBodyHandler() {
        log.info("GlobalResponseBodyHandler...");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        Method method = returnType.getMethod();
        if (method != null) {
            final Class<?> clazz = method.getDeclaringClass();
            if (clazz.isAnnotationPresent(UnWrapResponse.class) && !method.isAnnotationPresent(WrapResponse.class)) {
                return false;
            } else if (method.isAnnotationPresent(UnWrapResponse.class)) {
                return false;
            }
            String methodName = method.getName();
            List<String> methodNameList = Arrays.asList("apiCall", "test", "test2");
            return !(returnType.getDeclaringClass().equals(Docket.class) || methodNameList.contains(methodName));
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null) {
            return RestResult.success(null);
        }
        if (body.getClass().equals(b.getClass()) || body instanceof RestResult) {
            return body;
        }

        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        // String类型不能直接包装，所以要进行些特别的处理或者通过configureMessageConverters处理
        if (returnType.getGenericParameterType().equals(String.class)) {
            try {
                // 将数据包装在ResultVO里后，再转换为json字符串响应给前端
                response.setStatusCode(HttpStatus.OK);
                return objectMapper.writeValueAsString(RestResult.success(body));
            } catch (JsonProcessingException e) {
                return RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 将原本的数据包装在ResultVO里
        RestResult<Object> success = RestResult.success(body);
        return success;
    }
}
