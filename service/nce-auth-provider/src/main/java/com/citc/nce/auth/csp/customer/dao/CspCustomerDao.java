package com.citc.nce.auth.csp.customer.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.csp.customer.entity.CspCustomerDo;
import com.citc.nce.auth.csp.customer.vo.CustomerProvinceResp;
import com.citc.nce.auth.csp.customer.vo.CustomerResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:22
 */
@Mapper
public interface CspCustomerDao extends BaseMapperX<CspCustomerDo> {
}
