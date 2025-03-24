package com.citc.nce.im.likestatistic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/31 10:30
 */
//@TableName("like_statistic")
@Data
@Accessors(chain = true)
public class LikeStatistic {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("被赞主体id")
    private Long likedId;

    @ApiModelProperty("被赞数量")
    private Long likedNumber;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
