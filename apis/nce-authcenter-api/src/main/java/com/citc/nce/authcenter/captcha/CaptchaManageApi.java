package com.citc.nce.authcenter.captcha;

import com.citc.nce.authcenter.captcha.vo.CaptchaImageInfo;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageBatchInsertReq;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageQueryListReq;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "认证中心--图片验证码管理")
@FeignClient(value = "authcenter-service", contextId = "CaptchaManageApi", url = "${authCenter:}")
public interface CaptchaManageApi {
    @PostMapping(value ="/captcha/image/batchInsert",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation("验证码管理-新增验证码图片")
    void batchInsert(CaptchaImageBatchInsertReq req);
    @PostMapping("/captcha/image/queryList")
    @ApiOperation("验证码管理-验证码图片查询")
    PageResult<CaptchaImageInfo> queryList(@RequestBody CaptchaImageQueryListReq req);
    @PostMapping("/captcha/image/delete/{id}")
    @ApiOperation("验证码管理-删除")
    void delete(@PathVariable("id") Long id);
    @GetMapping("/image/queryInfo")
    @ApiOperation("验证码管理-图片查询")
    ResponseEntity<byte[]> imageQuery(@RequestParam("id") Long id);
    @GetMapping("/image/queryAllImageInfoForCaptcha")
    @ApiOperation("验证码管理-查询所有图片的详情")
    List<String> queryAllImageUrlForCaptcha();

}
