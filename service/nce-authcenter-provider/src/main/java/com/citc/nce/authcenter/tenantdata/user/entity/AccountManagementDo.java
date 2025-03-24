package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:55
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("account_management")
public class AccountManagementDo extends BaseDo<AccountManagementDo> {

    /**
     * 账户类型，1联通2硬核桃
     * 2023/2/20 注意：数据库存的名字，不是数字类型
     */
    @TableField(value = "account_type")
    private String accountType;

    /**
     * 机器人创建者(csp)
     * 2023/3/8
     */
    @TableField(value = "csp_user_id")
    private String cspUserId;

    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    @TableField(value = "account_type_code")
    private int accountTypeCode;

    /**
     * 机器人状态(30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试)
     */
    @TableField(value = "chatbot_status")
    private int chatbotStatus;

    @TableField(value = "enterprise_id")
    private Long enterpriseId;

    /**
     * 账号名称
     */
    @TableField(value = "account_name")
    private String accountName;

    /**
     * 账号id
     */
    @TableField(value = "account_id")
    private String accountId;

    /**
     * appid
     */
    @TableField(value = "app_id")
    private String appId;

    /**
     * appkey
     */
    @TableField(value = "app_key")
    private String appKey;

    /**
     * token
     */
    @TableField(value = "token")
    private String token;

    /**
     * 消息地址
     */
    @TableField(value = "message_address")
    private String messageAddress;

    /**
     * 文件地址
     */
    @TableField(value = "file_address")
    private String fileAddress;

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

    private Integer isAddOther;
}
