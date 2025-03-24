package com.citc.nce.auth.certificate.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

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
public class UserTagLogResp {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户账号资质信息表id")
    private Long certificateOptionsId;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "管理员账号id")
    private String adminUserId;

    @ApiModelProperty(value = "管理员账号名称")
    private String adminUserName;

    @ApiModelProperty(value = "备注")
    private String remark;
}
