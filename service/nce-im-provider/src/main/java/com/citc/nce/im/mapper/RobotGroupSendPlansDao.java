package com.citc.nce.im.mapper;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.req.RobotGroupSendPlansByPlanNameReq;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.AnalysisResp;
import com.citc.nce.robot.vo.PlanResp;
import com.citc.nce.robot.vo.RobotGroupSendPlans;
import com.citc.nce.robot.vo.RobotGroupSendPlansAndChatbotAccount;

import java.util.List;
import java.util.Objects;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:08
 * @Description: MybatisPlus的指令管理数据层
 * @Version: 1.0
 */
public interface RobotGroupSendPlansDao extends BaseMapperX<RobotGroupSendPlansDo> {

    Long countSendAmount(SendPageReq sendPageReq);

    List<AnalysisResp> queryAnalysisByHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryAnalysisByDay(SendPageReq sendPageReq);

    List<AnalysisResp> queryMassNumHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryMassNumDay(SendPageReq sendPageReq);

    List<RobotGroupSendPlans> queryPlum(SendPageReq sendPageReq);

    List<AnalysisResp> queryPlanNumHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryPlanNumDay(SendPageReq sendPageReq);

    List<AnalysisResp> queryExecuteNumHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryExecuteNumDay(SendPageReq sendPageReq);

    List<PlanResp> selectAll(SendPageReq sendPageReq);

    /**
     * 检查账号是不是自己的  csp 创建者，客户
     * @param planId
     */
    default void checkPlanIsOwn(Long planId) {
        RobotGroupSendPlansDo plansDetailDo = selectOne(
                new LambdaQueryWrapperX<RobotGroupSendPlansDo>()
                        .eq(BaseDo::getId, planId));
        if (Objects.isNull(plansDetailDo) ) {
            throw new BizException("计划不存在");
        }

        if (!SessionContextUtil.getLoginUser().getUserId().equals(plansDetailDo.getCreator())){
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
    }

    List<RobotGroupSendPlansAndChatbotAccount> getByGroupSendIds(List<String> selectPlanIds);
}
