package com.citc.nce.robot.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.robot.RobotAccountApi;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 场景管理
 * @Author: yangchuang
 * @Date: 2022/7/7 17:18
 * @Version: 1.0
 * @Description:
 */

@RestController
@RequestMapping("/robot")
@Slf4j
@Api(value = "robot", tags = "机器人聊天记录")
public class RobotRecordController {
    @Resource
    RobotAccountApi robotAccountApi;

    @Resource
    RobotRecordApi robotRecordApi;
    @Resource
    private ECDHService ecdhService;

    /**
     * 查询聊天会话账户
     *
     * @return
     */
    @ApiOperation(value = "查询聊天会话账户", notes = "查询聊天会话账户")
    @PostMapping("/account/pagelist")
    public RobotAccountPageResultResp getRobotAccountList(@RequestBody @Valid RobotAccountPageReq robotAccountPageReq) {
        RobotAccountPageResultResp page = robotAccountApi.getRobotAccountList(robotAccountPageReq);
        if (CollectionUtil.isNotEmpty(page.getList())) {
            for (RobotAccountResp body : page.getList()) {
                body.setMobileNum(ecdhService.encode(body.getMobileNum()));
            }
        }
        return page;
    }

    /**
     * 机器人设置查询接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "聊天记录详情", notes = "聊天记录")
    @PostMapping("/record/pageList")
    public RobotRecordPageResultResp robotRecordDetail(@RequestBody @Valid RobotRecordPageReq robotRecordPageReq) {
        return robotRecordApi.pageRobotRecordList(robotRecordPageReq);
    }

    /**
     * 查询通道发送量占比
     * @return
     */
    @ApiOperation(value = "查询通道发送量占比", notes = "查询通道发送量占比")
    @GetMapping("/record/queryChannelSendQuantity")
    public List<SendQuantityResp> queryChannelSendQuantity() {
        return robotRecordApi.queryChannelSendQuantity();
    }
}
