package com.citc.nce.auth.sharelink.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:00
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("share_link")
public class ShareLinkDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 账户id
     */
    @TableField(value ="chatbot_account_id")
    private String chatbotAccountId;

    /**
     * 链接名称
     */
    @TableField(value ="link_name")
    private String linkName;

    /**
     * 链接信息
     */
    @TableField(value ="link_info")
    private String linkInfo;

    /**
     * 0未删除  1已删除
     */
    @TableField(value = "deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

}
