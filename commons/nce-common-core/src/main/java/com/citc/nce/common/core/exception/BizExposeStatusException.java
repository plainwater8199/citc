package com.citc.nce.common.core.exception;

import lombok.Data;

/**
 * @author yy
 * @date 2024-05-15 09:24:41
 */
@Data
public class BizExposeStatusException extends RuntimeException{
    private Integer code;

    private String msg;
    public BizExposeStatusException(Integer code,String msg)
    {
        super(msg);
        this.code=code;
        this.msg=msg;
    }
}
