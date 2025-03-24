package com.citc.nce.auth.accountmanagement.dao;

import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.HashMap;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:00
 * @Version: 1.0
 * @Description:
 */
@Mapper
public interface AccountManagementDao extends BaseMapperX<AccountManagementDo> {
    @Update("UPDATE csp_customer_chatbot_account SET delete_time=#{deleteTime},deleted=#{deleted} WHERE chatbot_account_id=#{chatbotAccountId} ")
    int delAccountManagementById(HashMap<String, Object> map);

    @Select("select DISTINCT account_name\n" +
            "                                from csp_customer_chatbot_account\n" +
            "                                where csp_customer_chatbot_account.chatbot_account_id = #{accountId} or csp_customer_chatbot_account.chatbot_account = #{accountId}\n" +
            "                                  and csp_customer_chatbot_account.deleted = 0")
    String selectNameByAccountId(@Param("accountId") String accountId);
}
