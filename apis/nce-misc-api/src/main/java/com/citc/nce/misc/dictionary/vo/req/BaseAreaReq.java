package com.citc.nce.misc.dictionary.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BaseAreaReq {

    /**
     * 上级路径ID
     */
    @NotNull
    private String parentId;

}
