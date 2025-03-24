package com.citc.nce.robot.vo.ButtonDetail;

import lombok.Data;

import java.util.List;

@Data
public class InputInfo {
    private String name;

    private String value;

    private List<String> names;

    private Integer length;
}
