package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserCertificationOptionListDBVO {


    @ApiModelProperty(value = "账户资质")
    private List<Long> userCertificate;

    private String userId;

}
