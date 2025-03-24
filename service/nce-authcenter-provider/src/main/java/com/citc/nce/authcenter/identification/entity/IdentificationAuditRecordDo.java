package com.citc.nce.authcenter.identification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/14 18:49
 * @Version: 1.0
 * @Description: 认证审核记录表
 */
@Data
@TableName("identification_audit_record")
@Accessors(chain = true)
public class IdentificationAuditRecordDo extends BaseDo<IdentificationAuditRecordDo> {

    private static final long serialVersionUID = 1L;

    /**
     * 资质id
     */
    private Integer identificationId;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 审核结果：0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deletedTime;

    /**
     * 审核人
     */
    private String reviewer;
}
