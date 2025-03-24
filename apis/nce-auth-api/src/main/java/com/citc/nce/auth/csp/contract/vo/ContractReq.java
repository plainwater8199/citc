package com.citc.nce.auth.csp.contract.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43ce
 */
@Data
public class ContractReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("合同名称")
    private String contractName;

    @ApiModelProperty("状态 11:新增审核不通过， 12：变更审核不通过，20：新增审核中，22：待管理平台新增审核，23：待管理平台变更审核，30：正常，40：暂停")
    private Integer contractStatus;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户Id")
    private String cspId;

    @ApiModelProperty("归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "页数", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "每页条数", dataType = "Integer", required = true)
    private Integer pageSize;

    private String userId;
}
