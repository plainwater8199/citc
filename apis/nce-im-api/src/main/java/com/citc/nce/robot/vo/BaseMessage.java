package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class BaseMessage {
    //用户号码列表
    private List<String> destinationAddress;

    private String messageId;

}
