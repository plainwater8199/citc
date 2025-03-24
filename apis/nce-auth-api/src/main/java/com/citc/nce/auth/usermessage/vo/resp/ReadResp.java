package com.citc.nce.auth.usermessage.vo.resp;

import io.swagger.annotations.ApiModelProperty;

public class ReadResp {

    @ApiModelProperty(value = "读", dataType = "String")
    private String read;

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
