package com.citc.nce.common.facadeserver.core;

import cn.dev33.satoken.exception.NotLoginException;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.BizExposeStatusException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.RestResult;
import com.citc.nce.common.facadeserver.verifyCaptcha.Aspect.VerifyCaptchaExp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;


/**
 * 全局异常处理器，将 Exception 翻译成 RestResult + 对应的异常编号
 *
 * @author 芋道源码
 */
@RestControllerAdvice
@Slf4j
public class FacadeGlobalExceptionHandler {

    /**
     * 处理 SpringMVC 请求参数缺失
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数，结果并未传递 xx 参数
     */

    @ExceptionHandler(value = {MissingServletRequestParameterException.class})
    @ResponseBody
    public RestResult<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex, HttpServletResponse response) {
        log.warn("[missingServletRequestParameterExceptionHandler]", ex);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数缺失:%s", ex.getParameterName()));
    }


    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseBody
    public RestResult<?> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("[HttpMessageNotReadableException]", ex);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数获取失败: %s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     * <p>
     * 例如说，接口上设置了 @RequestParam("xx") 参数为 Integer，结果传递 xx 参数类型为 String
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public RestResult<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException ex) {
        log.warn("[methodArgumentTypeMismatchExceptionHandler]", ex);
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数类型错误:%s", ex.getMessage()));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public RestResult<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMsg = fieldError == null ? ex.getMessage() : fieldError.getDefaultMessage();
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", errorMsg));
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public RestResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[handleBindException]", ex);
        FieldError fieldError = ex.getFieldError();
        String errorMsg = fieldError == null ? ex.getMessage() : fieldError.getDefaultMessage();
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", errorMsg));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public RestResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
        return RestResult.error(GlobalErrorCode.BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", constraintViolation.getMessage()));
    }

    @ExceptionHandler(value = {VerifyCaptchaExp.class})
    @ResponseBody
    public RestResult<?> VerifyCaptchaExpExceptionHandler(VerifyCaptchaExp ex) {
        return RestResult.error(GlobalErrorCode.USER_AUTH_ERROR.getCode(), ex.getMessage());
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     * <p>
     * 例如说，A 接口的方法为 GET 方式，结果请求方法为 POST 方式，导致不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public RestResult<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[httpRequestMethodNotSupportedExceptionHandler]", ex);
        return RestResult.error(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode(), String.format("请求方法不正确:%s", ex.getMessage()));
    }

    /**
     * 处理 Spring Security 权限不足的异常
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public RestResult<?> accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException ex) {
        log.warn("[accessDeniedExceptionHandler][userId({}) 无法访问 url({})]", ex);
        return RestResult.error(GlobalErrorCode.FORBIDDEN);
    }

    /**
     * 处理业务异常 BizException
     * <p>
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public RestResult<?> serviceExceptionHandler(BizException ex) {
        log.info("[BizException]:{}", ex.toString(), ex);
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
        httpServletResponse.setStatus(ex.getCode());
        return RestResult.error(ex.getCode(), ex.getMsg());
    }
//    @ExceptionHandler(value = {DegradeException.class, NoFallbackAvailableException.class})
//    @ResponseBody
//    public RestResult<?> circuitBreakerHandler(Exception ex) {
//        log.info("[DegradeException]:{}", ex.toString(), ex);
//        return RestResult.error(GlobalErrorCode.SERVER_BUSY);
//    }

    @ExceptionHandler(value = {NotLoginException.class})
    @ResponseBody
    public RestResult<?> notLoginException(NotLoginException ex) {
        log.info("[NotLoginException]:{}", ex.toString());
        String message = null;
        if ("-3".equals(ex.getType())) {
            message = "当前账号已过期，请重新登录";
        } else if ("-4".equals(ex.getType())) {
            message = "提示：您的账号已在其它地方登陆，若非本人操作，请及时修改密码，保护账号安全。";
        } else {
            message = "请先登录";
        }
        return RestResult.error(401, message);
    }

//    /**
//     * 处理系统异常，兜底处理所有的一切
//     */
//    @ExceptionHandler(value = Exception.class)
//    @ResponseBody
//    public RestResult<?> defaultExceptionHandler(HttpServletRequest req, Throwable ex) {
//        log.error("[defaultExceptionHandler]", ex);
//        return RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR);
//    }


    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public RestResult<?> degradeExceptionHandler(Throwable ex, HttpServletResponse response) {
        log.info("[Throwable]:{}", ex.toString(), ex);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        Object cause = ex.getCause();
        BizException bizException = cause instanceof BizException ? ((BizException) cause) : null;
        if (bizException != null) {
            return RestResult.error(bizException.getCode(), bizException.getMsg());
        }
        BizExposeStatusException bizExposeStatusException=cause instanceof BizExposeStatusException ? ((BizExposeStatusException) cause) : null;
        if (bizExposeStatusException != null) {
            response.setStatus(bizExposeStatusException.getCode());
            return RestResult.error(bizExposeStatusException.getCode(), bizExposeStatusException.getMsg());
        }
        return RestResult.error(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }

}
