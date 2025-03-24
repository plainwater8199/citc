package com.citc.nce.im.config;

import cn.hutool.extra.spring.SpringUtil;

/**
 * bydud
 * 2024/1/3
 **/
public class LayoutStyleUtil {
    private LayoutStyleUtil() {
    }


    public static String appendPrefix(String styleFileId) {
        LayoutStyleConfig config = SpringUtil.getBean(LayoutStyleConfig.class);
        return config.getCssDownLoadUrI() + styleFileId;
    }

}
