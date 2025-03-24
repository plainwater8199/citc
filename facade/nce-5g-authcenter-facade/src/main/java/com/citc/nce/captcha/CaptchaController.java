package com.citc.nce.captcha;

import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cn.hutool.core.lang.UUID;
import com.citc.nce.authcenter.captcha.CaptchaApi;
import com.citc.nce.authcenter.captcha.CaptchaManageApi;
import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.req.EmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SelfEmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SmsCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;
import com.citc.nce.common.bean.CheckData;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.common.facadeserver.annotations.UnWrapResponse;
import com.citc.nce.common.facadeserver.verifyCaptcha.Aspect.VerifyCaptchaAspectImpl;
import com.citc.nce.common.facadeserver.verifyCaptcha.VerifyCaptchaV2;
import com.citc.nce.common.facadeserver.verifyCaptcha.resources.MyCaptchaStore;
import com.citc.nce.common.redis.config.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/6/29 16:27
 * @Version 1.0
 * @Description:
 */
@Api(tags = "验证码管理")
@RestController
public class CaptchaController {
    @Resource
    private CaptchaApi captchaApi;
    @Resource
    private VerifyCaptchaAspectImpl verifyCaptchaAspect;
    @Resource
    private RedisService redisService;
    @Resource
    private MyCaptchaStore myCaptchaStore;

    @Resource
    private CaptchaManageApi captchaManageApi;

    @ApiOperation("添加验证码图片资源")
    @PostMapping("/captcha/getImageCaptcha/addResource")
    @SkipToken
    @UnWrapResponse
    public String addResource(@RequestBody List<String> urls) {
        myCaptchaStore.addResource(urls);
        return "success";
    }


    @ApiOperation("更新图片验证码资源")
    @PostMapping("/captcha/getImageCaptcha/upgrade")
    @SkipToken
    @UnWrapResponse
    public String upgrade(@RequestBody List<String> urls) {
        myCaptchaStore.upgrade(urls);
        return "success";
    }

    @ApiOperation("获取旋转图片验证码")
    @PostMapping("/captcha/getImageCaptcha/rotate")
    @SkipToken
    @UnWrapResponse
    public CaptchaResponse<ImageCaptchaVO> getImageCaptcha() {
        CaptchaResponse<ImageCaptchaVO> response;
        try {
            response = verifyCaptchaAspect.generateCaptcha(CaptchaTypeConstant.ROTATE);
        } catch (Exception e) {
            // 更新验证码图片资源
            List<String> imageUrls = captchaManageApi.queryAllImageUrlForCaptcha();
            myCaptchaStore.updateCaptchaImages(imageUrls);
            response = verifyCaptchaAspect.generateCaptcha(CaptchaTypeConstant.ROTATE);
        }
        return response;
    }

    @ApiOperation("验证旋转图片验证码")
    @PostMapping("/captcha/getImageCaptcha/rotate/check")
    @SkipToken
    @UnWrapResponse
    public ApiResponse<?> checkCaptcha(@RequestBody @Valid CheckData checkData) {
        ApiResponse<?> matching = verifyCaptchaAspect.matching(checkData);
        if (matching.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", checkData.getId()));
        }
        return matching;
    }

    @ApiOperation("（未注册用户）csp注册获取验证码")
    @PostMapping("/captcha/getSmsCaptcha")
    @VerifyCaptchaV2
    public CaptchaResp getSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {
        return captchaApi.getSmsCaptcha(smsCaptchaReq);
    }

    @ApiOperation("（已注册用户）多因子发送验证码")
    @PostMapping("/captcha/getDyzSmsCaptcha")
    @VerifyCaptchaV2
    public CaptchaResp getDyzSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {
        return captchaApi.getDyzSmsCaptcha(smsCaptchaReq);
    }

    @ApiOperation("获取邮箱验证码")
    @PostMapping("/captcha/getEmailCaptcha")
    @SkipToken
    @VerifyCaptchaV2
    public CaptchaResp getEmailCaptcha(@RequestBody @Valid EmailCaptchaReq emailCaptchaReq) {
        return captchaApi.getEmailCaptcha(emailCaptchaReq);
    }


    @ApiOperation("获取-本人已注册-多因子发送验证码")
    @PostMapping("/selfCaptcha/getSelfDyzSmsCaptcha")
    @VerifyCaptchaV2
    public CaptchaResp getSelfDyzSmsCaptcha(@RequestBody @Valid SmsCaptchaReq smsCaptchaReq) {
        return captchaApi.getSelfDyzSmsCaptcha(smsCaptchaReq);
    }

    @ApiOperation("获取-本人-邮箱验证码")
    @PostMapping("/selfCaptcha/getSelfEmailCaptcha")
    @VerifyCaptchaV2
    public CaptchaResp getSelfEmailCaptcha(@RequestBody @Valid SelfEmailCaptchaReq emailCaptchaReq) {
        return captchaApi.getSelfEmailCaptcha(emailCaptchaReq);
    }

    @ApiOperation("校验验证码并申请二次验证码验证")
    @PostMapping("/VerifyCaptchaV2/applyPhone")
    CaptchaCheckResp applyPhoneVerifyCaptchaV2(@RequestBody @Valid CaptchaCheckReq captchaCheckReq) {
        CaptchaCheckResp captcha = captchaApi.checkCaptcha(captchaCheckReq);
        if (Boolean.TRUE.equals(captcha.getResult())) {
            String uuid = UUID.fastUUID().toString();
            redisService.setCacheObject("myVerifyCaptchaV2:" + uuid, captchaCheckReq.getAccount(), 10L, TimeUnit.MINUTES);
            captcha.setMyVerifyCaptchaV2(uuid);
        }
        return captcha;
    }
}
