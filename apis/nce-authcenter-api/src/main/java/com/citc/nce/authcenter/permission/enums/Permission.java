package com.citc.nce.authcenter.permission.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jiancheng
 */
@RequiredArgsConstructor
@Getter
public enum Permission {

    MASS(1, "群发"),
    ROBOT(2, "机器人"),
    MEDIA_SMS(3, "视频短信"),
    SMS(4, "短信"),
    _5G_MESSAGE(5, "5G消息");

    @JsonValue
    @EnumValue
    private final int code;

    private final String desc;

    /**
     * 将用户权限字符串转换为枚举类型权限集合
     *
     * @param permissions 权限字符串
     * @return 权限集合
     */
    public static List<Permission> convert(String permissions) {
        Objects.requireNonNull(permissions);
        ArrayList<Permission> permissionList = new ArrayList<>();
        for (String perm : permissions.split(",")) {
            for (Permission permission : values()) {
                if (Objects.equals(Integer.valueOf(perm), permission.getCode()))
                    permissionList.add(permission);
            }
        }
        return permissionList;
    }
}
