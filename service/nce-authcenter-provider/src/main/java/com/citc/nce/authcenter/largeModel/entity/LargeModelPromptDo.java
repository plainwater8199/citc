package com.citc.nce.authcenter.largeModel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("large_model_prompt")
public class LargeModelPromptDo extends BaseDo<LargeModelPromptDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "大模型id")
    private Long modelId;

    @ApiModelProperty(value = "节点类型（1-文本识别节点）")
    private Integer type;

    @ApiModelProperty(value = "prompt设定")
    private String promptSetting;

    @ApiModelProperty(value = "prompt规则")
    private String promptRule;

    @ApiModelProperty(value = "prompt示例")
    private String promptExample;

    @ApiModelProperty(value = "状态（0-禁用，1-启用）")
    private Integer status;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
