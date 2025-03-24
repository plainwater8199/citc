package com.citc.nce.auth.user.vo.req;

import lombok.Data;

import java.util.List;

@Data
public class GetUserInfoReq {
    private List<String> userIds;
}
