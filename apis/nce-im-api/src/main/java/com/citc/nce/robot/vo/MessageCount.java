package com.citc.nce.robot.vo;

import lombok.Data;

@Data
public class MessageCount {

    //返回的消息状态
    private String resultType;

    //每个消息状态对应的数量
    private Integer num;

}
