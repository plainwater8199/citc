package com.citc.nce.robot.bean;

import com.citc.nce.common.core.pojo.PageParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.bean
 * @Author: weilanglang
 * @CreateTime: 2022-07-20  11:45
 
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPageParam extends PageParam {
    private Long id;//指令id
    private String userId;
}
