package com.citc.nce.authcenter.tenantdata.user.entity;

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

    //private Long accountManagementId;//TODO fq ALTER TABLE `chatbot_manage_shelves` ADD COLUMN `account_management_id` BIGINT(20) NULL DEFAULT NULL COMMENT '历史数据' AFTER `chatbot_id`;

    private String chatbotAccountId;

    private String chatbotId;

    /**
     * 审核状态 0：待审核，1：审核通过，2：审核不通过
     */
    private Integer status;

    private String shelvesFileUrl;

    private String shelvesDesc;

    private int deleted;

    private String creatorOld;

    private String updaterOld;
}
