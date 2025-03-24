package com.citc.nce.auth.csp.videoSms.signature.entity;

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
@TableName("csp_account_signature")
public class CspVideoSmsSignatureDo extends BaseDo<CspVideoSmsSignatureDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accountId;

    private String signature;

    private Integer type;

    private int deleted;
}
