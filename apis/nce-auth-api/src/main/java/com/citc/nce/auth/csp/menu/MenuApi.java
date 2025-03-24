package com.citc.nce.auth.csp.menu;

import com.citc.nce.auth.csp.menu.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu
 * @Author: litao
 * @CreateTime: 2023-02-16  14:08
 
 * @Version: 1.0
 */
@FeignClient(value = "auth-service", contextId = "CSPMenu", url = "${auth:}")
public interface MenuApi {
    @PostMapping("/csp/menu/submit")
    void submit(@RequestBody @Valid MenuSaveReq req);

    @PostMapping("/csp/menu/queryByChatbotId")
    MenuResp queryByChatbotId(@RequestBody @Valid ChatbotIdReq req);

    @PostMapping("/csp/menu/reduction")
    void reduction(@RequestBody @Valid ChatbotIdReq req);

    @PostMapping("/csp/menu/queryMenuContentById")
    List<MenuParentResp> queryMenuContentById(@RequestBody @Valid IdReq req);

    @PostMapping("/csp/menu/updateStatus")
    void updateStatus(@RequestBody @Valid ChatbotIdReq req);

    @PostMapping("/csp/menu/queryButtonByUUID")
    MenuChildResp queryButtonByUUID(@RequestBody String uuid);

    @PostMapping("/csp/menu/supplier/save")
    void saveSupplierChatbotMenu(@RequestBody @Valid MenuSaveReq req);
}
