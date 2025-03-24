package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MenuRecordResp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("chatbotId")
    private String chatbotId;

    @ApiModelProperty("账户管理Id")
    private String accountManagementId;

    @ApiModelProperty("菜单状态")
    private Integer menuStatus;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("提交时间")
    private Date submitTime;

    @ApiModelProperty("审核结果")
    private String result;

    @ApiModelProperty("版本号")
    private Integer version;

    private String submitUser;

    @ApiModelProperty("父菜单")
    private List<MenuParentResp> menuParentRespList;
}
