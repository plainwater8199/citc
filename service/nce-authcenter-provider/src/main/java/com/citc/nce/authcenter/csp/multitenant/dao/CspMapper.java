package com.citc.nce.authcenter.csp.multitenant.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author jiancheng
 */
public interface CspMapper extends BaseMapperX<Csp> {
    Map existByTableName(@Param("tableName") String tableName);

    List<UserInfoVo> queryByNameOrPhone(@Param("nameOrPhone") String nameOrPhone);

    List<UserInfoVo> getByIdList(@Param("idList") Collection<String> idList);

    Page<CspMealCspInfo> listAll(@Param("page") Page<CspMealCspInfo> page,@Param("query")  CspInfoPage query);
}
