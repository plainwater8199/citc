package com.citc.nce.authcenter.directCustomer.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:24
 * @Version: 1.0
 * @Description:
 */

/**
 * @author dylicr
 * @description 供应商聊天机器人信息操作记录
 * @date 2022-07-10
 */
@Data
@Accessors(chain = true)
@TableName("supplier_chatbot_status_operation_log")
public class SupplierChatbotStatusOperationLogDo extends BaseDo<UserDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userName;

    /**
     * 内部所用机器人id,是UUID, 非运营商/供应商提供的chatbotAccount
     */
    @TableField("chatbot_account_id")
    private String chatbotAccountId;

    /**
     * 处理内容  @See com.citc.nce.authcenter.directCustomer.enums.OperationTypeEnum
     */
    private int operationTypeCode;
    private String operationType;
    /**
     * 备注
     */
    private String remark;

    /**
     * 是否删除 默认0 未删除 1 删除
     */
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deletedTime;

}