package com.citc.nce.robot.sms;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 状态报告数据
 * @author ping chen
 *
 */
@Setter
@Getter
@ToString
public class ReportResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String smsId;// 短信唯一标识

	private String customSmsId;// 客户自定义SmsId 

	private String state;// 成功失败标识

	private String desc;// 状态报告描述

	private String mobile;// 手机号

	private String receiveTime;// 状态报告返回时间

	private String submitTime;// 信息提交时间

	private String extendedCode;// 扩展码

}
