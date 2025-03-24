package com.citc.nce.authcenter.tenantdata.user.dao;

import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.user.vo.req.UserCertificationOptionListDBVO;
import com.citc.nce.authcenter.tenantdata.user.entity.UserCertificateOptionsDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCertificateOptions1Dao extends BaseMapperX<UserCertificateOptionsDo> {

    List<UserCertificateOptionsDo> getList(UserCertificationOptionListDBVO vo);

    List<UserCertificateResp> selectCertificateOptionsList(@Param("userId") String userId);
}
