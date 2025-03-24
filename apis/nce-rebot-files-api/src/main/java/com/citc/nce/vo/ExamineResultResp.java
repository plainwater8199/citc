package com.citc.nce.vo;

import lombok.Data;

@Data
public class ExamineResultResp {
    private String chatbotId;
    private String fileUuid;

    public ExamineResultResp() {
    }

    public ExamineResultResp(String chatbotId, String fileUuid) {
        this.chatbotId = chatbotId;
        this.fileUuid = fileUuid;
    }
}
