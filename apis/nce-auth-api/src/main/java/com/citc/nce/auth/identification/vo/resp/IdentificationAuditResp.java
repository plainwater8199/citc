package com.citc.nce.auth.identification.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/15 9:31
 * @Version: 1.0
 * @Description:
 */
@Data
@ApiModel(description = "Admin端 查看审核client 审核备注list resp")
@Accessors(chain = true)
public class IdentificationAuditResp {
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    @ApiModelProperty(value = "admin 端审核账号")
    private String auditAccount;
    @ApiModelProperty(value = "备注")
    private String auditRemark;
}
