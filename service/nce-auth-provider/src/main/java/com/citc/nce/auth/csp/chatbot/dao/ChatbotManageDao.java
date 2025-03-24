package com.citc.nce.auth.csp.chatbot.dao;

import com.citc.nce.auth.csp.chatbot.entity.ChatbotManageDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:22
 */
@Mapper
public interface ChatbotManageDao extends BaseMapperX<ChatbotManageDo> {

    @Select("SELECT chatbot_boot.*\n" +
            "FROM  \n" +
            "( SELECT * FROM chatbot_manage_cmcc WHERE customer_id= #{customerId} AND operator_code = #{operatorCode}) chatbot_boot\n" +
            "left join csp_customer_chatbot_account ON csp_customer_chatbot_account.chatbot_account_id= chatbot_boot.chatbot_account_id\n" +
            "WHERE csp_customer_chatbot_account.supplier_tag = 'owner'")
    ChatbotManageDo getByISPAndCustomerIdExcludeFd(@Param("operatorCode") Integer operatorCode, @Param("customerId") String customerId);
}
