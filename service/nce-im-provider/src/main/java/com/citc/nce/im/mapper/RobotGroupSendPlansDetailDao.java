package com.citc.nce.im.mapper;


import com.citc.nce.im.entity.RobotGroupSendPlansDetailDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.AnalysisResp;
import com.citc.nce.robot.vo.PlanSend;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:08
 * @Description: MybatisPlus的指令管理数据层
 * @Version: 1.0
 */
public interface RobotGroupSendPlansDetailDao extends BaseMapperX<RobotGroupSendPlansDetailDo> {

    List<AnalysisResp> queryAnalysisByHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryAnalysisByDay(SendPageReq sendPageReq);

    List<PlanSend> queryPlanSend();
}
