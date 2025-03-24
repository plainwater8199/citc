package com.citc.nce.common.bean;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @author bydud
 * @since 2024/4/1
 */
@Data
public class CheckData {
    @NotBlank(message = "验证码id不能为空")
    private String id;
    @NotNull(message = "验证码结果不能为空")
    private ImageCaptchaTrack data;
}
