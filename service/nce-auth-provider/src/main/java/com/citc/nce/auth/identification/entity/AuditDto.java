package com.citc.nce.auth.identification.entity;

import lombok.Data;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/15 10:35
 * @Version: 1.0
 * @Description:
 */
@Data
public class AuditDto {
    private String remark;
    private Integer status;
    private String userId;
}
