package com.citc.nce.authcenter.largeModel.vo;

import lombok.Data;

/**
 * 文件名:LargeModelPromptSettingResp
 * 创建者:zhujinyu
 * 创建时间:2024/5/14 10:40
 * 描述:
 */
@Data
public class LargeModelPromptSettingResp {
    /**
     * 设定
     */
    private String settings;

    /**
     * 示例
     */
    private String examples;

    /**
     * 规则和示例
     */
    private String ruleAndFormats;

    /**
     * 规则和示例
     */
    private LargeModelResp largeModelResp;

}
