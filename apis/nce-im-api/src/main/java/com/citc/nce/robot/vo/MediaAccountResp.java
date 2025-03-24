package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class MediaAccountResp {

    private List<String> accounts;

    private List<String> mediaAccounts;

    private List<String> shortMsgAccounts;

}
