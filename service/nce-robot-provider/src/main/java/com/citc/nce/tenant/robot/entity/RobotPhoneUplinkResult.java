package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

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

    private String creatorOld;

    private String updaterOld;


}
