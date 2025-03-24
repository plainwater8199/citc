package com.citc.nce.auth.sharelink.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class ShareLinkResp implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 账户id
     */
    @ApiModelProperty(value ="账户id")
    private String chatbotAccountId;

    /**
     * 链接名称
     */
    @ApiModelProperty(value ="链接名称")
    private String linkName;

    /**
     * 链接信息
     */
    @ApiModelProperty(value ="链接信息")
    private String linkInfo;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
