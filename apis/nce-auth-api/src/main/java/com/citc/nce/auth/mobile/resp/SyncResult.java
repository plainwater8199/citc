package com.citc.nce.auth.mobile.resp;


import lombok.Data;

/**
 * 同步移动平台返回结果接收实体类
 */
@Data
public class SyncResult {

    //00000表示成功，其它值是失败
    private String resultCode;

    private String resultDesc;

    private CustomerNum data;

    private String timestamp;

    private String status;

    private String error;

    private String message;

    private String path;
}
