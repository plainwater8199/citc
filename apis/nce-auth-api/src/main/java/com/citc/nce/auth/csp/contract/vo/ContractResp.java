package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ContractResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("合同名称")
    private String contractName;

//    @ApiModelProperty("企业id")
//    private Long enterpriseId;

    @ApiModelProperty("客户Id")
    private String customerId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;
    @ApiModelProperty("企业账号名称")
    private String enterpriseAccountName;

    @ApiModelProperty("归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("服务代码")
    private String contractServiceCode;

    @ApiModelProperty("服务扩展码")
    private String contractServiceExtraCode;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("变更时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private Integer contractStatus;

    @ApiModelProperty("归属代理商id")
    private Long agentInfoId;

    private String failureReason;

    private Integer availableStatus;

    private Integer channel; // 通道
}
