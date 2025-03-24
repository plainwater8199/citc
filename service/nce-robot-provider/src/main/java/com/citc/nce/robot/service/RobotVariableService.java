package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.vo.RobotVariableCreateReq;
import com.citc.nce.robot.vo.RobotVariablePageReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import com.citc.nce.robot.vo.RobotVariableResp;
import com.citc.nce.tempStore.vo.UseTempVariableOrder;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.service
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:19
 * @Description: 机器人设置--变量管理Service层
 * @Version: 1.0
 */
public interface RobotVariableService {


    public RobotVariableResp list(RobotVariablePageReq robotVariablePageReq);

    public RobotVariableResp listByName(String name, BaseUser user);

    public void saveVariable(RobotVariableReq robotVariableReq, BaseUser baseUser);

    public void editVariable(RobotVariableReq robotVariableReq);

    public void removeVariable(long id, BaseUser baseUser);

    public RobotVariableBean queryById(RobotVariableReq robotVariableReq);

    RobotVariableResp getList(RobotVariableCreateReq robotVariableCreateReq);

    List<Long> checkListUseTemp(Map<Long, RobotVariableReq> variableMap);

    void saveListUseTemp(UseTempVariableOrder data);

    List<RobotVariableBean> queryByTsOrderId(Long tsOrderId);

    void removeIds(List<Long> ids);
}
