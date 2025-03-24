package com.citc.nce.authcenter.csp.customerLoginRecord.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class CspCustomerLoginRecordResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

}
