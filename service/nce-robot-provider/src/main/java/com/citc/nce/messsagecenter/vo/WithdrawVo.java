package com.citc.nce.messsagecenter.vo;

import lombok.Data;

import java.util.List;

@Data
public class WithdrawVo {
    //messageId
    private String messageId;
    //电话列表
    private List<String> destinationAddress;
}
