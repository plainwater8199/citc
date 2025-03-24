package com.citc.nce.developer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.developer.entity.VideoDeveloperCustomerDo;
import com.citc.nce.developer.vo.VideoDeveloperCustomerManagerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ping chen
 */
public interface VideoDeveloperCustomerMapper extends BaseMapper<VideoDeveloperCustomerDo> {

    Page<VideoDeveloperCustomerManagerVo> searchCustomersManager(
            @Param("customerId") String customerId,
            @Param("state") Integer state,
            @Param("cspId") String cspId,
            Page<VideoDeveloperCustomerManagerVo> page
    );

    List<VideoDeveloperCustomerDo> selectListEncode();
}
