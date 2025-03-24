package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:01
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class RoleAndMenuReq {
    @ApiModelProperty(value = "菜单code", dataType = "List", required = false)
    private List<String> menuCodeList;

    @ApiModelProperty(value = "菜单url", dataType = "Map", required = false)
    private Map<String, List<String>> menuUrl;

    @NotBlank(message = "角色id不能为空")
    @ApiModelProperty(value = "角色id", dataType = "String", required = true)
    private String roleId;
}
