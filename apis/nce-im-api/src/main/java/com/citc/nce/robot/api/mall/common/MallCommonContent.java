package com.citc.nce.robot.api.mall.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;


/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:24
 */
@Data
public class MallCommonContent {

    @ApiModelProperty(value = "机器人list")
    private List<Robot> robot;

    @ApiModelProperty(value = "5G消息模板快照内容")
    private FiveG fiveG;

    private String snapshotUuid;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date updateTime;
}
