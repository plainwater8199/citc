package com.citc.nce.auth.identification.dao;

import com.citc.nce.auth.identification.entity.UserEnterpriseIdentificationDo;
import com.citc.nce.auth.identification.vo.resp.UserProvinceResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:54
 * @description:
 */
@Mapper
public interface UserEnterpriseIdentificationDao extends BaseMapperX<UserEnterpriseIdentificationDo> {
//    List<UserProvinceResp> queryUserProvince(@Param("userId") String userId);
}
