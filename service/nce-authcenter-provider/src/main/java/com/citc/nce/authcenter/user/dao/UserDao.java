package com.citc.nce.authcenter.user.dao;

import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapperX<UserDo> {
}
