package com.citc.nce.authcenter.tenantdata.user.dao;


import com.citc.nce.auth.csp.menu.vo.ChatbotIdReq;
import com.citc.nce.auth.csp.menu.vo.MenuRecordResp;
import com.citc.nce.authcenter.tenantdata.user.entity.MenuDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.dao
 * @Author: litao
 * @CreateTime: 2023-02-16  14:34
 
 * @Version: 1.0
 */
@Mapper
public interface Menu1Dao extends BaseMapperX<MenuDo> {
    List<MenuRecordResp> queryByChatbotId(ChatbotIdReq req);
}
