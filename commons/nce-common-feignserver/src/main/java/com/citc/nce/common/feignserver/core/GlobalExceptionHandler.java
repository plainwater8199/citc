package com.citc.nce.common.feignserver.core;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizExposeStatusException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/22 17:39
 * @Version: 1.0
 * @Description:
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final int BIZ_EXCEPTION_HTTP_STATUS = 600;

    /**
     * 处理 SpringMVC 请求参数缺失
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseBody
    public RestResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex, HttpServletResponse response) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }
    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    @ResponseBody
    public RestResult<?> MaxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException ex, HttpServletResponse response) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), "上传文件大小超限");
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseBody
    public RestResult<?> httpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletResponse response) {
        log.warn("[HttpMessageNotReadableException]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), "请求参数获取失败");
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public RestResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex, HttpServletResponse response) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RestResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex, HttpServletResponse response) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String reason = fieldError == null ? ex.getMessage() : fieldError.getDefaultMessage();
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", reason));
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RestResult<?> bindExceptionHandler(BindException ex, HttpServletResponse response) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        String reason = fieldError == null ? ex.getMessage() : fieldError.getDefaultMessage();
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", reason));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public RestResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex, HttpServletResponse response) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }


    /**
     * 处理 SpringMVC 请求方法不正确
     * <p>
     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public RestResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex, HttpServletResponse response) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }

    /**
     * 处理业务异常 BizException
     * <p>
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public RestResult<?> serviceExceptionHandler(BizException ex, HttpServletResponse response) {
        log.info("[serviceExceptionHandler]:{}", ex.toString(), ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(ex.getCode(), ex.getMsg());
    }
    /**
     * 处理业务异常 BizException 更改httpStatus
     * <p>
     */
    @ExceptionHandler(value = BizExposeStatusException.class)
    @ResponseBody
    public RestResult<?> BizExposeStatusExceptionHandler(HttpServletResponse httpServletResponse,BizExposeStatusException ex) {
        log.info("[BizExposeStatusException]:{}", ex.toString(), ex);
        httpServletResponse.setStatus(408);
        return RestResult.error(ex.getCode(), ex.getMsg());
    }
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public RestResult<?> illegalArgumentExceptionHandler(IllegalArgumentException ex, HttpServletResponse response) {
        log.info("[IllegalArgumentException]:{}", ex.toString(), ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
    }


    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    @ResponseBody
    public RestResult<?> sqlSyntaxErrorExceptionHandler(SQLSyntaxErrorException ex, HttpServletResponse response) {
        log.info("[SQLSyntaxErrorException]:{}", ex.toString(), ex);
        response.setStatus(BIZ_EXCEPTION_HTTP_STATUS);
        return RestResult.error(GlobalErrorCode.SQL_ERROR.getCode(), GlobalErrorCode.SQL_ERROR.getMsg());
    }



}
