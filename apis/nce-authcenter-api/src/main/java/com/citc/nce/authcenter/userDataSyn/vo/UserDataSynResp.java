package com.citc.nce.authcenter.userDataSyn.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserDataSynResp {
    private List<String> saveDBUsers;
    private List<String> unSaveDBUsers;
}
