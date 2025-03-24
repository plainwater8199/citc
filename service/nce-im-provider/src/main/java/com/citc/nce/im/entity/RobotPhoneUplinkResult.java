package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("robot_phone_uplink_result")
public class RobotPhoneUplinkResult extends BaseDo<RobotPhoneUplinkResult> implements Serializable {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 消息id
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

    /**
     * 按钮的uuid
     */
    private String btnUuid;

    /**
     * 上行类型
     */
    private String actionType;

    /**
     * 节点id
     */
    private Long planDetailId;

    /**
     * 回执时间
     */
    private Date receiptTime;


}
