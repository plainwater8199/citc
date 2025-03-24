package com.citc.nce.auth.csp.sms.account.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.sms.account.dto.CspSmsAccountDto;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountChatbotResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountResp;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CspSmsAccountDao extends BaseMapperX<CspSmsAccountDo> {
    Long queryListCount(CspSmsAccountDto dto);

    List<CspSmsAccountResp> queryList(CspSmsAccountDto dto);

    Long queryListChatbotCount(CspSmsAccountDto dto);

    List<CspSmsAccountChatbotResp> queryListChatbot(CspSmsAccountDto dto);

    Page<SmsMessageAccountListVo> selectSmsAccount(@Param("cspId") String cspId,
                                                   @Param("customerId") String customerId,
                                                   @Param("name") String name,
                                                   @Param("dictCode") String dictCode,
                                                   @Param("status") Integer status,
                                                   @Param("accountName") String accountName,
                                                   Page<SmsMessageAccountListVo> page);

    @Select("select account_name\n" +
            "                                from csp_sms_account\n" +
            "                                where csp_sms_account.account_id = #{accountId}\n" +
            "                                  and csp_sms_account.deleted = 0")
    String selectNameByAccountId(@Param("accountId") String accountId);

    List<SmsMessageAccountListVo> selectPlanSmsAccount(@Param("customerId") String customerId, @Param("accountIds") String accountIds);
}
