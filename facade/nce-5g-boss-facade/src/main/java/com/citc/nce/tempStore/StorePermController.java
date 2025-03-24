package com.citc.nce.tempStore;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.tempStorePerm.bean.ChangePrem;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bydud
 * @since 11:43
 */
@Api(tags = "后台管理-用户管理")
@RestController
@Slf4j
@AllArgsConstructor
public class StorePermController {
    private AdminAuthApi authApi;

    @BossAuth("/chatbot-view/user-crm")
    @PostMapping("/admin/user/changeTempStorePermission")
    @ApiOperation("修改模板商城权限")
    @Log(title = "管理端-修改模板商城权限", operatorType = OperatorType.MANAGE)
    public void changeTempStorePermission(@RequestBody @Validated ChangePrem changePrem) {
        authApi.changeTempStorePermission(changePrem);
    }

}
