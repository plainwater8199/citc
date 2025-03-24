package com.citc.nce.authcenter.identification.dao;


import com.citc.nce.authcenter.identification.entity.UserCertificateOptionsDo;
import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import com.citc.nce.authcenter.identification.vo.UserCertificationOptionListDBVO;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserCertificateOptionsDao extends BaseMapperX<UserCertificateOptionsDo> {

    List<UserCertificateOptionsDo> getList(UserCertificationOptionListDBVO vo);

    List<UserCertificateItem> selectCertificateOptionsList(@Param("userId") String userId);
}
