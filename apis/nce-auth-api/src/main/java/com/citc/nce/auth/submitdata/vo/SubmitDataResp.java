package com.citc.nce.auth.submitdata.vo;

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
public class SubmitDataResp implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 表单id
     */
    @ApiModelProperty(value ="表单id")
    private Long formId;

    /**
     * 提交数据
     */
    @ApiModelProperty(value ="提交数据")
    private String submitValue;

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
