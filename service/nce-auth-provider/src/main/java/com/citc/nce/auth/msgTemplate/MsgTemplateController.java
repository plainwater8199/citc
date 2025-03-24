package com.citc.nce.auth.msgTemplate;

import com.citc.nce.auth.csp.msgTemplate.MsgTemplateApi;
import com.citc.nce.auth.msgTemplate.service.MsgTemplateService;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController()
@Slf4j
public class MsgTemplateController implements MsgTemplateApi {

    @Resource
    private MsgTemplateService msgTemplateService;


    @Override
    public String templateContentQuery(MsgTypeEnum msgType, Long templateId,String customerId) {
        return msgTemplateService.templateContentQuery(msgType, templateId,customerId);
    }
}
