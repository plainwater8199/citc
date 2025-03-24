package com.citc.nce.robot.common;

import lombok.Getter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/30 10:45
 */
@Getter
public enum ResponsePriority {
    CHATBOT, KEYWORDS, DEFAULT;


    public static List<ResponsePriority> resolve(String priorities) {
        List<ResponsePriority> result = new ArrayList<>();
        result.add(CHATBOT);
        result.add(KEYWORDS);
        result.add(DEFAULT);
        if(StringUtils.hasLength(priorities) && !"default".equals(priorities)){
            String[] split = priorities.split(",");
            result = new ArrayList<>(split.length);
            for (String s : split) {
                result.add(ResponsePriority.valueOf(s.trim()));
            }
        }
        return result;
    }

    public static ResponsePriority getNext(List<ResponsePriority> responsePriorities, ResponsePriority currentStatus) {
        if(currentStatus == null){//如果当前状态为空，则返回第一个
            return responsePriorities.get(0);
        }else{
            int index = responsePriorities.indexOf(currentStatus);
            if (index != -1 && index < responsePriorities.size() - 1) {
                return responsePriorities.get(index + 1);
            }
            return null;//最后一个
        }
    }
}
