package com.citc.nce.robot;

import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:06
 * @Version: 1.0
 * @Description:
 */


@FeignClient(value = "rebot-service", contextId = "RebotSetting", url = "${robot:}")
public interface RebotSettingApi {
    /**
     * 机器人设置保存
     *
     * @param rebotSettingReq
     * @return
     */
    @PostMapping("/setting/save")
    int saveRebotSettingReq(@RequestBody @Valid RebotSettingReq rebotSettingReq);

    /**
     * 增加chatbot時，兜底回復同步發送到運營商審核
     *
     * @param chatbotAccount
     */
    @GetMapping("/setting/addAuditLastReplyForChatbotAccount")
    void auditLastReplyForChatbot(@RequestParam("chatbotAccount") @NotEmpty String chatbotAccount);

    /**
     * 机器人设置查询接口
     *
     * @param
     * @return
     */
    @PostMapping("/setting/list")
    RebotSettingResp getRebotSettingReq(@RequestBody RebotSettingQueryReq rebotSettingQueryReq);

    @PostMapping("/setting/getButtonByUuid")
    RobotProcessButtonResp getButtonByUuid(@RequestBody String uuid);

    @PostMapping("/setting/getRobotBaseSetting")
    RobotBaseSettingVo getRobotBaseSetting();

    @PostMapping("/setting/configBaseSetting")
    void configBaseSetting(@RequestBody RobotBaseSettingVo baseConfig);

    @PostMapping("/setting/getDefaultReplyConfig")
    RobotDefaultReplySettingVo getDefaultReplyConfig();

    @PostMapping("/setting/configDefaultReply")
    void configDefaultReply(@RequestBody RobotDefaultReplySettingVo settingVo);

}
