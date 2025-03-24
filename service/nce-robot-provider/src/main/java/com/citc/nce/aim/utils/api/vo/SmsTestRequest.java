package com.citc.nce.aim.utils.api.vo;

import lombok.Data;

@Data
public class SmsTestRequest {
    private String calling;
    private String called;
}
