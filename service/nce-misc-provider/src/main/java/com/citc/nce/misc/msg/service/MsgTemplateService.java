package com.citc.nce.misc.msg.service;

import com.citc.nce.misc.msg.entity.MsgTemplateDo;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/13 15:27
 * @Version: 1.0
 * @Description:
 */
public interface MsgTemplateService {
    /**
     * 根据消息code查询消息模板
     *
     * @param code
     * @return
     */
    MsgTemplateDo getMsgTemplateByCode(String code);

    MsgTemplateResp getMsgTemplateByCode(MsgTemplateReq msgTemplateReq);

}
