package com.citc.nce.robot.vo;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * 流程发布结果
 * @author yy
 * @date 2024-03-29 20:29:01
 */
@Data
public class RobotProcessTemplateAuditResp {
    public int totalCount;
    int successCount;
    int failCount;
    List<JSONObject> failList;
    DateTime currentTime;
}
