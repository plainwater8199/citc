package com.citc.nce.auth.csp.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.csp.menu.dao.MenuDao;
import com.citc.nce.auth.csp.menu.entity.MenuDo;
import com.citc.nce.auth.csp.menu.service.MenuService;
import com.citc.nce.auth.csp.menu.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu
 * @Author: litao
 * @CreateTime: 2023-02-16  14:43

 * @Version: 1.0
 */
@RestController
@Slf4j
public class MenuController implements MenuApi{
    @Resource
    private MenuService menuService;

    @Resource
    private MenuDao menuDao;

    @Override
    public void submit(@RequestBody @Valid MenuSaveReq req) {
        menuService.submit(req);
    }

    @Override
    public MenuResp queryByChatbotId(@RequestBody @Valid ChatbotIdReq req) {
        return menuService.queryByChatbotId(req);
    }

    @Override
    public void reduction(ChatbotIdReq req) {
        menuService.reduction(req);
    }

    @Override
    public List<MenuParentResp> queryMenuContentById(IdReq req) {
        return menuService.queryMenuContentById(req);
    }

    @Override
    @Transactional
    public void updateStatus(@Valid ChatbotIdReq req) {
        update(req);
    }

    @Override
    public MenuChildResp queryButtonByUUID(String uuid) {
        return menuService.queryButtonByUUID(uuid);
    }

    @Override
    public void saveSupplierChatbotMenu(MenuSaveReq req) {
        menuService.saveSupplierChatbotMenu(req);
    }

    @Transactional
    public void update(ChatbotIdReq req){
        LambdaQueryWrapper<MenuDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuDo::getChatbotId,req.getChatbotId());
        wrapper.orderByDesc(MenuDo::getVersion);
        List<MenuDo> menuDos = menuDao.selectList(wrapper);
        if (1 == req.getUseable()){
            //可用
            menuDos.get(0).setMenuStatus(1);
            menuDos.get(0).setResult("审核通过");
        }else {
            //不可用
            menuDos.get(0).setMenuStatus(2);
            menuDos.get(0).setResult("【审核不通过】");
        }
        menuDao.updateById(menuDos.get(0));
    }
}
