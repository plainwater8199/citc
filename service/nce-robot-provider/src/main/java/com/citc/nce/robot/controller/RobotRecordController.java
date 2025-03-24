package com.citc.nce.robot.controller;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.RobotRecordApi;
import com.citc.nce.robot.service.RobotRecordService;
import com.citc.nce.robot.vo.RobotRecordPageReq;
import com.citc.nce.robot.vo.RobotRecordPageResultResp;
import com.citc.nce.robot.vo.RobotRecordReq;
import com.citc.nce.robot.vo.RobotRecordResp;
import com.citc.nce.robot.vo.RobotRecordSaveReq;
import com.citc.nce.robot.vo.RobotRecordStatisticResp;
import com.citc.nce.robot.vo.SendQuantityResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class RobotRecordController implements RobotRecordApi {

    @Resource
    RobotRecordService robotRecordService;

    @PostMapping("/robot/record/pageList")
    @Override
    public RobotRecordPageResultResp pageRobotRecordList(@RequestBody @Valid RobotRecordPageReq robotRecordPageReq) {
        return robotRecordService.pageRobotRecordList(robotRecordPageReq);
    }

    @PostMapping("/robot/record/save")
    @Override
    public int saveRobotRecord(@RequestBody @Valid RobotRecordReq robotRecordReq) {
        return robotRecordService.saveRobotRecord(robotRecordReq);
    }

    @Override
    public RobotRecordResp saveRobotRecord(@RequestBody @Valid RobotRecordSaveReq robotRecordReq) {
        return robotRecordService.saveRobotRecord(robotRecordReq);
    }
    @Override
    public int updateById(RobotRecordResp robotRecordDo){
        return robotRecordService.updateById(robotRecordDo);
    }

    @Override
    public List<SendQuantityResp> queryChannelSendQuantity() {
        return robotRecordService.queryChannelSendQuantity();
    }

    @PostMapping("/robot/record/getRobotRecordStatisticByTime")
    @Override
    public PageResult<RobotRecordStatisticResp> getRobotRecordStatisticByTime(@RequestBody @Valid RobotRecordPageReq req) {
        return robotRecordService.getRobotRecordStatisticByTime(req);
    }

}
