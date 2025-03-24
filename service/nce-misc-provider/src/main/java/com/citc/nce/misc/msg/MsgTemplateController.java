package com.citc.nce.misc.msg;

import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.citc.nce.misc.msg.service.MsgTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/18 15:29
 * @Version 1.0
 * @Description:
 */
@RestController
@Slf4j
public class MsgTemplateController implements MsgApi {
    @Autowired
    private MsgTemplateService msgTemplateService;

    @PostMapping("/msg/getMsgTemplateByCode")
    @Override
    public MsgTemplateResp getMsgTemplateByCode(@RequestBody @Valid MsgTemplateReq msgTemplateReq) {
        return msgTemplateService.getMsgTemplateByCode(msgTemplateReq);
    }
}
