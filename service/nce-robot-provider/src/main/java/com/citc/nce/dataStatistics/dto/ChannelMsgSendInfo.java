package com.citc.nce.dataStatistics.dto;

import lombok.Data;

@Data
public class ChannelMsgSendInfo {

    private String sendTime;

    private String channelId;

    private String accountId;

    private Long sendSum;
}
