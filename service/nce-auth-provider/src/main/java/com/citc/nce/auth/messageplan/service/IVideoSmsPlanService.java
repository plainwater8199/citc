package com.citc.nce.auth.messageplan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.entity.VideoSmsPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;

/**
 * <p>
 * 视频短信套餐表 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
public interface IVideoSmsPlanService extends IService<VideoSmsPlan> {

    void addPlan(VideoSmsPlanAddVo addVo);

    Page<VideoSmsPlanListVo> selectPlan(MessagePlanSelectReq selectReq);

    void updateState(Long id, Integer status);

    void deletePlan(Long id);

    VideoSmsPlanVo getPlanDetail(Long id);
}
