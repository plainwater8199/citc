package com.citc.nce.auth.helpcenter.vo;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 * @date 2024-05-06 09:57:33
 * @description 文档
 */
@Data
public class ArticleVo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "文档目录id")
    @NotNull(message = "文档目录id不能为空")
    String docId;
    @ApiModelProperty(value = "文档id")
    Integer id;
    @ApiModelProperty(value = "文档内容")
    @NotNull(message = "文档内容不能为空")
    String content;
    @ApiModelProperty(value = "文档标题")
    @NotNull(message = "文档标题不能为空")
    String title;
    @ApiModelProperty(value = "文档更新时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;
}
