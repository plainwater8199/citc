package com.citc.nce.misc.msg.service.impl;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.misc.constant.MiscErrorCode;
import com.citc.nce.misc.msg.entity.MsgTemplateDo;
import com.citc.nce.misc.msg.mapper.MsgTemplateMapper;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.citc.nce.misc.msg.service.MsgTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/13 15:27
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
public class MsgTemplateServiceImpl implements MsgTemplateService {

    @Resource
    private MsgTemplateMapper msgTemplateMapper;

    @Override
    public MsgTemplateDo getMsgTemplateByCode(String code) {
        return msgTemplateMapper.selectOne(MsgTemplateDo::getTempldateCode, code);
    }


    @Override
    public MsgTemplateResp getMsgTemplateByCode(MsgTemplateReq msg) {
        //查询模板格式内容
        MsgTemplateDo msgTemplateDo = msgTemplateMapper.selectOne(MsgTemplateDo::getTempldateCode,msg.getTempldateCode());
        if (null == msgTemplateDo) {
            log.error("getMsgTemplateByCode error message is === " + MiscErrorCode.Execute_SQL_QUERY + ";the query data is null, please confirm whether the parameters are correct!");
            throw new BizException(MiscErrorCode.Execute_SQL_QUERY);
        }
        MsgTemplateResp msgTemplateResp = new MsgTemplateResp();
        BeanUtils.copyProperties(msgTemplateDo, msgTemplateResp);
        return msgTemplateResp;
    }
}
