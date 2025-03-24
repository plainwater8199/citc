package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.bean.RobotOrderBean;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotOrderResp;
import com.citc.nce.tempStore.vo.UseTempVariableOrder;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.service
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:19
 * @Description: 机器人设置--指令管理Service层
 * @Version: 1.0
 */
public interface RobotOrderService {


    public void saveOrder(RobotOrderReq robotOrderReq, BaseUser user);

    public void removeOrder(RobotOrderReq robotOrderReq, BaseUser user);

    public void editOrder(RobotOrderReq robotOrderReq);

    public RobotOrderResp listAll(OrderPageParam pageParam);

    public RobotOrderResp listOrderByName(String name);

    RobotOrderBean queryOneById(Long id);

    List<Long> checkListUseTemp(Map<Long, RobotOrderReq> map);

    void saveListUseTemp(UseTempVariableOrder data);

    List<RobotOrderBean> queryByTsOrderId(Long tsOrderId);


    void removeIds(List<Long> ids);
}
