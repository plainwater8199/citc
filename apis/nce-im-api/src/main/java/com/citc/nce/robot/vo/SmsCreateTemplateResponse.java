package com.citc.nce.robot.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 单条短信发送响应
 * @author ping chen
 *
 */
@Data
public class SmsCreateTemplateResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private String templateId;
}
