package com.citc.nce.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ExamineResultVo {

    // 1待审核  2审核成功  3失败  4无状态 5审核中
    private Integer fileStatus;

    private Date validity;

    private String operator;

    private String chatbotAccount;
}
