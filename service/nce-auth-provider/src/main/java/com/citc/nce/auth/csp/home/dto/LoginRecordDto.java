package com.citc.nce.auth.csp.home.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/25 14:37
 */
@Data
public class LoginRecordDto {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    private Date dailyFirstLoginTime;
}
