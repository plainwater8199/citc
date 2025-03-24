package com.citc.nce.robot.vo.directcustomer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件名:Parameter
 * 创建者:zhujinyu
 * 创建时间:2024/2/28 11:23
 * 描述:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    //参数类型: STRING, LONG, DOUBLE，BOOLEAN，FLOAT和INTEGER;
    String type;

    String key;

    String value;
}
