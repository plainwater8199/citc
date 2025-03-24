package com.citc.nce.rebot.manage;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.RobotOrderApi;
import com.citc.nce.robot.bean.OrderPageParam;
import com.citc.nce.robot.vo.RobotOrderReq;
import com.citc.nce.robot.vo.RobotOrderResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.rebot.manage
 * @Author: weilanglang
 * @CreateTime: 2022-07-12  16:17
 * @Description: 机器人设置--指令管facade层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/robot")
@Api(value = "order",tags = "机器人设置--指令管理API")
public class RobotOrderController {


    @Resource
    RobotOrderApi robotOrderApi;

    @PostMapping("/order/save")
    @ApiOperation(value = "指令保存", notes = "指令保存")
    public void saveOrder(@RequestBody RobotOrderReq robotOrderReq){
        robotOrderApi.saveOrder(robotOrderReq);
    }

    @PostMapping("/order/edit")
    @ApiOperation(value = "指令编辑", notes = "指令编辑")
    public void compileOrder(@RequestBody RobotOrderReq robotOrderReq){
        robotOrderApi.compileOrder(robotOrderReq);
    }

    @PostMapping("/order/list")
    @ApiOperation(value = "获取所有/指定id指令", notes = "获取所有/指定id指令")
    public RobotOrderResp listAllOrder(@RequestBody OrderPageParam pageParam){
        return robotOrderApi.listAllOrder(pageParam);
    }

    @PostMapping("/order/delete")
    @ApiOperation(value = "指令删除", notes = "指令删除")
    public void removeOrder(@RequestBody RobotOrderReq robotOrderReq){
        robotOrderApi.removeOrder(robotOrderReq);
    }
}
