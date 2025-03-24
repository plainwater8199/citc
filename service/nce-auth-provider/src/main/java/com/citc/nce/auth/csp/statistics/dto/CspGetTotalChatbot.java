package com.citc.nce.auth.csp.statistics.dto;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspGetTotalChatbot {

    private Integer accountTypeCode;

    private Long num;


}
