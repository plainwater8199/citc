package com.citc.nce.auth.csp.videoSms.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("csp_video_sms_account")
public class CspVideoSmsAccountDo extends BaseDo<CspVideoSmsAccountDo> implements Serializable {
    private static final long serialVersionUID = 1L;

//    private String userId;

    private String accountId;

    private String accountName;

    private String customerId;

    private String cspId;

    private Long residualCount;

    private String dictCode;

    private String dictValue;

    private String appId;

    private String appSecret;

    private Integer status;

    private int deleted;

    private String tariffBatch;
}
