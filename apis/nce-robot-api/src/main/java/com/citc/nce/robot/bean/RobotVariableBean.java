package com.citc.nce.robot.bean;



import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RobotVariableBean implements Serializable {
    private long id;
    private String chatbotAccountId;
    private String variableName;
    private String variableValue;
    private String creator;
    private Date createTime;
    private String updater;
    private Date updateTime;
    private Integer deleted;
    private Date deletedTime;
}
