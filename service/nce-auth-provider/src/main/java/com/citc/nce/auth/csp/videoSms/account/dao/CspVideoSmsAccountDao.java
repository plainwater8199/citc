package com.citc.nce.auth.csp.videoSms.account.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.videoSms.account.dto.CspVideoSmsAccountDto;
import com.citc.nce.auth.csp.videoSms.account.entity.CspVideoSmsAccountDo;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountChatbotResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountResp;
import com.citc.nce.auth.prepayment.vo.VideoSmsMessageAccountListVo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:22
 */
@Mapper
public interface CspVideoSmsAccountDao extends BaseMapperX<CspVideoSmsAccountDo> {

    List<CspVideoSmsAccountResp> queryList(CspVideoSmsAccountDto dto);

    Long queryListCount(CspVideoSmsAccountDto dto);

    List<CspVideoSmsAccountChatbotResp> queryListChatbot(CspVideoSmsAccountDto dto);

    Long queryListChatbotCount(CspVideoSmsAccountDto dto);

    Long checkNameUnique(@Param("accountName") String accountName, @Param("id") Long id);

    default CspVideoSmsAccountDo selectAccountId(String accountId) {
        return selectOne(new LambdaQueryWrapperX<CspVideoSmsAccountDo>()
                .eq(CspVideoSmsAccountDo::getAccountId, accountId));
    }


    Page<VideoSmsMessageAccountListVo> selectAccount(@Param("cspId") String cspId,
                                                     @Param("customerId") String customerId,
                                                     @Param("name") String name,
                                                     @Param("dictCode") String dictCode,
                                                     @Param("status") Integer status,
                                                     @Param("accountName") String accountName,
                                                     Page<VideoSmsMessageAccountListVo> page);


    @Select("select account_name\n" +
            "                                from csp_video_sms_account\n" +
            "                                where csp_video_sms_account.account_id = #{accountId}\n" +
            "                                  and csp_video_sms_account.deleted = 0")
    String selectNameByAccountId(@Param("accountId") String accountId);

    List<VideoSmsMessageAccountListVo> selectPlanVideoSmsAccount(@Param("customerId") String customerId, @Param("accountIds") String accountIds);
}
