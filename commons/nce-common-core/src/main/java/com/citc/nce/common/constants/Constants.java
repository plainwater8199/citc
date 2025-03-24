package com.citc.nce.common.constants;

/**
 * 一些公共的常量
 *
 * @author yy
 * @date 2024-03-13 10:45:23
 */
public interface Constants {
    /**
     * 服务供应商为蜂动
     */
    String SUPPLIER_TAG_FONTDO = "fontdo";
    /**
     * 服务提供商为csp自己
     */
    String SUPPLIER_TAG_OWNER = "owner";

    /**
     * 模板审核状态（待审核）
     */
    int TEMPLATE_STATUS_WAITING = -1;
    /**
     * 模板审核状态（审核中）
     */
    int TEMPLATE_STATUS_PENDING = 1;
    /**
     * 模板审核状态（审核通过）
     */
    int TEMPLATE_STATUS_SUCCESS = 0;
    /**
     * 模板审核状态（审核不通过）
     */
    int TEMPLATE_STATUS_FAILED = 2;
    /**
     * 传到供应商的模板中button包含的流程节点信息的占位符
     */
    String TEMPLATE_BUTTON_DETAILID_PLACEHOLDER = "detailId";
    /**
     * 模板来源，在哪里制作的模板(1模板管理模块)
     */
    int TEMPLATE_SOURCE_TEMPLATEMANAGE = 1;
    /**
     * 模板来源，在哪里制作的模板(2 机器人流程)
     */
    int TEMPLATE_SOURCE_ROBOT = 2;

    /**
     * 模板来源，在哪里制作的模板(3 兜底回复)
     */
    int TEMPLATE_SOURCE_LASTREPLY = 3;
    /**
     * 普通模板不包含变量
     */
    int TEMPLATE_TYPE_GENARAL = 1;
    /**
     * 个性模板包含变量
     */
    int TEMPLATE_TYPE_VARIABLE = 2;

    /**
     * 模板需要送审
     */
    int TEMPLATE_AUDIT_NEED = 2;
    /**
     * 模板不需要送审
     */
    int TEMPLATE_AUDIT_NOT = 1;
    /**
     * 模板送审 Redis缓存5分钟，防止送审还未保存，审核状态已经来到。
     */
    String TEMPLATE_AUDIT_CACHE_REDIS_KEY = "auditing_templated:%s";

    /**
     * 插入msg_record数据库 Redis缓存5分钟，防止msgRecord还插入 状态报告等却已经到了
     */
    String MSG_RECORDS_INSERT_REDIS_KEY = "msg_record_insert_lock:%s";

    /**
     *   Redis缓存7天，表明该5G消息 对应的messageId是选择的回落5G阅信
     */
    String FIFTH_MSG_FALLBACK_READING_LETTER_KEY = "fifth_msg_fallback_reading_letter:%s";

    /**
     * 模板状态回调锁
     */
    String TEMPLATE_AUDIT_CALLBACK_LOCK_REDIS_KEY = "template_audit_callback_lock:%s";

    //蜂动多触发 发送任务的rediskey前缀
    String FONTDO_NEW_TEMPLATE_SEND_TASK_PREFIX = "FONTDO:SEND:TASK:";
    //蜂动多触发 发送任务的rediskey前缀
    String FONTDO_NEW_TEMPLATE_MODULE_INFORMATION_PREFIX = "FONTDO:SEND:MODULE:INFORMATION:";
    //蜂动多触发 发送任务的rediskey前缀
    String FONTDO_NEW_TEMPLATE_BUTTON_PREFIX = "FONTDO:SEND:BUTTON:";
    //蜂动多触发 付费类型的rediskey前缀
    String FONTDO_NEW_TEMPLATE_PAY_TYPE_PREFIX = "FONTDO:SEND:PAYTYPE:";

    /**
     * 未刪除
     */
    int DELETE_STATUS_NOT = 0;
    /**
     * 已刪除
     */
    int DELETE_STATUS_DID = 1;

    /**
     * 是否经过用户二次确认   0 未进行二次确认
     */
    Integer isChecked_first = 0;
    /**
     * 是否经过用户二次确认    1已经进行二次确认
     */
    Integer isChecked_secondary = 1;
}
