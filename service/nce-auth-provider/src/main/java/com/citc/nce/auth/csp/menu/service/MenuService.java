package com.citc.nce.auth.csp.menu.service;

import com.citc.nce.auth.csp.menu.vo.*;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.service
 * @Author: litao
 * @CreateTime: 2023-02-16  14:41
 
 * @Version: 1.0
 */
public interface MenuService {
    void submit(MenuSaveReq req);

    MenuResp queryByChatbotId(ChatbotIdReq req);

    void reduction(ChatbotIdReq req);

    List<MenuParentResp> queryMenuContentById(IdReq req);

    MenuChildResp queryButtonByUUID(String uuid);

    /**
     * 新建供应商chatbot的menu
     */
    void saveSupplierChatbotMenu(MenuSaveReq req);
}
