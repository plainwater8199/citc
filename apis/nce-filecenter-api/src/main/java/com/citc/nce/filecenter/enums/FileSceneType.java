package com.citc.nce.filecenter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSceneType {


    H5("H5","h5场景"),
    RICH_TEXT("richText","富文本");

    private final String code;
    private final String desc;
}
