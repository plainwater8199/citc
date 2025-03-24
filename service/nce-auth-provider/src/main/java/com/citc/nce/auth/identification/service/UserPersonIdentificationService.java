package com.citc.nce.auth.identification.service;

import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.auth.identification.vo.resp.WebPersonIdentificationResp;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:58
 * @description:
 */
public interface UserPersonIdentificationService {

    /**
     * 个人实名认证申请
     *
     * @param personIdentificationReq
     */
    void personIdentificationApply(PersonIdentificationReq personIdentificationReq);

    /**
     * 获取用户个人认证信息
     *
     * @param userId
     * @return
     */
    WebPersonIdentificationResp getPersonIdentification(String userId);

}
