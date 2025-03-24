package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("csp_sms_account_signature")
public class CspSmsAccountSignatureDo extends BaseDo<CspSmsAccountSignatureDo> implements Serializable {

    private String accountId;

    private String signature;

    private Integer type;

    private int deleted;

    private String creatorOld;

    private String updaterOld;
}
