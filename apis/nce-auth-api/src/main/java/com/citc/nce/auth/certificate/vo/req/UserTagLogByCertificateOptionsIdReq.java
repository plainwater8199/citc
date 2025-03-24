package com.citc.nce.auth.certificate.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/15
 * @Version 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserTagLog对象", description = "用户标签处理日志表")
public class UserTagLogByCertificateOptionsIdReq {

    @ApiModelProperty(value = "用户账号资质信息表id")
    private Long certificateOptionsId;

}
