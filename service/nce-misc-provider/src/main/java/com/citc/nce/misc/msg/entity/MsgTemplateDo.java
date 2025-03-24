package com.citc.nce.misc.msg.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/13 15:24
 * @Version: 1.0
 * @Description:
 */

@Data
@TableName("msg_template")
public class MsgTemplateDo extends BaseDo<MsgTemplateDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板code
     */
    private String templdateCode;

    /**
     * 邮件主题
     */
    private String templdateSubject;

    /**
     * 模板内容
     */
    private String templdateContent;

    /**
     * 消息过期时间
     */
    private Integer expireTime;

    /**
     * 是否删除（1为已删除，0为未删除）
     */
    @TableLogic
    private int isDelete;

    /**
     * 未删除默认为0，删除为时间戳
     */
    private Long deletedTime;
}