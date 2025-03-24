package com.citc.nce.im.likestatistic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/31 11:28
 */
@TableName
@Data
@Accessors(chain = true)
public class LikeRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("被赞主体id")
    private Long likedId;

    private String customerId;

    private LocalDateTime createTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;
}
