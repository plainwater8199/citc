package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserCertificationOptionListDBVO  {


    @ApiModelProperty(value = "账户资质")
    private List<Long> userCertificate;

    private String userId;

}
