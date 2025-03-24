package com.citc.nce.authcenter.captcha.vo.resp;

import lombok.Data;
/**
 * @author luohaihang
 * @version 1.0
 * @description：
 * @date 2023/3/1 14:30
 */
@Data
public class DyzResultResp<T>{
    /**
     * 错误码
     */
    private Integer code;
    /**
     * 信息
      */
    private String msg;
    /**
     * Data
     */
    private T data;
}
