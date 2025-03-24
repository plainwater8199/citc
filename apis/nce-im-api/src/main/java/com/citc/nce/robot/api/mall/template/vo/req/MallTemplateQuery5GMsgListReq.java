package com.citc.nce.robot.api.mall.template.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateQuery5GMsgListReq extends MallTemplateBaseQueryReq {

    @ApiModelProperty(value = "消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置", dataType = "Integer")
    private Integer messageType;
}
