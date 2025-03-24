package com.citc.nce.auth.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/15 9:48
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ViewRemarkReq {

    @ApiModelProperty(value = "客户端用户user_id", dataType = "String")
    private String clientUserId;
    @ApiModelProperty(value = "资质id", dataType = "Integer")
    private Integer identificationId;
}
