package com.citc.nce.vo;

import lombok.Data;

import java.util.List;

/**
 * 素材统计数量返回类
 */
@Data
public class StatisticsResp {

    private Integer type;

    private Long total = 0L;

    private List<StatusTotal> statusTotals;
}
