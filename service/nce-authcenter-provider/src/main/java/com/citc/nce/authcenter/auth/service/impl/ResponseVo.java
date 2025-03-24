package com.citc.nce.authcenter.auth.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 全局响应类
 *
 * @param <T>
 * @author
 */
@Data
public class ResponseVo<T> {
    public ResponseVo(){
        this.code="200";
        this.message ="操作成功!";
        this.time=new Date();
    }
    private String code;

    private String message;

    private Date time;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

}
