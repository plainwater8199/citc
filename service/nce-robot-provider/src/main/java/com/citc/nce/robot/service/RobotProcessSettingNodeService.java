package com.citc.nce.robot.service;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.vo.*;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 11:14
 * @Version: 1.0
 * @Description:
 */
public interface RobotProcessSettingNodeService {
    PageResultResp getRobotProcessSettings(RobotProcessSettingNodePageReq robotProcessSettingNodePageReq);

    RobotProcessSettingNodeResp getRobotProcessOne(Long id) ;

    Long saveRobotProcessSetting(RobotProcessSettingNodeReq robotProcessSettingNodeReq);

    int updateRobotProcessSetting(RobotProcessSettingNodeEditReq robotProcessSettingNodeEditReq);

    int deleteRobotProcessSettingById(Long id);

    List<RobotProcessSettingNodeResp> getRobotProcessSettingNodes();

    int updateRobotProcessSettingDerailById(RobotProcessSettingNodeEditDerailReq robotProcessSettingNodeEditDerailReq);

    RobotProcessSettingNodeResp getRobotProcessSettingById(Long id);
}
