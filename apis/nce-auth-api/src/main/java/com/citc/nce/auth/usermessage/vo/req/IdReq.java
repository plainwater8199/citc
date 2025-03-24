package com.citc.nce.auth.usermessage.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@Data
public class IdReq {

    @NotNull
    @ApiModelProperty(value = "查询或删除id", example = "1")
    private Long id;
}
