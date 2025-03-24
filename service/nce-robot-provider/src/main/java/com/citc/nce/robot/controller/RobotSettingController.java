package com.citc.nce.robot.controller;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.service.RobotSettingService;
import com.citc.nce.robot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 15:01
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class RobotSettingController implements RebotSettingApi {

    @Resource
    private RobotSettingService robotSettingService;

    @PostMapping("/setting/save")
    @XssCleanIgnore
    public int saveRebotSettingReq(@RequestBody @Valid RebotSettingReq rebotSettingReq) {
        log.info("rebotSettingReq  user controller enter" + rebotSettingReq);
        return robotSettingService.saveRebotSettingReq(rebotSettingReq);
    }

    @GetMapping("/setting/addAuditLastReplyForChatbotAccount")
    public void auditLastReplyForChatbot(@RequestParam("chatbotAccount") @NotEmpty String chatbotAccount) {
        robotSettingService.auditLastReplyForChatbot(chatbotAccount);
    }

    @PostMapping("/setting/list")
    @XssCleanIgnore
    public RebotSettingResp getRebotSettingReq(RebotSettingQueryReq rebotSettingQueryReq) {
        return robotSettingService.getRebotSettingReq(rebotSettingQueryReq);
    }

    @PostMapping("/setting/getButtonByUuid")
    @XssCleanIgnore
    public RobotProcessButtonResp getButtonByUuid(@RequestBody String uuid) {
        return robotSettingService.getButtonByUuid(uuid);
    }

    @Override
    public RobotBaseSettingVo getRobotBaseSetting() {
        return robotSettingService.getRobotBaseSetting();
    }

    @Override
    public void configBaseSetting(RobotBaseSettingVo baseConfig) {
        robotSettingService.configBaseSetting(baseConfig);
    }

    @Override
    public RobotDefaultReplySettingVo getDefaultReplyConfig() {
        return robotSettingService.getDefaultReplyConfig();
    }

    @Override
    public void configDefaultReply(RobotDefaultReplySettingVo settingVo) {
        robotSettingService.configDefaultReply(settingVo);
    }

}
