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
@TableName("large_model")
public class LargeModelDo extends BaseDo<LargeModelDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型产品")
    private Integer modelProduct;

    @ApiModelProperty(value = "模型")
    private Integer modelCode;

    @ApiModelProperty(value = "APIKey")
    private String apiKey;

    @ApiModelProperty(value = "SECRETKey")
    private String secretKey;

    @ApiModelProperty(value = "API地址")
    private String apiUrl;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
