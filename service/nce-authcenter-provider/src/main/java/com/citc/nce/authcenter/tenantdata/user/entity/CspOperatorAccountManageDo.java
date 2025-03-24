package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.account.entity
 * @Author: litao
 * @CreateTime: 2023-02-14  10:02
 
 * @Version: 1.0
 */
@Data
@TableName("csp_operator_account_manage")
public class CspOperatorAccountManageDo extends BaseDo<CspOperatorAccountManageDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private String userId;  // TODO 废弃

    private String operatorAccountId;

    private String cspId;

    @ApiModelProperty("账号")
    private String cspAccount;

    @ApiModelProperty("密码")
    private String cspPassword;

    @ApiModelProperty("csp编码")
    private String cspCode;

    @ApiModelProperty("公钥")
    private String publicKey;

    @ApiModelProperty("消息回调地址")
    private String msgCallbackUrl;

    @ApiModelProperty("媒体文件审核回调地址")
    private String mediaCallbackUrl;

    @ApiModelProperty("数据同步接口地址")
    private String dataSyncUrl;

    @ApiModelProperty("ip地址")
    private String ipAddress;

    @ApiModelProperty("代理商客户编码")
    private String agentCustomerNum;

    private String creatorOld;

    private String updaterOld;
}
