package com.citc.nce.filecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jcrenc
 * @since 2024/7/16 10:07
 */
@Getter
@RequiredArgsConstructor
public enum UploadFileType {

    MATERIAL_IMAGE("素材-图片"),
    MATERIAL_AUDIO("素材-音频"),
    MATERIAL_VIDEO("素材-视频"),
    MATERIAL_FILE("素材-文件"),
    CUSTOM_LOG("自定义log"),
    BLACK_LIST("黑名单"),
    H5_FORM("h5表单");

    private final String desc;
}
