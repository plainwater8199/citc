package com.citc.nce.exceptionController;

import com.citc.nce.common.CommonConstant;
import com.citc.nce.common.core.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: ylzouf
 * @Date: 2022/10/16 22:47
 * @Version 1.0
 * @Description:
 */
@Api(tags = "权限资源异常处理")
@RestController
public class ExceptionController {

    @ApiOperation("拦截异常处理")
    @RequestMapping(CommonConstant.ERROR_CONTROLLER_PATH)
    public void handleException(HttpServletRequest request) {
        throw (BizException) request.getAttribute("filterError");
    }
}


