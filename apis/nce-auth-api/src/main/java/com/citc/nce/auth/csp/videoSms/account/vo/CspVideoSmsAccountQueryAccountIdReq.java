package com.citc.nce.auth.csp.videoSms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsAccountQueryAccountIdReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("userIds")
    private List<String> userIds;
}
