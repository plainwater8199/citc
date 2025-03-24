package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RcsContractInformation {

 private String contractNo;
 private String name;
 private String effectiveDate;
 private String expiryDate;
 private Integer status;
 private String renewalDate;
 private String accessory;

}
