package com.citc.nce.misc.shortUrl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("short_url")
public class ShortUrlDo extends BaseDo<ShortUrlDo> implements Serializable {

    @ApiModelProperty("短链url")
    private String shortUrl;

    @ApiModelProperty("原始url")
    private String originUrl;

    @ApiModelProperty("hash")
    private long hash;

    @ApiModelProperty("用于生成短链的id")
    private Long originId;

    @ApiModelProperty("功能类型 01:表单")
    private String type;

    @ApiModelProperty("是否删除 0:未删除 1:已删除")
    private Long deleted;
}
