package com.citc.nce.auth.csp.readingLetter.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("csp_reading_letter_account")
public class CspReadingLetterAccountDo extends BaseDo<CspReadingLetterAccountDo> implements Serializable {
    private String accountId;

    private String cspId;

    private String accountName;

    private String customerId;

    private String appId;

    private String appKey;

    private String agentId;

    private String ecId;

    private Integer operator;

    private Integer status;

    private String customDomains;

    private int deleted;

    private String tariffBatch;
}
