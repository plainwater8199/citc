package com.citc.nce.robot;

import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.bean.RobotOrderBean;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotOrderResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:50
 * @Description: 机器人设置--指令管理控制层API
 * @Version: 1.0
 * 变量API使用相同的value，需要用contextId区分，不然会报错
 */
@FeignClient(value = "rebot-service", contextId = "order-service", url = "${robot:}")
public interface RobotOrderApi {

    @PostMapping("/robot/order/save")
    public void saveOrder(@RequestBody RobotOrderReq robotOrderReq);

    @PostMapping("/robot/order/queryByTsOrderId")
    List<RobotOrderBean> queryByTsOrderId(@RequestBody RobotOrderReq req);

    @PostMapping("/robot/order/edit")
    public void compileOrder(@RequestBody RobotOrderReq robotOrderReq);

    @PostMapping("/robot/order/list")
    public RobotOrderResp listAllOrder(@RequestBody OrderPageParam pageParam);

    @PostMapping("/robot/order/queryOneById")
    RobotOrderBean queryOneById(@RequestBody OrderPageParam pageParam);

    @PostMapping("/robot/order/delete")
    public void removeOrder(@RequestBody RobotOrderReq robotOrderReq);

}
