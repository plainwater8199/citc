package com.citc.nce.csp;

import com.citc.nce.auth.csp.menu.MenuApi;
import com.citc.nce.auth.csp.menu.vo.*;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.csp
 * @Author: litao
 * @CreateTime: 2023-02-20  15:34
 
 * @Version: 1.0
 */
@RestController
@Api(value = "MenuController",tags = "CSP--固定菜单")
public class MenuController {
    @Resource
    private MenuApi menuApi;

    @SkipToken
    @PostMapping("/csp/menu/queryByChatbotId")
    @ApiOperation(value = "查询固定菜单", notes = "查询固定菜单")
    MenuResp queryByChatbotId(@RequestBody @Valid ChatbotIdReq req){
        return menuApi.queryByChatbotId(req);
    }

    @PostMapping("/csp/menu/submit")
    @ApiOperation(value = "提交固定菜单", notes = "提交固定菜单")
    void submit(@RequestBody @Valid MenuSaveReq req){
        menuApi.submit(req);
    }

    @PostMapping("/csp/menu/reduction")
    @ApiOperation(value = "还原", notes = "还原")
    public void reduction(@RequestBody @Valid ChatbotIdReq req) {
        menuApi.reduction(req);
    }

    @PostMapping("/csp/menu/queryMenuContentById")
    @ApiOperation(value = "查询菜单内容", notes = "查询菜单内容")
    public List<MenuParentResp> queryMenuContentById(@RequestBody @Valid IdReq req) {
        return menuApi.queryMenuContentById(req);
    }

    @PostMapping("/csp/menu/supplier/save")
    @HasCsp
    @ApiOperation(value = "供应商chatbot menu保存")
    public void saveSupplierChatbotMenu(@RequestBody @Valid MenuSaveReq req) {
        menuApi.saveSupplierChatbotMenu(req);
    };
}
