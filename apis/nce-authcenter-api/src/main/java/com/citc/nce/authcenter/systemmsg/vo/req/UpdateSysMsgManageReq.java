package com.citc.nce.authcenter.systemmsg.vo.req;

import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateSysMsgManageReq extends SysMsgManageInfo {
    @NotNull
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;
}
