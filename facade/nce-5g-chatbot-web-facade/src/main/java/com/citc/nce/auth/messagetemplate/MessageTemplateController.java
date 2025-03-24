package com.citc.nce.auth.messagetemplate;

import cn.hutool.core.util.StrUtil;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.messagetemplate.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 15:57
 * @Version: 1.0
 * @Description: 消息模板操作
 */
@RestController
@RequestMapping("/message/template")
@Slf4j
@Api(value = "auth", tags = "消息模板功能")
public class MessageTemplateController {
    @Resource
    private MessageTemplateApi messageTemplateApi;
    @Resource
    private ObjectMapper objectMapper;

    /**
     * 消息模板列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取5G模板列表", notes = "消息模板列表分页获取")
    @PostMapping("/get5gTemplatesList")
    @XssCleanIgnore
    public MessageTemplatePageResp get5gTemplatesList(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq) {
        return messageTemplateApi.get5gTemplatesList(messageTemplatePageReq);
    }


    /**
     * 消息模板列表分页获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "消息模板列表分页获取", notes = "消息模板列表分页获取")
    @PostMapping("/pageList")
    public PageResultResp getMessageTemplates(@RequestBody @Valid MessageTemplatePageReq messageTemplatePageReq) {
        return messageTemplateApi.getMessageTemplates(messageTemplatePageReq);
    }

    /**
     * 模板直接送审接口
     *
     * @param templateId 模板id
     * @param operators  operators
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "templateId", dataType = "Long", required = true),
            @ApiImplicitParam(name = "operators", value = "operators", dataType = "String", required = true)
    })
    @ApiOperation(value = "模板直接送审接口", notes = "模板直接送审接口")
    @GetMapping(value = "/public", produces = "application/json;charset=utf-8")
    public String publicTemplate(@RequestParam("templateId") @NotNull Long templateId, @RequestParam("operators") @NotNull String operators,@RequestParam("isChecked") Integer isChecked) {
        String result = messageTemplateApi.publicTemplate(templateId, operators,isChecked);
        return StrUtil.isEmpty(result) ? "送审成功" : result;

    }

    /**
     * 新增消息模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增消息模板", notes = "新增消息模板")
    @PostMapping("/save")
    @XssCleanIgnore
    public MessageTemplateIdResp saveMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateReq) {
       return messageTemplateApi.saveMessageTemplate(messageTemplateReq);
       }

    /**
     * 修改消息模板
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改消息模板", notes = "修改消息模板")
    @PostMapping("/edit")
    @XssCleanIgnore
    public MessageTemplateIdResp updateMessageTemplate(@RequestBody @Valid MessageTemplateReq messageTemplateEditReq) {
        return messageTemplateApi.updateMessageTemplate(messageTemplateEditReq);
    }

    @ApiOperation(value = "获取模板已审核通过的chatbot账号", notes = "获取模板已审核通过的chatbot账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "templateId", dataType = "Long", required = true),

    })
    @PostMapping("/getAccountForProvedTemplateCarrier")
    public List<AccountManagementResp> getAccountForProvedTemplateCarrier(@RequestParam("id") @NotNull Long id) {
        return messageTemplateApi.getAccountForProvedTemplateCarrier(id);

    }

    /**
     * 删除消息模板
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true)
    })
    @ApiOperation(value = "删除消息模板", notes = "删除消息模板")
    @PostMapping("/delete")
    public int delMessageTemplateById(@RequestParam("id") Long id) {
        return messageTemplateApi.delMessageTemplateById(id);
    }

    /**
     * 获取单个消息模板
     *
     * @param
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true)
    })
    @ApiOperation(value = "获取单个消息模板", notes = "获取单个消息模板")
    @PostMapping("/getOne")
    @XssCleanIgnore
    public MessageTemplateResp getMessageTemplateById(@RequestParam("id") Long id) {
        MessageTemplateResp message = messageTemplateApi.getMessageTemplateById(id);
        if (Objects.isNull(message)) return null;
        if (!SessionContextUtil.getUser().getUserId().equals(message.getCreator())) {
            throw new BizException("模板不是你的");
        }
        return message;
    }

    //    @ApiImplicitParams({
//            @ApiImplicitParam(name = "id", value = "id", dataType = "Long", required = true)
//    })
    @ApiOperation(value = "获取单个审核通过消息模板", notes = "获取单个审核通过消息模板")
    @PostMapping("/getProvedOne")
    @XssCleanIgnore
    public MessageTemplateResp getProvedOne(@RequestBody MessageTemplateProvedReq messageTemplateProvedReq) {
        MessageTemplateResp message = messageTemplateApi.getProvedTemplate(messageTemplateProvedReq);
        if (Objects.isNull(message)) return null;
        if (!SessionContextUtil.getUser().getUserId().equals(message.getCreator())) {
            throw new BizException("模板不是你的");
        }
        return message;
    }

    /**
     * 获取消息模板树
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取消息模板树", notes = "获取消息模板树")
    @GetMapping("/getTreeList")
    public List<MessageTemplateTreeResp> getTreeList() {
        return messageTemplateApi.getTreeList();
    }

    @ApiOperation(value = "获取审核通过的消息模板树", notes = "获取审核通过的消息模板树")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "templateSource", value = "模板来源 1 群发计划 2 机器人流程 ", dataType = "Integer", required = true),
//            @ApiImplicitParam(name = "accounts", value = "模板涉及的chatbotAccount账号", dataType = "String", paramType = "body")
//    })
    @PostMapping("/getProvedTreeList")
    @XssCleanIgnore
    public List<MessageTemplateTreeResp> getProvedTreeList(@RequestBody @NotNull MessageTemplateProvedForQueryReq queryReq) {
        return messageTemplateApi.getProvedTreeList(queryReq);
    }

    @SkipToken
    @ApiOperation(value = "模板审核状态回调", notes = "模板审核状态回调")
    @PostMapping("/template/status")
    public void templateStatusCallback(RequestEntity<String> requestEntity, HttpServletResponse httpServletResponse) {
        log.info("收到模板审核状态回调: body:{}", requestEntity.getBody());
        TemplateStatusCallbackReq templateStatusCallbackReq;
        try {
            templateStatusCallbackReq = objectMapper.readValue(requestEntity.getBody(), TemplateStatusCallbackReq.class);
        } catch (JsonProcessingException e) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            log.error("解析模板审核状态回调消息失败", e);
            return;
        }
        messageTemplateApi.templateStatusCallabck(templateStatusCallbackReq);
    }


}
