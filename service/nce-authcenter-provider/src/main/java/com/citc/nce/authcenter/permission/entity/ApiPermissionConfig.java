package com.citc.nce.authcenter.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.authcenter.permission.enums.Permission;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
//@TableName("api_permission_config")
public class ApiPermissionConfig {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String url;

    private Permission permission;
}
