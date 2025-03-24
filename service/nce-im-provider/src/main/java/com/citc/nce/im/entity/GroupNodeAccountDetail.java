package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 群发计划节点账号发送明细
 *
 * @author jcrenc
 * @since 2024/5/10 11:47
 */
@Data
@Accessors(chain = true)
@TableName("robot_group_node_account_detail")
public class GroupNodeAccountDetail {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long nodeId;
    private String accountId;
    private Integer maxSend;
    private Integer preemptedNumber;
    private Integer actualSendNumber;
    private Integer returnNumber;
    //预扣除金额
    private Long preempted_count;
    private String messageId;
    //资费批次号
    private Long tariff_id;
    //资费单价
    private int price;
    //应发送的手机号集合快照, 英文逗号分隔
    private String selectPhoneNumbers;
    private Integer chargeNum;
}
