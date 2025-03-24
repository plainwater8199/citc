package com.citc.nce.im.robot.enums;

/**
 * 路由条件策略，决定了节点如何使用通过条件路由到下一个节点
 *
 * @author jcrenc
 * @since 2023/7/14 14:22
 */
public enum RouteConditionStrategy {
    //普通节点（非提问节点和指令节点的路由，可以直接路由成功）
    NONE/*不需要任何条件，直接路由到目标节点*/,

    //提问节点用
    ANY_MATCH/*任意匹配一个条件即可路由到目标节点*/,
    ALL_MATCH/*需要全部条件都匹配才能路由到目标节点*/,

    //指令节点用
    EXECUTE_SUCCESS/*当前节点执行成功*/,
    EXECUTE_FAIL/*当前节点执行失败*/;
}
