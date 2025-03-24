package com.citc.nce.aim.vo;

import lombok.Data;

/**
 * <p>回调参数</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/14 15:10
 */
@Data
public class AimCallBackResp<T> {

    /**
     * 成功或者失败的code码
     * 默认 : 200
     **/
    private int code;

    /**
     * 成功时返回的数据，失败时返回具体的异常信息
     **/
    private T data;

    /**
     * 返回消息
     * 默认 : success
     **/
    private String msg;
}
