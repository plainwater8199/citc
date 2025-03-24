package com.citc.nce.auth.csp.readingLetter.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.readingLetter.entity.CspReadingLetterAccountDo;
import com.citc.nce.auth.csp.readingLetter.vo.CspReadingLetterAccountListResp;
import com.citc.nce.auth.csp.sms.account.entity.CspSmsAccountDo;
import com.citc.nce.auth.prepayment.vo.SmsMessageAccountListVo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CspReadingLetterAccountDao extends BaseMapperX<CspReadingLetterAccountDo> {

    Page<CspReadingLetterAccountListResp> selectAccount(@Param("cspId") String cspId,
                                                        @Param("customerId") String customerId,
                                                        @Param("name") String name,
                                                        @Param("status") Integer status,
                                                        @Param("accountName") String accountName,
                                                        Page<CspReadingLetterAccountListResp> page);

    @Select("select account_name\n" +
            "                                from csp_reading_letter_account\n" +
            "                                where csp_reading_letter_account.account_id = #{accountId}\n" +
            "                                  and csp_reading_letter_account.deleted = 0")
    String selectNameByAccountId(@Param("accountId") String accountId);
}
