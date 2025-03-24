package com.citc.nce.authcenter.csp.customerLoginRecord.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/10/25 10:29
 */
@Data
public class CspCustomerLoginRecordReq extends PageParam implements Serializable {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;

    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;

    @ApiModelProperty(value = "customerId", dataType = "String")
    private List<String> customerIdList;
}
