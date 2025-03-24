package com.citc.nce.auth.messageplan.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.entity.SmsPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 短信套餐表 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
public interface SmsPlanMapper extends BaseMapper<SmsPlan> {

    Page<SmsPlanListVo> selectPlan(
            @Param("planId") String planId,
            @Param("creator") String creator,
            @Param("status") MessagePlanStatus status,
            Page<SmsPlanListVo> page);

    SmsPlanVo selectPlanDetail(@Param("id") Long id, @Param("cspId") String cspId);
}
