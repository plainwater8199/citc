package com.citc.nce.robot;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.vo.RobotRecordPageReq;
import com.citc.nce.robot.vo.RobotRecordPageResultResp;
import com.citc.nce.robot.vo.RobotRecordReq;
import com.citc.nce.robot.vo.RobotRecordResp;
import com.citc.nce.robot.vo.RobotRecordSaveReq;
import com.citc.nce.robot.vo.RobotRecordStatisticResp;
import com.citc.nce.robot.vo.SendQuantityResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = "rebot-service",contextId="RobotRecordApi", url = "${robot:}")
public interface RobotRecordApi {

    /**
     * 聊天记录查询
     *
     * @return
     */
    @PostMapping("/robot/record/pageList")
    RobotRecordPageResultResp pageRobotRecordList(@RequestBody @Valid RobotRecordPageReq robotRecordPageReq);

    /**
     * 聊天记录保存
     *
     * @return
     */
    @PostMapping("/robot/record/save")
    int saveRobotRecord(@RequestBody @Valid RobotRecordReq robotRecordReq);
    @PostMapping("/robot/record/saveStr")
    RobotRecordResp saveRobotRecord(@RequestBody @Valid RobotRecordSaveReq robotRecordReq);

    @PostMapping("/robot/record/updateById")
    int updateById(@RequestBody RobotRecordResp robotRecordDo);

    @PostMapping("/robot/record/queryChannelSendQuantity")
    List<SendQuantityResp> queryChannelSendQuantity();

    @PostMapping("/robot/record/getRobotRecordStatisticByTime")
    PageResult<RobotRecordStatisticResp> getRobotRecordStatisticByTime(@RequestBody RobotRecordPageReq robotRecordPageReq);
}
