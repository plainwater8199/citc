package com.citc.nce.im.robot.node;


import com.citc.nce.im.robot.enums.RouteConditionStrategy;
import lombok.Data;

import java.util.List;

/**
 * 路由信息
 */
@Data
public class RouterInfo {
    private String lineId;

    //当前节点ID
    private String headNodeId;

    //下个节点ID
    private String tailNodeId;

    // 关系Id (提问节点使用， 命中验证或兜底以后，识别自己对应的 路由
    private String relationId;

    //执行条件类型，0 满足任意一个即可，1 全部满足
    private RouteConditionStrategy conditionStrategy;

    //执行条件
    private List<ConditionExpression> execConditions;
}
