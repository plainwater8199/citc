package com.citc.nce.filecenter.vo;

import lombok.Data;

@Data
public class AccountDetails {
    private Long id;

    private String chatbotName;

    private String operator;

    private Integer fileCount;

    private Integer totalCount;
}
