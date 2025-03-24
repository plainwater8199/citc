package com.citc.nce.im.session.processor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.im.session.ApplicationContextUil;
import com.citc.nce.im.session.entity.Gps;
import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.bean.RobotVariableBean;
import com.citc.nce.robot.vo.RobotVariablePageReq;
import com.citc.nce.robot.vo.RobotVariableResp;
import com.citc.nce.robot.vo.UpMsgReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class Variable {
    private static Logger log = LoggerFactory.getLogger(Variable.class);

    @Autowired
    RobotVariableApi robotVariableApi;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //初始化变量值
    public void init(NodeProcessor nodeProcessor) {
//        String conversationId = nodeProcessor.getUpMsgReq().getConversationId();

        RobotVariablePageReq robotVariablePageReq = new RobotVariablePageReq();
        robotVariablePageReq.setPageNo(0);
        robotVariablePageReq.setPageSize(1000);
        robotVariablePageReq.setCreator(nodeProcessor.getUpMsgReq().getCreate());
        RobotVariableResp robotVariableResp = robotVariableApi.listAll(robotVariablePageReq);
        SessionContext sessionContext = new SessionContext();
        List<RobotVariableBean> variableList = robotVariableResp.getList();
        if (CollectionUtil.isNotEmpty(variableList)) {
            variableList.forEach(robotVariableBean -> {
                String variableName = robotVariableBean.getVariableName();
                String newVariableName = "{{custom-" + variableName + "}}";
                String variableValue = robotVariableBean.getVariableValue();
                sessionContext.addVar(nodeProcessor.getUpMsgReq(), newVariableName, variableValue);
            });
            nodeProcessor.setSessionContext(sessionContext);
        }
    }

    /**
     * {{sys-time}}    系统时间
     * {{sys-phone}}   手机号
     * {{sys-process}} 触发流程上行
     * {{question-场景-流程-节点名称}}
     * {{名称}}
     * {{question-节点id/'1'-节点名称}}
     * {{custom-变量名x}}  读取sessionContext
     */
    public String translate(String msg, NodeProcessor nodeProcessor) {
        if (null == stringRedisTemplate) {
            stringRedisTemplate = ApplicationContextUil.getBean(StringRedisTemplate.class);
        }
        UpMsgReq upMsgReq = nodeProcessor.getUpMsgReq();
        msg = msg.replace("{{sys-time}}", changeTimeZone());
        if (StringUtils.hasLength(upMsgReq.getPhone())) {
            msg = msg.replace("{{sys-phone}}", upMsgReq.getPhone());
        } else {
            msg = msg.replace("{{sys-phone}}", "");
        }

        SessionContext sessionContext = nodeProcessor.getSessionContext();
        try {
            Map<Object, Object> var = sessionContext.getVarForRedis(upMsgReq);
            Set<Map.Entry<Object, Object>> entries = var.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (StringUtils.hasLength(key)) {
                    if (!StringUtils.hasLength(value)) {
                        value = "";
                    }
                    msg = msg.replace(key, value);
                }
            }
        } catch (Exception e) {
            log.error("变量查询为空");
        }
        log.info("变量查询结果-------------------{}", msg);
        if (msg.contains("sys-process")) {
            String upMessage = stringRedisTemplate.opsForValue().get(upMsgReq.getConversationId() + "sys-process");
            msg = msg.replace("{{sys-process}}", upMessage);
        }
        //最后处理question
        List<String> variableList = getSplintValue(msg, "\\{\\{", "}}");
        for (String variable : variableList) {
            if (variable.contains("question")) {
                //截取节点id
                List<String> splintValue = getSplintValue(variable, "\\(", ")");
                String replace = splintValue.get(0).replace("{{question-(", "");
                replace = replace.replace(")}}", "");
                String questionData = stringRedisTemplate.opsForValue().get(upMsgReq.getConversationId() + replace);
                log.info("进入提问回复：{0},{1}", replace, questionData);

                if (StringUtils.hasText(questionData)) {
                    if(questionData.startsWith("geo:")&&questionData.contains("u=")&&questionData.contains("crs=")&&questionData.contains("rcs-l=")){
                        String[] split = questionData.split(";");
                        String addr="经度为";
                        for (int i=0;i<split.length;i++){
                            if(split[i].contains("geo:")){
                                String replaceadd = split[i].replace("geo:", "");
                                String[] split1 = replaceadd.split(",");
                                //Gps gps = bd09_To_Gcj02(Double.parseDouble(split1[0]), Double.parseDouble(split1[1]));
                                addr+=split1[1]+",维度为"+split1[0];

                            }
                            if(split[i].contains("rcs-l=")){
                                String replacercs = split[i].replace("rcs-l=", "");
                                try {
                                    String decode = URLDecoder.decode(replacercs, "utf-8");
                                    addr+=",详细地址为:"+decode;
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        msg = msg.replace(variable, addr);
                    }else{
                        msg = msg.replace(variable, questionData);
                    }
                } else {
                    msg = msg.replace(variable, "");
                }
            }
        }
        return msg;
    }

    private String changeTimeZone() {
        Calendar instance = Calendar.getInstance();
        /*LocalDateTime time = LocalDateTime.now();
        ZonedDateTime zonedDateTime = time.atZone(ZoneId.of("GMT-8"));
        ZonedDateTime converted = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT+8"));
        Date date = Date.from(converted.toInstant());*/
        return DateUtil.format(instance.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    private List<String> getSplintValue(String msg, String begin, String end) {
        List<String> stringList = new ArrayList<>();
        String[] split = msg.split("\\{\\{");
        for (int i = 0; i < split.length; i++) {
            String tem = split[i];
            int index = tem.indexOf("}}");
            if (index != -1) {
                String substring = tem.substring(0, index);
                stringList.add("{{" + substring + "}}");
            }
        }
        return stringList;
    }
}
