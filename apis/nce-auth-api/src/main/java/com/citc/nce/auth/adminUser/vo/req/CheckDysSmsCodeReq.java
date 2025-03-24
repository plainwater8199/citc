package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 检查来短信代码要求
 *
 * @author ylzouf
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/11 11:27
 * @Version 1.0
 * @Description:
 * @date 2022/11/11
 */
@Data
@Accessors(chain = true)
public class CheckDysSmsCodeReq extends DyzOnlyIdentificationReq {
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    String phone;
    @ApiModelProperty(value = "验证码", dataType = "String", required = true)
    String code;
    @ApiModelProperty(value = "多因子系统流水号(这里是拿验证码的key作为唯一性)", dataType = "String", required = true)
    String bizSn;
}
