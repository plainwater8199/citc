package com.citc.nce.auth.mobile.resp;


import lombok.Data;

/**
 * 返回给移动的结果
 */
@Data
public class SyncReturn {

    //"00000"表示成功，其它值是失败
    private String resultCode;
    //"成功"
    private String resultDesc;
}
