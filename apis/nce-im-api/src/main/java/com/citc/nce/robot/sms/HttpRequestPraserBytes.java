package com.citc.nce.robot.sms;

import java.io.UnsupportedEncodingException;

/**
 * Http 请求解析器：byte[]
 * 
 * @author ping chen
 *
 */
public class HttpRequestPraserBytes implements HttpRequestPraser<byte[]> {

	/**
	 * 请求内容字符串
	 */
	private String contentString;

	@Override
	public String praseRqeuestContentToString(HttpRequestParams<byte[]> httpParams) {
		if (contentString != null) {
			return contentString;
		}
		try {
			contentString = new String(httpParams.getParams(), httpParams.getCharSet());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return contentString;
	}

	@Override
	public byte[] praseRqeuestContentToBytes(HttpRequestParams<byte[]> httpParams) {
		return httpParams.getParams();
	}

	@Override
	public int praseRqeuestContentLength(HttpRequestParams<byte[]> httpParams) {
		if (httpParams.getParams() != null) {
			return httpParams.getParams().length;
		} else {
			return 0;
		}
	}

}
