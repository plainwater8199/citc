package com.citc.nce.authcenter.auth.vo.req;

import lombok.Data;

import java.util.List;

@Data
public class GetUsersInfoReq {
    private List<String> userIds;
}
