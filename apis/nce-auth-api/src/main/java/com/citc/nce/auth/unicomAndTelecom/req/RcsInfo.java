package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class RcsInfo {
    private String introduce;
    private String serviceIcon;
    private String workPhone;
    private String businessLicense;
    private String businessAddress;
    private String province;
    private String city;
    private String area;
    private String operatorName;
    private String operatorCard;
    private String operatorPhone;
    private String emailAddress;
    private List<String> operatorIdentityPic;

}
