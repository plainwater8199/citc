package com.citc.nce.authcenter.captch.service;

import com.citc.nce.authcenter.captcha.vo.CaptchaImageInfo;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageBatchInsertReq;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageQueryListReq;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CaptchaManageService {
    void batchInsert(CaptchaImageBatchInsertReq req);

    PageResult<CaptchaImageInfo> queryList(CaptchaImageQueryListReq req);

    void delete(Long id);

    ResponseEntity<byte[]> imageQuery(Long id);

    List<String> queryAllImageInfoForCaptcha();
}
