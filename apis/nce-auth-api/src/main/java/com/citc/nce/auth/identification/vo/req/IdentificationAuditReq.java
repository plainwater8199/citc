package com.citc.nce.auth.identification.vo.req;

import lombok.Data;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/15 9:48
 * @Version: 1.0
 * @Description:
 */
@Data
public class IdentificationAuditReq {

    private String userId;

    private Integer status;

    private String remark;
}
