package com.citc.nce.auth.sharelink.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class ShareLinkH5Resp implements Serializable {

    /**
     * 账号名称
     */
    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 账号名称
     */
    @ApiModelProperty(value="账号名称")
    private String accountName;;

    /**
     * 账号id
     */
    @ApiModelProperty(value="账号id")
    private String chatbotAccountId;

    private String chatbotAccount;

    /**
     * 校验结果
     */
    @ApiModelProperty(value="校验结果")
    private boolean checkStatus;

    /**
     * 图片
     */
    @ApiModelProperty(value="图片id")
    private String imgId;
}
