package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("robot_node_result")
public class RobotNodeResultDo extends BaseDo<RobotNodeResultDo> implements Serializable {

    /**
     * 盒子id
     */
    private Long planDetailId;

    /**
     * 消息id或taskId
     * ,supplier fontdo发送的时候这里首先是null,  后期根据网关回调的taskId ,找到对应的planDetailId, 然后再更新这个字段
     */
    private String messageId;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

}
