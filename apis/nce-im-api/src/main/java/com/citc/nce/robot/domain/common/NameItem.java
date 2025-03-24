package com.citc.nce.robot.domain.common;

import lombok.Data;

/**
 * 变量names单个元素实体定义
 *
 * @author jcrenc
 * @since 2024/3/5 10:02
 */
@Data
public class NameItem {
    public static String PREFIX = "变量";
    private String id;
    private String type;
    private String name;
    @Data
    public static class Test{}
}
