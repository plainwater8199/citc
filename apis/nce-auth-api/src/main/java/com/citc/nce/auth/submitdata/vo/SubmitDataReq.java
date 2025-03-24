package com.citc.nce.auth.submitdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class SubmitDataReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表单id
     */
    @ApiModelProperty(value ="表单id")
    @NotNull(message = "账户id不能为空")
    private Long formId;

    /**
     * 提交数据
     */
    @ApiModelProperty(value ="提交数据")
    @NotBlank(message = "提交数据不能为空")
    private String submitValue;

}
