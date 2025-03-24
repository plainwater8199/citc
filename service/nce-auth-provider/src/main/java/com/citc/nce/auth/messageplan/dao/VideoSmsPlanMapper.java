package com.citc.nce.auth.messageplan.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.entity.VideoSmsPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.vo.SmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 视频短信套餐表 Mapper 接口
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
public interface VideoSmsPlanMapper extends BaseMapper<VideoSmsPlan> {
    Page<VideoSmsPlanListVo> selectPlan(
            @Param("planId") String planId,
            @Param("creator") String creator,
            @Param("status") MessagePlanStatus status,
            Page<VideoSmsPlanListVo> page);

    VideoSmsPlanVo selectPlanDetail(@Param("id") Long id, @Param("cspId") String cspId);
}
