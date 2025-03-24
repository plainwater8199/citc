package com.citc.nce.auth.csp.menu.dao;

import com.citc.nce.auth.csp.menu.entity.MenuDo;
import com.citc.nce.auth.csp.menu.vo.ChatbotIdReq;
import com.citc.nce.auth.csp.menu.vo.MenuRecordResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.dao
 * @Author: litao
 * @CreateTime: 2023-02-16  14:34
 
 * @Version: 1.0
 */
@Mapper
public interface MenuDao extends BaseMapperX<MenuDo> {
    List<MenuRecordResp> queryByChatbotId(ChatbotIdReq req);

    @Select("select max(version) from chatbot_manage_menu where chatbot_account_id = #{chatbotId} and deleted = 0")
    Integer selectMenuVersion(@Param("chatbotId") String chatbotId);
}
