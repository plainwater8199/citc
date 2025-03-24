package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("csp_sms_account")
public class CspSmsAccountDo extends BaseDo<CspSmsAccountDo> implements Serializable {

    private String accountId;

    private String accountName;

    private String userId; // TODO fq

    private String customerId;

    private String cspId;

    private Long enterpriseId; // TODO fq

    private Long residualCount;

    private String dictCode;

    private String dictValue;

    private String appId;

    private String appSecret;

    private Integer status;

    private int deleted;

    private String creatorOld;

    private String updaterOld;


}
