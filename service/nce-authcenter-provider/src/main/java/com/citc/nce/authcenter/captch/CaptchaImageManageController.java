package com.citc.nce.authcenter.captch;


import com.citc.nce.authcenter.captch.service.CaptchaManageService;
import com.citc.nce.authcenter.captcha.CaptchaManageApi;
import com.citc.nce.authcenter.captcha.vo.CaptchaImageInfo;
import com.citc.nce.authcenter.captcha.vo.req.*;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class CaptchaImageManageController implements CaptchaManageApi {
    @Resource
    private CaptchaManageService captchaManageService;

    @Override
    public void batchInsert(CaptchaImageBatchInsertReq req) {
        captchaManageService.batchInsert(req);
    }

    @Override
    public PageResult<CaptchaImageInfo> queryList(CaptchaImageQueryListReq req) {
        return captchaManageService.queryList(req);
    }

    @Override
    public void delete(Long id) {
        captchaManageService.delete(id);
    }

    @Override
    public ResponseEntity<byte[]> imageQuery(Long id) {
        return captchaManageService.imageQuery(id);
    }

    @Override
    public List<String> queryAllImageUrlForCaptcha() {
        return captchaManageService.queryAllImageInfoForCaptcha();
    }

}
