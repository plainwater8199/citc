package com.citc.nce.developer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.developer.entity.SmsDeveloperCustomerDo;
import com.citc.nce.developer.vo.SmsDeveloperCustomerManagerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
public interface SmsDeveloperCustomerMapper extends BaseMapper<SmsDeveloperCustomerDo> {

    Page<SmsDeveloperCustomerManagerVo> searchCustomersManager(
            @Param("customerId") String customerId,
            @Param("state") Integer state,
            @Param("cspId") String cspId,
            Page<SmsDeveloperCustomerManagerVo> page
    );

    List<SmsDeveloperCustomerDo> selectListEncode();
}
