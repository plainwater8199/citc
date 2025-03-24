package com.citc.nce.auth.messagetemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateIdResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("id")
    private Long id;


    /**
     * 成功数量
     */
    @ApiModelProperty("成功数量")
    private int successNum;

    /**
     *方法返回描述
     */
    @ApiModelProperty("方法返回描述")
    private String desc;

    /**
     *更新后是否需要重新送审
     */
    @ApiModelProperty("更新后是否需要重新送审")
    private boolean needAudit;
    /**
     *
     */
    @ApiModelProperty("方法是否执行成功")
    private int code;
    public  MessageTemplateIdResp()
    {
        code=200;
    }
}
