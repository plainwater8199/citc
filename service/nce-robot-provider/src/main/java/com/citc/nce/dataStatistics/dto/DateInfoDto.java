package com.citc.nce.dataStatistics.dto;

import lombok.Data;

import java.util.Date;

@Data
public class DateInfoDto {
    private Date startDate;
    private Date endDate;
    private Integer type;
}
