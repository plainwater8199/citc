package com.citc.nce.im.shortMsg;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.List;

@Data
public class ShortMsgRequest {

    private List<ShortMsgMobile> smses;

    private String templateId;

    private Long requestTime;

    private Integer requestValidPeriod;




}
