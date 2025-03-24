package com.citc.nce.robot.sms;

/**
 * @author ping chen
 */
public class ResultModel {

	private String code;
	private String result;

	public ResultModel(String code, String result) {
		this.code = code;
		this.result = result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResultModel{" +
				"code='" + code + '\'' +
				", result='" + result + '\'' +
				'}';
	}
}
