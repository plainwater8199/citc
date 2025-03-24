package com.citc.nce.im.session.processor.ncenode.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/6 15:12
 * @Version: 1.0
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Line {
    private String lineName;
    private String lineId;
    private String fromId;
    /**
     * 起始节点名称
     */
    private String fromName;
    /**
     * 下一个节点名称
     */
    private String toId;
    private String toName;
    /**
     * 锚点索引，用于提问节点
     */
    private int index = 1;
}
