package com.citc.nce.misc.msg;

import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/18 15:32
 * @Version 1.0
 * @Description:
 */
@FeignClient(value = "misc-service", contextId = "MsgApi", url = "${miscServer:}")
public interface MsgApi {

    /**
     * 通过code查询模板内容信息
     *
     * @param msgTemplateReq
     * @return {@link MsgTemplateResp}
     */
    @PostMapping("/msg/getMsgTemplateByCode")
    MsgTemplateResp getMsgTemplateByCode(@RequestBody @Valid MsgTemplateReq msgTemplateReq);
}
