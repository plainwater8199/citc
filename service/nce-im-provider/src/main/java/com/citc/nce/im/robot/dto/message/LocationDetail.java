package com.citc.nce.im.robot.dto.message;

import lombok.Data;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/13 9:43
 */
@Data
public class LocationDetail {

    private String name;
    private String value;
    private List<?> names;
}
