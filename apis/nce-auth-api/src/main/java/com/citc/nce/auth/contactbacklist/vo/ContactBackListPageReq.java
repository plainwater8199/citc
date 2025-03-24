package com.citc.nce.auth.contactbacklist.vo;

import com.citc.nce.common.core.pojo.PageParam;
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
public class ContactBackListPageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 联系人组id
     */
    @ApiModelProperty("联系人组id")
    private Long groupId;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String personName;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phoneNum;

    @ApiModelProperty("分页插件")
    private PageParam pageParam;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

}
