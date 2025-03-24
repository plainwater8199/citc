package com.citc.nce.robot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
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
public class RobotGroupSendPlansDetailPageParam extends PageParam {

    @ApiModelProperty(value = "分页发送计划明细列表请求参数")
    private RobotGroupSendPlansDetail robotGroupSendPlansDetail;
}
