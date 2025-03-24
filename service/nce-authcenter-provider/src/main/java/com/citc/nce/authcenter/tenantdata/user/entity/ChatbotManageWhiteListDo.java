package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("chatbot_manage_white_list")
public class ChatbotManageWhiteListDo extends BaseDo<ChatbotManageWhiteListDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String chatbotAccountId;

    private String chatbotId;

    /**
     * 审核状态 0：待审核，26：审核通过，27：审核不通过
     */
    private Integer status;

    private String whiteList;

    private Integer deleted;

    /**
     * 新增和审核
     * 0：默认 1:移动新增审核通过
     */
    private Integer createAndAudit;
}
