package com.citc.nce.auth.formmanagement.vo;

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
public class FormManagementSaveResp implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value="id")
    private Long id;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "新增结果")
    private int success;


}
