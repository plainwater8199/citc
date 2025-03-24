package com.citc.nce.auth.messageplan.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.entity.FifthMessagePlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 5G消息套餐表 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:41
 */
public interface FifthMessagePlanMapper extends BaseMapper<FifthMessagePlan> {

    Page<FifthMessagePlanListVo> selectPlan(
            @Param("operator") Integer operator,
            @Param("planId") String planId,
            @Param("creator") String creator,
            @Param("status") MessagePlanStatus status,
            Page<FifthMessagePlanListVo> page);

    FifthMessagePlanVo selectPlanDetail(@Param("id") Long id, @Param("cspId") String cspId);
}
