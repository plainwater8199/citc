package com.citc.nce.auth.captcha;

import com.citc.nce.authcenter.captcha.CaptchaManageApi;
import com.citc.nce.authcenter.captcha.vo.CaptchaImageInfo;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageBatchInsertReq;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaImageQueryListReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.verifyCaptcha.resources.MyCaptchaStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author jiancheng
 */
//@BossAuth({"/chatbot-view/directive/produce","/chatbot-view/shop/storeroom-add","/chatbot-view/shop/storeroom-edit"})
@RestController
@Api(value = "CaptchaManageController", tags = "验证码管理")
@RequiredArgsConstructor
@RequestMapping("captcha")
public class CaptchaManageImageController {
    private final CaptchaManageApi captchaManageApi;
    private final ApiTLainROTATE apiTLainROTATE;



    @PostMapping("/image/batchInsert")
    @ApiOperation("验证码管理-新增验证码图片")
    public void batchInsert(CaptchaImageBatchInsertReq req) {
        captchaManageApi.batchInsert(req);

        //更新本地缓存
        List<String> imageUrls = captchaManageApi.queryAllImageUrlForCaptcha();
        System.out.println("图片的数量："+imageUrls);
        apiTLainROTATE.upgrade(imageUrls);

    }

    @PostMapping("image/queryList")
    @ApiOperation("验证码管理-验证码图片查询")
    public PageResult<CaptchaImageInfo> queryList(@RequestBody @Valid CaptchaImageQueryListReq req) {
        return captchaManageApi.queryList(req);
    }

    @PostMapping("image/delete/{id}")
    @ApiOperation("验证码管理-删除")
    public void delete(@PathVariable("id") Long id) {
        captchaManageApi.delete(id);

        //更新本地缓存
        List<String> imageUrls = captchaManageApi.queryAllImageUrlForCaptcha();
        System.out.println("图片的数量："+imageUrls);
        apiTLainROTATE.upgrade(imageUrls);
    }

    @SkipToken
    @GetMapping("image/queryInfo")
    @ApiOperation("验证码管理-图片查询")
    public ResponseEntity<byte[]> imageQuery(@RequestParam("id") Long id) {
        return captchaManageApi.imageQuery(id);
    }

}
