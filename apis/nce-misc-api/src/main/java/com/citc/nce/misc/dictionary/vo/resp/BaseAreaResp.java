package com.citc.nce.misc.dictionary.vo.resp;

import lombok.Data;

@Data
public class BaseAreaResp {
    private String baseId;

    /**
     * 地区名字
     */
    private String name;

    private String parentId;
}
