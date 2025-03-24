package com.citc.nce.auth.csp.chatbot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("chatbot_manage_shelves")
public class ChatbotManageShelvesDo extends BaseDo<ChatbotManageShelvesDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatbotAccountId;

    private String chatbotId;

    /**
     * 审核状态 0：待审核，1：审核通过，2：审核不通过
     */
    private Integer status;

    private String shelvesFileUrl;

    private String shelvesDesc;

    private int deleted;
}
