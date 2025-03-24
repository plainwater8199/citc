package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:32
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AdminUserSumResp {
    @ApiModelProperty(value = "待审核申请", dataType = "Long")
    private Long userSum;

}
