package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * <p>回调参数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/14 15:10
 */
@Data
public class AimSMSCallBackReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "回调对象")
    @NotEmpty
    private List<AimCallBack> callBackList;
}
