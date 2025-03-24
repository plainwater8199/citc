package com.citc.nce.tempStore;

import com.citc.nce.authcenter.auth.AdminAuthApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bydud
 * @since 17:06
 */
@RestController
@Api(tags = "csp-检查权限")
@Slf4j
@AllArgsConstructor
@RequestMapping("/tempStore/perm")
public class CspPermController {
    private AdminAuthApi authApi;

    @GetMapping("hasePerm")
    public boolean hasePerm(){
       return authApi.haseTempStorePerm();
    }

}
