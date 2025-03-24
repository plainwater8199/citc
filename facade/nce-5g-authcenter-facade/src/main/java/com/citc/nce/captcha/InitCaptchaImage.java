package com.citc.nce.captcha;

import com.citc.nce.authcenter.captcha.CaptchaManageApi;
import com.citc.nce.common.facadeserver.verifyCaptcha.resources.MyCaptchaStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class InitCaptchaImage implements ApplicationRunner {

    @Resource
    private CaptchaManageApi captchaManageApi;

    @Resource
    private MyCaptchaStore myCaptchaStore;

    @Override
    public void run(ApplicationArguments args) {
        //更新本地缓存
        log.info("----------------初始化图片验证码到本地缓存----------------");
        try {
            List<String> imageUrls = captchaManageApi.queryAllImageUrlForCaptcha();
            System.out.println("图片的数量："+imageUrls);
            myCaptchaStore.updateCaptchaImages(imageUrls);
        } catch (Exception e) {
            log.info("----------------初始化图片验证码到本地缓存----------失败----【authCenter-service、fileCenter-service】服务启动后再启动】--");
        }

    }
}
