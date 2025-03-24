package com.citc.nce.im.robot.dto.robot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jcrenc
 * @since 2023/7/27 16:23
 */
@ConfigurationProperties(prefix = "robot.process")
@Component
public class RobotConfigProperties {
    //流程的复制次数
    private Integer copyTime = 30;

    //复制速率--流程复制的时间间隔  单位S
    private Integer timeSpan = 5;

    //复制速率--流程在指定时间间隔内复制的最大数量
    private Integer intervalCount = 6;

    public Integer getCopyTime() {
        return copyTime;
    }

    public void setCopyTime(Integer copyTime) {
        this.copyTime = copyTime;
    }

    public Integer getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Integer timeSpan) {
        this.timeSpan = timeSpan;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }
}
