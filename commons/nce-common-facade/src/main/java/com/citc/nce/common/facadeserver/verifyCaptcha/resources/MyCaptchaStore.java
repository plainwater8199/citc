package com.citc.nce.common.facadeserver.verifyCaptcha.resources;


import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.resource.impl.provider.URLResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;

import java.util.List;
import static cloud.tianai.captcha.generator.impl.StandardRotateImageCaptchaGenerator.TEMPLATE_ACTIVE_IMAGE_NAME;
import static cloud.tianai.captcha.generator.impl.StandardRotateImageCaptchaGenerator.TEMPLATE_FIXED_IMAGE_NAME;

/**
 * @Author: 天爱有情
 * @date 2022/7/11 14:22
 * @Description 负责模板和背景图存储的地方
 */
@Component
public class MyCaptchaStore {

    private static final Logger log = LoggerFactory.getLogger(MyCaptchaStore.class);
    @Autowired
    private ResourceStore resourceStore;



    @PostConstruct
    public void init() {
        // 滑块验证码 模板 (系统内置)
        ResourceMap template3 = new ResourceMap("default", 4);
        template3.put(TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "template/active.png", "default"));
        template3.put(TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, "template/fixed.png", "default"));
        resourceStore.addTemplate(CaptchaTypeConstant.ROTATE, template3);
        resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource("classpath", "bgimages/01.png", "default"));
    }


    /**
     * 添加选择图片资源
     *
     * @param urls 下载地址
     */
    public void addResource(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) return;
        urls.forEach(url -> resourceStore.addResource(CaptchaTypeConstant.ROTATE, new Resource(URLResourceProvider.NAME, url, "default")));
    }

    /**
     * 添加选择图片资源
     *
     * @param urls 下载地址
     */
    public void upgrade(List<String> urls) {
        resourceStore.clearAllResources();
        addResource(urls);
    }



    //更新classpath的缓存图片
    public void updateCaptchaImages(List<String> captchaImageUrls) {
        if (!CollectionUtils.isEmpty(captchaImageUrls)){
            upgrade(captchaImageUrls);
        }
    }
}
