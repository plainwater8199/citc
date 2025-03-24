package com.citc.nce.im.mapper;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.entity.RobotGroupSendPlanBindTaskDo;
import com.citc.nce.im.entity.RobotGroupSendPlansDo;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.AnalysisResp;
import com.citc.nce.robot.vo.PlanResp;
import com.citc.nce.robot.vo.RobotGroupSendPlans;

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
public interface RobotGroupSendPlanBindTaskDao extends BaseMapperX<RobotGroupSendPlanBindTaskDo> {

}
