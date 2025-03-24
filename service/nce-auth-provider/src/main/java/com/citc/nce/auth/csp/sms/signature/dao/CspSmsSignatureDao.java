package com.citc.nce.auth.csp.sms.signature.dao;

import com.citc.nce.auth.csp.sms.signature.entity.CspSmsAccountSignatureDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CspSmsSignatureDao extends BaseMapperX<CspSmsAccountSignatureDo> {

    @Select("SELECT id, account_id, signature, type, creator, creator_old, create_time, updater, updater_old, update_time, deleted, deleted_time " +
            "FROM csp_sms_account_signature " +
            "WHERE id IN (#{ids})")
    List<CspSmsAccountSignatureDo> getCspSmsAccountSignatureDoList(String ids);
}
