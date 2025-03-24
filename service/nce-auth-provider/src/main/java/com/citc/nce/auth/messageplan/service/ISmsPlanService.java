package com.citc.nce.auth.messageplan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.messageplan.entity.SmsPlan;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.SmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.SmsPlanVo;

/**
 * <p>
 * 短信套餐表 服务类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
public interface ISmsPlanService extends IService<SmsPlan> {

    void addPlan(SmsPlanAddVo addVo);

    Page<SmsPlanListVo> selectPlan(MessagePlanSelectReq selectReq);

    void updateState(Long id, Integer status);

    void deletePlan(Long id);

    SmsPlanVo getPlanDetail(Long id);

}
