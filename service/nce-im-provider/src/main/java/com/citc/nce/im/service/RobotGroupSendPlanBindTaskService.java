package com.citc.nce.im.service;

import com.citc.nce.robot.req.FontdoGroupSendResultReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanBindTask;

import java.util.List;

public interface RobotGroupSendPlanBindTaskService {
    /**
     * 通过TaskId查询单条数据
     *
     * @param taskId 供应商会将TaskID
     * @return 实例对象
     */
    public RobotGroupSendPlanBindTask queryByTaskId(String taskId, String appId);

    /**
     * 通过TaskId查询单条数据, 此处是适配5G阅信的解析回执查询
     *
     * @param taskId 供应商会将TaskID
     * @return 实例对象
     */
    public RobotGroupSendPlanBindTask queryByTaskId(String taskId);

    void bind(FontdoGroupSendResultReq req, String appid);

    List<RobotGroupSendPlanBindTask> queryByTaskIds(List<String> taskIds);
}
