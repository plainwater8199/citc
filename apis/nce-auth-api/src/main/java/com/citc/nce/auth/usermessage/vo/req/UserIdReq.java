package com.citc.nce.auth.usermessage.vo.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class UserIdReq {

    @NotNull
    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
