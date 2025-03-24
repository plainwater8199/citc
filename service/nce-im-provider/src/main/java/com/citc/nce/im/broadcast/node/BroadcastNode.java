package com.citc.nce.im.broadcast.node;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.msgenum.SendStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群发节点定义，用来解析robot_group_plans_desc(群发计划定义)里的群发计划详情
 *
 * @author jcrenc
 * @since 2024/3/26 15:52
 */
@Data
public class BroadcastNode {
    /**
     * root:根节点，1:5G消息  2：短信  3：视频短信
     */
    private String type;
    /**
     * 当前节点id
     */
    private String leftPoint;

    /**
     * 父节点id
     */
    private String parentPoint;

    /**
     * 节点详情
     */
    private NodeParam params;

    private List<BroadcastNode> children;

    @Data
    public static class NodeParam {
        /**
         * 数据库发送节点id
         */
        private String id;
        /**
         * 父节点的数据库发送节点id
         */
        private String parentId;
        /**
         * 5g消息模板id
         */
        private String templateId;
        /**
         * 短信模板id
         */
        private String shortMsgTemplateId;
        /**
         * 视频短信模板id
         */
        private String mediaTemplateId;
        /**
         * 1普通模板 2变量模板
         */
        private Integer templateType;
        /**
         * 模板名称
         */
        private String templateName;
        /**
         * 联系人组id
         */
        private String planGroup;
        /**
         * 联系人组名称
         */
        private String groupName;
        /**
         * 是否是定时发送节点
         */
        private Boolean isTime;
        /**
         * 定时发送时间
         */
        private LocalDateTime sendTime;

        /**
         * 默认路由
         */
        @JsonAlias("default")
        private OutRouter defaultRoute;
        /**
         * 人群筛选
         */
        private List<FilterStrategy> screen;

        private SendStatus status;
        //是否允许回落( 0 不允许, 1允许)
        private int allowFallback;
        // 1短信回落 ,  2  5G阅信回落
        private Integer fallbackType;
        //回落短信内容(allowFallback = 1时, 必填)
        private JSONObject fallbackSmsContent;
        //允许回落阅读信模板id(fallbackType = 2时, 必填)
        private Long fallbackReadingLetterTemplateId;
        //选择需要发送的运营商,英文逗号拼接 (1,2,3)
        private String selectedCarriers;

    }

    @Data
    public static class OutRouter {
        /**
         * 路由的出口子节点
         */
        private List<String> children;
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FilterStrategy extends OutRouter {
        /**
         * 人群筛选条件
         */
        private CrowdFilterCondition condition;
        private String btnUuid;
    }

    @Getter
    @RequiredArgsConstructor
    public enum CrowdFilterCondition {
        DELIVERED("delivered", "发送成功"),
        DISPLAYED("displayed", "消息已阅"),
        UNREAD("unread", "消息未读"),
        CLICK("click", "点击按钮"),
        NOT_CLICK("notClick", "未点击按钮"),
        FAILED("failed", "发送失败");
        @JsonValue
        private final String value;
        private final String desc;
    }
}

