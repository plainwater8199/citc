package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

/**
 * 撤回相网关请求
 */
@Data
public class WithdrawReq {

    //messageId
    private String messageId;
    //电话列表
    private List<String> destinationAddress;
}
