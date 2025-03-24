package com.citc.nce.keywordsreply.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/29 15:23
 */
@TableName("keywords_reply")
@Data
@Accessors(chain = true)
public class KeywordsReply {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id ;

    @ApiModelProperty("关键词")
    private String keywords ;
    @ApiModelProperty("回复模板id")
    private Long replyTemplateId;
    @ApiModelProperty("关联账号")
    private String applyAccounts;



    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;
}
