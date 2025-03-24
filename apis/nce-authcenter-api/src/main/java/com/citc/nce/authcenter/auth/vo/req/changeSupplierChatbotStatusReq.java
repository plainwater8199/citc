package com.citc.nce.authcenter.auth.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @Author: zhujy
 * @Date: 2024/3/17 14:34
 * @Version: 1.0
 * @Description: 更改chaotbot状态申请(上线,下线,注销)
 */
@Data
@Accessors(chain = true)
public class changeSupplierChatbotStatusReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * chatbot 的数据库主键
     */
    private String chatbotAccountId;

    /**
     * 更改的状态
     *     EDIT(1, "编辑信息"),
     *     COMPLETE_CONFIGURATION(2,"完成Chatbot配置"),
     *     UPDATE_CONFIGURATION(3,"修改Chatbot配置"),
     *     LOG_OFF(4,"注销Chatbot"),
     *     LOG_ON(5,"上线Chatbot"),
     *     OFFLINE(6,"下线Chatbot"),
     *     REJECT(7,"驳回Chatbot申请"),
     */
    private int newStatus;

    /**
     * 备注
     */
    private String remark;

}
