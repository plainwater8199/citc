package com.citc.nce.im.robot.util;

import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.im.msgenum.MsgActionEnum;
import com.citc.nce.robot.vo.BaseMessage;
import com.citc.nce.robot.vo.FileMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/2/22 14:27
 */
public class MsgUtils {
    private MsgUtils() {
    }

    /**
     * 根据上行消息类型和内容判断是否是上行位置信息
     *
     * @param action  消息类型code，会自动转换为MsgActionEnum
     * @param msgData 消息内容
     * @return 如果是则返回true
     */
    public static boolean isGeoMsg(String action, String msgData) {
        return isGeoMsg(MsgActionEnum.byCode(action), msgData);
    }

    public static boolean isGeoMsg(MsgActionEnum action, String msgData) {
        return MsgActionEnum.TEXT == action
                && StringUtils.contains(msgData, "geo:")
                && StringUtils.contains(msgData, "u=")
                && StringUtils.contains(msgData, "crs=")
                && StringUtils.contains(msgData, "rcs-l=");
    }


    public static boolean trySendSessionMessage(BaseMessage message, CSPOperatorCodeEnum operator) {
        StringRedisTemplate stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        boolean result;
        if (!(message instanceof FileMessage)) {
            result = false; //非直连渠道没有会话消息
        } else {
            String contributionId = ((FileMessage) message).getContributionId();
            if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey("chatbot-conversation:" + contributionId))) {
                result = false; //会话有效时间已过，不能视作会话消息
            } else {
                switch (operator) {
                    case CMCC:
                    case CT: {
                        result = true;
                        break;
                    }
                    case CUNC: {
                        result = Boolean.TRUE.equals(
                                stringRedisTemplate.opsForValue()
                                        .setIfAbsent("chatbot-inReplyTo:" + contributionId, LocalDateTime.now().toString(), Duration.ofMinutes(30))
                        );
                        break;
                    }
                    default: {
                        result = false; //除三网运营商的其他运营商（硬核桃）都没有会话消息
                        break;
                    }
                }
            }
        }
        return result;
    }

}
