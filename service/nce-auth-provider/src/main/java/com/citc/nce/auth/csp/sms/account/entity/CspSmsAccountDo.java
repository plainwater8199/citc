package com.citc.nce.auth.csp.sms.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("csp_sms_account")
public class CspSmsAccountDo extends BaseDo<CspSmsAccountDo> implements Serializable {

    private String accountId;

    private String cspId;

    private String accountName;

    private String customerId;

    private Long residualCount;

    private String dictCode;

    private String dictValue;

    private String appId;

    private String appSecret;

    private Integer status;

    private int deleted;

    private String tariffBatch;
}
