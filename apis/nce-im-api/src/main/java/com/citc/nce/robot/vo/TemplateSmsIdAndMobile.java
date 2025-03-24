package com.citc.nce.robot.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ping chen
 */
@Data
public class TemplateSmsIdAndMobile implements Serializable {

	/**
	*/
	private static final long serialVersionUID = 1L;
	private String mobile;
	private String customSmsId;
	private Map<String, String> content;

	public TemplateSmsIdAndMobile() {

	}

	public TemplateSmsIdAndMobile(String mobile) {
		this.mobile = mobile;
	}

	public TemplateSmsIdAndMobile(String mobile, String customSmsId) {
		this.mobile = mobile;
		this.customSmsId = customSmsId;
	}

	public TemplateSmsIdAndMobile(String mobile, String customSmsId, Map<String, String> content) {
		this.mobile = mobile;
		this.customSmsId = customSmsId;
		this.content = content;
	}


}
