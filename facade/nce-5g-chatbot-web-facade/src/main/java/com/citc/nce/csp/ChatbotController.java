package com.citc.nce.csp;

import cn.hutool.core.util.StrUtil;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTreeResp;
import com.citc.nce.auth.csp.chatbot.ChatbotApi;
import com.citc.nce.auth.csp.chatbot.vo.*;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.Examine;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.security.annotation.HasCsp;
import com.citc.nce.security.annotation.RepeatSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@Api(value = "ChatbotController", tags = "CSP--chatbot管理")
public class ChatbotController {

    @Resource
    private ChatbotApi chatbotApi;
    @Resource
    private ECDHService ecdhService;

    @PostMapping("/csp/chatbot/queryList")
    @ApiOperation(value = "分页查询机器人", notes = "列表查询")
    @HasCsp
    public PageResult<ChatbotResp> queryList(@RequestBody ChatbotReq req) {
        return chatbotApi.queryList(req);
    }
    @PostMapping("/csp/chatbot/queryBychatbotAccounts")
    @ApiOperation(value = "根据chatbotAccount集合查询chatbot信息", notes = "根据chatbotAccount集合查询chatbot信息")
    List<AccountManagementTreeResp> queryBychatbotAccounts(@RequestBody String chatbotAccounts)
    {
        if(StrUtil.isEmpty(chatbotAccounts)||StrUtil.isEmpty(chatbotAccounts.replace("\"","")))return  null;
        List<String> list= Arrays.asList(chatbotAccounts.replace("\"","").split(","));
        return chatbotApi.queryBychatbotAccounts(list);
    }

    @PostMapping("/csp/chatbot/status")
    @ApiOperation(value = "查询机器人状态", notes = "状态查询")
    public ChatbotGetStatusResp getChatbotStatus(@RequestBody ChatbotGetReq req) {
        return chatbotApi.getChatbotStatus(req);
    }

    @Examine
    @PostMapping("/csp/chatbot/saveCMCC")
    @ApiOperation(value = "新增非本地机器人", notes = "新增移动")
    @HasCsp
    @Log(title = "新增非本地机器人")
    public void saveCMCC(@RequestBody ChatbotSaveReq req) {
        chatbotApi.saveCMCC(req);
    }

    @Examine
    @PostMapping("/csp/chatbot/updateCMCCById")
    @ApiOperation(value = "变更", notes = "变更")
    @HasCsp
    @Log(title = "变更移动")
    public void updateCMCCById(@RequestBody ChatbotUpdateCMCCReq req) {
        chatbotApi.updateCMCCById(req);
    }

    @PostMapping("/csp/chatbot/activeOn")
    @ApiOperation(value = "上架", notes = "上架")
    @HasCsp
    @Log(title = "上架")
    public int activeOn(@RequestBody ChatbotActiveOnReq req) {
        return chatbotApi.activeOn(req);
    }

    @PostMapping("/csp/chatbot/queryByAccountManagementId")
    @ApiOperation(value = "查询chatbot信息(移动)", notes = "查询移动chatbot信息")
    public ChatbotGetResp queryByAccountManagementId(@RequestBody @Valid ChatbotGetReq req) {
        ChatbotGetResp body = chatbotApi.queryByAccountManagementId(req);
        body.setWhiteList(ecdhService.encode(body.getWhiteList()));
        return body;
    }

    @PostMapping("/csp/chatbot/setWhiteList")
    @ApiOperation(value = "设置调试白名单", notes = "设置调试白名单")
    @NotNull
    @HasCsp
    @Log(title = "设置调试白名单")
    public void setWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req) {
        chatbotApi.setWhiteList(req);
    }

    @PostMapping("/csp/chatbot/queryWhiteList")
    @ApiOperation(value = "获取调试白名单", notes = "获取调试白名单")
    @NotNull
    @HasCsp
    public List<ChatbotSetWhiteListResp> queryWhiteList(@RequestBody @Valid ChatbotSetWhiteListReq req) {
        return chatbotApi.queryWhiteList(req);
    }

    @PostMapping("/csp/chatbot/updateChatbotStatusByAccountManagementId")
    @ApiOperation(value = "上线/下线(更改机器人状态)", notes = "上线/下线(更改机器人状态)")
    @NotNull
    @HasCsp
    @Log(title = "上线/下线(更改机器人状态)")
    public int updateChatbotStatusByAccountManagementId(@RequestBody @Valid ChatbotStatusUpdateReq req) {
        return chatbotApi.updateChatbotStatusByAccountManagementId(req);
    }

    @PostMapping("/csp/chatbot/getOtherByAccountManagementId")
    @ApiOperation(value = "查询chatbot信息(非移动)", notes = "查询chatbot信息(非移动)")
    @NotNull
    @HasCsp
    public AccountManagementResp getOtherByAccountManagementId(@RequestBody @Valid ChatbotGetReq req) {
        return chatbotApi.getOtherByAccountManagementId(req);
    }

    @PostMapping("/csp/chatbot/logOffCmcc")
    @ApiOperation("注销非添加的移动Chatbot")
    @HasCsp
    public void logOffCmccRobot(@RequestBody @Valid ChatbotCmccLogOffReq req) {
        chatbotApi.logOffCmccRobot(req);
    }


    @PostMapping("/csp/chatbot/saveOther")
    @ApiOperation("新增本地机器人")
    @NotNull
    @HasCsp
    @Log(title = "新增本地机器人")
    public void saveLocalChatbot(@RequestBody @Valid ChatbotOtherSaveReq req) {
        chatbotApi.saveLocalChatbot(req);
    }

    @PostMapping("/csp/chatbot/checkForCanCreate")
    @ApiOperation("检查是否能创建Chatbot")
    @HasCsp
    public boolean checkForCanCreate(@RequestBody @Valid checkForCanCreateChatbotReq req) {
        return chatbotApi.checkForCanCreate(req);
    }

    @PostMapping("/csp/chatbot/updateOther")
    @ApiOperation("修改本地机器人")
    @NotNull
    @HasCsp
    @Log(title = "修改本地机器人")
    public void updateLocalRobot(@RequestBody @Valid ChatbotOtherUpdateReq req) {
        chatbotApi.updateLocalRobot(req);
    }

    @PostMapping("/csp/chatbot/logOffLocal")
    @ApiOperation("注销本地电联机器人")
    @NotNull
    @HasCsp
    public void logOffLocalRobot(@RequestBody @Valid ChatbotLocalLogOffReq req) {
        //合同添加的电联chatbot注销方法 见 UnicomAndTelecomApi.withdrawChatBot
        chatbotApi.logOffLocalRobot(req);
    }

    @PostMapping("/csp/chatbot/delete")
    @ApiOperation("删除电联机器人")
    @NotNull
    @HasCsp
    public void deleteLocalRobot(@RequestBody @Valid ChatbotLocalDeleteReq req) {
        chatbotApi.deleteLocalRobot(req);
    }

    @Examine
    @PostMapping("/csp/chatbot/supplier/save")
    @ApiOperation(value = "新增供应商chatbot")
    @HasCsp
    @RepeatSubmit(interval = 8000)
    public void saveSupplierChatbot(@RequestBody @Valid ChatbotSupplierAdd req) {
        chatbotApi.saveSupplierChatbot(req);
    }

    @Examine
    @PostMapping("/csp/chatbot/supplier/update")
    @ApiOperation(value = "更新供应商chatbot")
    @HasCsp
    @RepeatSubmit(interval = 8000)
    public void updateSupplierChatbot(@RequestBody @Valid ChatbotSupplierUpdate req) {
        chatbotApi.updateSupplierChatbot(req);
    }

    @PostMapping("/csp/chatbot/supplier/{id}/delete")
    @HasCsp
    @ApiOperation(value = "供应商chatbot删除")
    public void deleteSupplierChatbot(@PathVariable("id") Long id) {
        chatbotApi.deleteSupplierChatbot(id);
    };

    @PostMapping("/csp/chatbot/supplier/{id}/submit")
    @HasCsp
    @ApiOperation(value = "供应商chatbot提交")
    public void submitSupplierChatbot(@PathVariable("id") Long id) {
        chatbotApi.submitSupplierChatbot(id);
    };

    @GetMapping("/csp/chatbot/supplier/{id}")
    @ApiOperation(value = "查询供应商chatbot信息")
    @NotNull
    @HasCsp
    public ChatbotSupplierInfo getSupplierChatbotInfo(@PathVariable("id") Long id) {
        ChatbotSupplierInfo info = chatbotApi.getSupplierChatbotInfo(id);
        info.setWhiteList(ecdhService.encode(info.getWhiteList()));
        return info;
    }

    @GetMapping("/csp/chatbot/status/options")
    @HasCsp
    @ApiOperation(value = "查询供应商chatbot状态")
    public ChatbotStatusResp getChatbotStatusOptions() {
        return chatbotApi.getChatbotStatusOptions();
    };

    @PostMapping("/csp/chatbot/channel")
    @ApiOperation(value = "获取chatbot的通道")
    public ChatbotChannelResp getChatbotChannel(@RequestBody @Valid ChatbotChannelReq req) {
        return chatbotApi.getChatbotChannel(req);
    }

    @PostMapping("/csp/chatbot/new/permission")
    @HasCsp
    @ApiOperation(value = "新增chatbot权限")
    public Boolean getNewChatbotPermission(@RequestBody @Valid ChatbotNewPermissionReq req) {
        return chatbotApi.getNewChatbotPermission(req);
    };


    @PostMapping("/csp/chatbot/supplier/{id}/online")
    @ApiOperation("上线chatbot")
    @HasCsp
    public void onlineSupplierChatbot(@PathVariable("id") Long id) {
        chatbotApi.onlineSupplierChatbot(id);
    };

    @PostMapping("/csp/chatbot/supplier/{id}/offline")
    @ApiOperation("下线chatbot")
    @HasCsp
    public void offlineSupplierChatbot(@PathVariable("id") Long id) {
        chatbotApi.offlineSupplierChatbot(id);
    };
}
