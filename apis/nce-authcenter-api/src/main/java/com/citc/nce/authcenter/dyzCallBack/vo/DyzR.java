package com.citc.nce.authcenter.dyzCallBack.vo;

import cn.hutool.core.map.MapUtil;
import lombok.Data;

import java.util.Map;

/**
 * bydud
 * 2024/1/16
 **/
@Data
public class DyzR<T> {
    private Integer code; // 错误码
    private String msg; // 信息
    private T data;


    public DyzR(){

    }
    public DyzR(T data) {
        this.data = data;
    }

    public static DyzR<Map<String, Integer>> pass() {
        DyzR<Map<String, Integer>> dyzR = new DyzR<>(MapUtil.builder("status", 0).build());
        dyzR.setCode(0);
        dyzR.setMsg("success");
        return dyzR;
    }

    public static DyzR<Map<String, Integer>> fail() {
        DyzR<Map<String, Integer>> dyzR = new DyzR<>(MapUtil.builder("status", 1).build());
        dyzR.setCode(0);
        dyzR.setMsg("fail");
        return dyzR;
    }
}