package com.citc.nce.robot.service;

import com.citc.nce.robot.vo.*;


/**
 * @Author: yangchuang
 * @Date: 2022/7/5 17:10
 * @Version: 1.0
 * @Description:
 */
public interface RobotSettingService {
    int saveRebotSettingReq(RebotSettingReq rebotSettingReq);
    void auditLastReplyForChatbot(String chatbotAccount);

    RebotSettingResp getRebotSettingReq(RebotSettingQueryReq rebotSettingQueryReq);

    RobotProcessButtonResp getButtonByUuid(String uuid);

    RobotBaseSettingVo getRobotBaseSetting();

    void configBaseSetting(RobotBaseSettingVo baseConfig);

    RobotDefaultReplySettingVo getDefaultReplyConfig();

    void configDefaultReply(RobotDefaultReplySettingVo settingVo);
}
