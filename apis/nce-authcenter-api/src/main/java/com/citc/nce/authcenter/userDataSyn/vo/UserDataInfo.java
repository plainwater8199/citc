package com.citc.nce.authcenter.userDataSyn.vo;

import lombok.Data;

@Data
public class UserDataInfo {
    private CertificationInfo certification;
    private Boolean enable;
    private String idType;
    private String comments;
    private String introduction;
    private Integer reportCount;
    private String logo;
    private String group;
    private String birth;
    private String gender;
    private String state;
    private Integer breakRuleTimes;
    private Boolean loginActive;
    private String isSettled;
    private String isGsma;
    private String is5G;
    private Integer checkedLegalDocVersion;
    private String userCode;
    private String userCenterId;
    private String date;
    private String userName;
    private String phoneNum;
    private String email;
    private String cardNo;
    private String userType;
    private String userTag;
    private String settledStatus;
    private String id;

}
