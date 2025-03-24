package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.vo.*;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:09
 * @Version: 1.0
 * @Description:
 */
public interface RobotRecordService {

    RobotRecordPageResultResp pageRobotRecordList(RobotRecordPageReq robotRecordPageReq);

    int saveRobotRecord(RobotRecordReq robotRecordReq);

    RobotRecordResp saveRobotRecord(RobotRecordSaveReq robotRecordReq);

    int updateById(RobotRecordResp robotRecordDo);

    List<SendQuantityResp> queryChannelSendQuantity();

    PageResult<RobotRecordStatisticResp> getRobotRecordStatisticByTime(@RequestBody @Valid RobotRecordPageReq req);
}
