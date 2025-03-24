package com.citc.nce.aim.privatenumber.vo;

import lombok.Data;

@Data
public class PrivateNumberCallBackResp {

    /**
     * 成功或者失败的code码
     * 默认 : 200
     **/
    private int code;

    /**
     * 成功时返回的数据，失败时返回具体的异常信息
     **/
    private PrivateNumberProjectInfo data;

    /**
     * 返回消息
     * 默认 : success
     **/
    private String msg;
}
