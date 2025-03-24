package com.citc.nce.robot.req;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class ShortMsgResultArray {
    private String code;
    private JSONArray data;
}
