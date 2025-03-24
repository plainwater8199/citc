package com.citc.nce.auth.messageplan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.messageplan.entity.FifthMessagePlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanAddVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;

/**
 * <p>
 * 5G消息套餐表 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:41
 */
public interface IFifthMessagePlanService extends IService<FifthMessagePlan> {

    void addPlan(FifthMessagePlanAddVo addVo);

    Page<FifthMessagePlanListVo> selectPlan(MessagePlanSelectReq selectReq);

    void updateState(Long id, Integer status);

    void deletePlan(Long id);

    FifthMessagePlanVo getPlanDetail(Long id);
}
