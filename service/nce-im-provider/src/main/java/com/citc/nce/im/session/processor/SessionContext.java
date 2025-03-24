package com.citc.nce.im.session.processor;

import com.alibaba.fastjson.annotation.JSONField;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.robot.vo.UpMsgReq;
import com.github.pagehelper.util.StringUtil;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/4 19:20
 * @Version: 1.0
 * @Description: 会话上下文管理对象
 */
@Data
public class SessionContext {

    @JSONField(serialize = false)
    private StringRedisTemplate stringRedisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);

    /**
     * 流程变量
     */
    private Map<String, String> var = new HashMap<String, String>();

    public Map<Object, Object> getVarForRedis(UpMsgReq upMsgReq) {
        StringBuffer redisKey = getStringBuffer(upMsgReq);
        return stringRedisTemplate.opsForHash().entries(redisKey + upMsgReq.getConversationId());
    }

    private String get(String varName) {
        return var.get(varName);
    }

    public String addVar(UpMsgReq upMsgReq,String varName, String value) {
        StringBuffer redisKey = getStringBuffer(upMsgReq);
        String expireTime = stringRedisTemplate.opsForValue().get(redisKey + "RebotSettingexpireTime" + upMsgReq.getConversationId());
        Object obj = stringRedisTemplate.opsForHash().get(redisKey + upMsgReq.getConversationId(), varName);
        if (null != obj) {
            stringRedisTemplate.opsForHash().delete(redisKey + upMsgReq.getConversationId(),varName);
        }
        stringRedisTemplate.opsForHash().put(redisKey + upMsgReq.getConversationId(),varName,value);
        stringRedisTemplate.expire(redisKey + upMsgReq.getConversationId(), Integer.parseInt(expireTime), TimeUnit.MINUTES);
        return var.put(varName, value);
    }

    @NotNull
    private StringBuffer getStringBuffer(UpMsgReq upMsgReq) {
        StringBuffer redisKey  = new StringBuffer();
        redisKey.append(upMsgReq.getChatbotAccount());
        if (StringUtil.isNotEmpty(upMsgReq.getPhone())) {
            redisKey.append("@");
            redisKey.append(upMsgReq.getPhone());
        }
        redisKey.append(":");
        return redisKey;
    }

    @JSONField(serialize = false)
    public UpMsgReq upMsgReq;
}
