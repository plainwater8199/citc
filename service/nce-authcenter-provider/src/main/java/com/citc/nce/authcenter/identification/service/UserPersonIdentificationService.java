package com.citc.nce.authcenter.identification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.identification.vo.req.PersonIdentificationReq;
import com.citc.nce.authcenter.identification.entity.UserPersonIdentificationDo;

/**
 * @author jiancheng
 */
public interface UserPersonIdentificationService extends IService<UserPersonIdentificationDo> {

    /**
     * 个人实名认证申请
     *
     * @param personIdentificationReq
     */
    void personIdentificationApply(PersonIdentificationReq personIdentificationReq);
}
