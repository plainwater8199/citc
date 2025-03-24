package com.citc.nce.vo;

import lombok.Data;

/**
 * 审核状态和总数
 */
@Data
public class StatusTotal {

    private Integer status;

    private Long total = 0L;
}
