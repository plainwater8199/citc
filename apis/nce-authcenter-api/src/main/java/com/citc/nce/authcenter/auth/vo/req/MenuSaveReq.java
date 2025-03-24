package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:09
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MenuSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "chatbotAccountId不能为空")
    @ApiModelProperty(value = "chatbotAccountId", required = true)
    private String chatbotAccountId;

    @NotEmpty(message = "菜单列表不能为空")
    @ApiModelProperty("菜单列表")
    List<MenuParentReq> menuParentReqList;

    private Integer version;

}
