package com.citc.nce.im.broadcast.fastgroupmessage.configure;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rocketmq.fastgroup.send")
public class FastGroupRocketMQConfigure {
    private String group;
    private String topic;
}
