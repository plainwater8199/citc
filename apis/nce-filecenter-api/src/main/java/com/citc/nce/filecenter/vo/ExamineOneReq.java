package com.citc.nce.filecenter.vo;

import lombok.Data;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月27日17:23:27
 * @Version: 1.0
 * @Description: ExamineOneReq
 */
@Data
public class ExamineOneReq {
    private String operator;

    private String fileUUID;

    private String creator;

    private String chatbotId;
}
