package com.citc.nce.keywordsreply.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@TableName("keywords_reply_statistics")
@Data
@Accessors(chain = true)
public class KeyWordsReplyStatistics {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id ;

    @ApiModelProperty("关键词")
    private String keywords ;


    @ApiModelProperty("消息ID")
    private String messageId;

    @ApiModelProperty("账户ID")
    private String customerId;


    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
