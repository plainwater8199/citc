package com.citc.nce.auth.certificate.dao;

import com.citc.nce.auth.certificate.entity.UserCertificateOptionsDo;
import com.citc.nce.auth.certificate.vo.resp.UserCertificateResp;
import com.citc.nce.auth.user.vo.req.UserCertificationOptionListDBVO;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCertificateOptionsDao extends BaseMapperX<UserCertificateOptionsDo> {

    List<UserCertificateOptionsDo> getList(UserCertificationOptionListDBVO vo);

    List<UserCertificateResp> selectCertificateOptionsList(@Param("userId") String userId);
}
