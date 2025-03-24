package com.citc.nce.robot.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/6 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeActResult {
    /**
     * 是否能去子节点
     */
    private boolean next;

    /**
     * 给输入的相应解雇，会输出到客户端
     */
    private Object result;

    /**
     * 用于提问节点返回子节点的索引
     */
    private String childIdx;
}
