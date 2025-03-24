package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IdReq
 */
@Data
public class MsgWithdrawIdReq {

    @ApiModelProperty(value = "查询或删除id",example = "1")
    private Long id;

    @ApiModelProperty(value = "开始时间",example = "2022-10-14")
    private String startTime;

    @ApiModelProperty(value = "结束时间",example = "2022-10-14")
    private String endTime;
}
