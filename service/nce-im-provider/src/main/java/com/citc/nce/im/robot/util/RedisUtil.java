package com.citc.nce.im.robot.util;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.session.ApplicationContextUil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.citc.nce.common.core.exception.GlobalErrorCode.INTERNAL_SERVER_ERROR;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/7/12 18:00
 */
public class RedisUtil {
    private static final String PUT_CONVERSATION_KEY_PREFIX = "robot:phone:";
    private static final String PROCESS_KEY_PREFIX = "robot:process:";
    private static final String CHECK_SUBPROCESS_UNIQUE_KEY_PREFIX = "subProcessUnique:";
    private static final String CONVERSATION_KEY_PREFIX = "robot:";
    private static final String FILE_URL = "fileUrl";

    private static final String BAIDUWENXIN_KEY_PREFIX = "robot:baiduWenxinToken:";
    //不允许随意更改, 在AuthCenter中也有使用
    private static final String BAIDUWENXIN_IM_KEY_PREFIX = "im:baiduWenxinToken:";

    public static final String CONVERSATION_TRIGGER_PROCESS_MSG = "@sys-process";
    public static final String EFFECTIVE_CONVERSATION = ":ValidConversation";
    public static final String CONVERSATION_TRIGGER_MULTIPLE_PROCESS_MSG = "triggerMultipleProcessMsgKey@";
    public static final String CONVERSATION_VARIABLE = "variables";

    public static final String CONVERSATION_ORDER = "order";

    public static final String QUESTION_NODE_REPLY = "questionNodeReply";

    /**
     * getCheckSubProcessUniqueKey
     *
     * @param conversation
     * @return robot:subProcessUnique:AAAAAAAAAA:triggerMsg
     */
    public static String getTriggerProcessMsgKey(String conversation) {
        return CONVERSATION_KEY_PREFIX + conversation + ":" + CONVERSATION_TRIGGER_PROCESS_MSG;
    }
    /**
     * getCheckSubProcessUniqueKey
     *
     * @param conversation
     * @return robot:subProcessUnique:AAAAAAAAAA:triggerMsg
     */
    public static String getFileUrlKey(String conversation) {
        return CONVERSATION_KEY_PREFIX + conversation + ":" + FILE_URL;
    }

    /**
     * getBaiduWenxinToken
     *
     * @param userId
     * @return robot:baiduWenxinToken:
     */
    public static String getBaiduWenxinTokenKey(String userId) {
        return BAIDUWENXIN_KEY_PREFIX + userId;
    }

    /**
     * getBaiduWenxinToken
     *
     * @param modelId 大模型Id
     * @return robot:baiduWenxinToken:
     */
    public static String getBaiduwenxinImTokenKey(Long modelId) {
        return BAIDUWENXIN_IM_KEY_PREFIX + modelId;
    }

    /**
     * triggerMultipleProcessMsgKey
     *
     * @param conversation
     * @return robot:subProcessUnique:AAAAAAAAAA:triggerMultipleProcessMsgKey
     */
    public static String triggerMultipleProcessMsgKey(String conversation, String butId) {
        return CONVERSATION_KEY_PREFIX + conversation + ":" + CONVERSATION_TRIGGER_MULTIPLE_PROCESS_MSG + butId;
    }

    /**
     * getCheckSubProcessUniqueKey
     *
     * @param conversation
     * @param subProcessId
     * @return robot:subProcessUnique:AAAAAAAAAA:111
     */
    public static String getCheckSubProcessUniqueKey(String conversation, String subProcessId) {
        if (StringUtils.isNotEmpty(subProcessId)) {
            return CONVERSATION_KEY_PREFIX + conversation + CHECK_SUBPROCESS_UNIQUE_KEY_PREFIX + subProcessId;
        }
        return null;
    }

    /**
     * getConversationKeyByPhone
     *
     * @param phone
     * @return robot:phone:13111111111
     */
    public static String getConversationKeyByPhone(String phone, String chatbotId) {
        return PUT_CONVERSATION_KEY_PREFIX + phone + ":" + chatbotId;
    }

    /**
     * checkEffectiveConversation
     *
     * @param conversation
     * @return robot:1234567890:ValidConversation
     */
    public static String getCheckEffectiveConversationKey(String conversation) {
        if (StringUtils.isNotEmpty(conversation)) {
            return CONVERSATION_KEY_PREFIX + conversation + EFFECTIVE_CONVERSATION;
        }
        return null;
    }

    /**
     * getRobotKeyByConversation
     *
     * @param conversation
     * @return robot:AAAAAAAAAA
     */
    public static String getRobotKeyByConversation(String conversation) {
        if (StringUtils.isEmpty(conversation)) {
            throw new BizException(500, "会话ID不能为空");
        }
        return CONVERSATION_KEY_PREFIX + conversation;
    }

    /**
     * @param conversationId 会话ID
     * @return 变量key
     */
    public static String getVariableKey(String conversationId) {
        return CONVERSATION_KEY_PREFIX + conversationId + ":" + CONVERSATION_VARIABLE;
    }

    /**
     * @param conversationId 会话ID
     * @return 指令key
     */
    public static String getOrderKey(String conversationId) {
        return CONVERSATION_KEY_PREFIX + conversationId + ":" + CONVERSATION_ORDER;
    }

    /**
     * @param conversationId 会话ID
     * @param nodeId         节点ID
     * @return 提问节点回复键
     * @example robot:conversation:questionNodeReply:nodeId
     */
    public static String getQuestionNodeReplyKey(String conversationId, String nodeId) {
        return CONVERSATION_KEY_PREFIX + conversationId + ":" + QUESTION_NODE_REPLY + ":" + nodeId;
    }
}
