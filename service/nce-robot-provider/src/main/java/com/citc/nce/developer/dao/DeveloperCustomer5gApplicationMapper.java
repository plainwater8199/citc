package com.citc.nce.developer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.developer.entity.DeveloperCustomer5gApplicationDo;
import com.citc.nce.developer.vo.DeveloperCustomer5gManagerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
public interface DeveloperCustomer5gApplicationMapper extends BaseMapper<DeveloperCustomer5gApplicationDo> {

    Page<DeveloperCustomer5gManagerVo> searchCustomersManager(
            @Param("customerId") String customerId,
            @Param("state") Integer state,
            @Param("cspId") String cspId,
            @Param("applicationName") String applicationName,
            Page<DeveloperCustomer5gManagerVo> page
    );

    List<DeveloperCustomer5gApplicationDo> selectListEncode();
}
