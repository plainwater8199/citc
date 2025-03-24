package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "robot.init")
public class RobotInitMsgConfigure {


    private String welcome;

}
