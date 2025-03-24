package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("msg_id_mapping")
@EqualsAndHashCode(callSuper = true)
public class MsgIdMappingDo extends BaseDo<MsgIdMappingDo> implements Serializable {

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息id
     */
    private String customerId;
    /**
     * 消息id
     */
    private String platformMsgId;
}
