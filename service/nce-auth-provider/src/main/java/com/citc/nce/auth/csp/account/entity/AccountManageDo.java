package com.citc.nce.auth.csp.account.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * csp运营商账户管理表
 * @BelongsPackage: com.citc.nce.auth.csp.account.entity
 * @Author: litao
 * @CreateTime: 2023-02-14  10:02

 * @Version: 1.0
 */
@Data
@TableName("csp_operator_account_manage")
public class AccountManageDo extends BaseDo<AccountManageDo> implements Serializable {
    private static final long serialVersionUID = 1L;
    //CCA 后台自建
    @ApiModelProperty("csp_account_id")
    private String chatbotCspAccountId;

//    @ApiModelProperty("用户id")
//    private String userId;
    //移动的appId=自己上传的CspAccount
    @ApiModelProperty("appId")
    private String appId;
    //移动,自传,账号   电联,自传,csp客户编码
    @ApiModelProperty("账号")
    private String cspAccount;
    //移动自传,密码  电联,自传,csp密钥
    @ApiModelProperty("密码")
    private String cspPassword;
    //移动:CSP自传
    @ApiModelProperty("csp编码")
    private String cspCode;

    @ApiModelProperty("公钥")
    @TableField(fill = FieldFill.INSERT)
    private String publicKey;

    @ApiModelProperty("私钥")
    @TableField(fill = FieldFill.INSERT)
    private String privateKey;

    @ApiModelProperty("消息回调地址")
    private String msgCallbackUrl;

    @ApiModelProperty("媒体文件审核回调地址")
    private String mediaCallbackUrl;

    @ApiModelProperty("数据同步接口地址")
    private String dataSyncUrl;

    @ApiModelProperty("ip地址")
    private String ipAddress;
    //移动:自传,代理商客户编码   电联: 自传,csp服务代码
    @ApiModelProperty("代理商客户编码")
    private String agentCustomerNum;

    @ApiModelProperty("运营商")
    //operator_code 123
    private Integer operatorCode;
    //移动:后台自动计算     电联: CSP输入
    private String token;
    //OPA  后台自建
    @ApiModelProperty("运营商账号ID")
    private String operatorAccountId;

    @ApiModelProperty("cspId")
    private String cspId;

    private String creatorOld;

    private String updaterOld;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;

    private Boolean deleted;

}
