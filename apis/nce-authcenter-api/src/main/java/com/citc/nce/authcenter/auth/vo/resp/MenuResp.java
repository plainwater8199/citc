package com.citc.nce.authcenter.auth.vo.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuResp {

    @ApiModelProperty("菜单状态 0：待审核，1：审核通过，2：审核不通过")
    private Integer menuStatus;

    @ApiModelProperty("当前菜单")
    private List<MenuParentResp> menuParentRespList;

    @ApiModelProperty("提交记录")
    private List<MenuRecordResp> menuRecordRespList;

    private Integer version;

    private Boolean isShowReduction;
}