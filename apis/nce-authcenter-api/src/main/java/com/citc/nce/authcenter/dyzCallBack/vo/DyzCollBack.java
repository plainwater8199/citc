package com.citc.nce.authcenter.dyzCallBack.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class DyzCollBack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资产标识
     */
    @NotEmpty(message = "appCode不能为空")
    private String appCode;

    /**
     * 私钥加密的签名
     */
    @NotEmpty(message = "sign不能为空")
    private String sign;

    /**
     * 业务流水号 由调用方生成，保持唯一
     *
     */
    @NotEmpty(message = "bizSn不能为空")
    private String bizSn;

    /**
     * 手机号
     */
    @NotEmpty(message = "mobile不能为空")
    private String mobile;

    /**
     * 用户账号（不传递）
     */
    private String userAccount;
}
