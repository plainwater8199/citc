package com.citc.nce.auth.csp.home.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class HomeTotalOverviewResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活跃客户", dataType = "BigDecimal")
    private Integer activeUserCount;

    @ApiModelProperty(value = "活跃chatbot", dataType = "BigDecimal")
    private Integer activeChatbotCount;

    @ApiModelProperty("视频短信账号数量")
    private Integer mediasSmsAccountCount;
}
