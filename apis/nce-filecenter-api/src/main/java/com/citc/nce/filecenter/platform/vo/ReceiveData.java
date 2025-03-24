package com.citc.nce.filecenter.platform.vo;

import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月20日15:05:21
 * @Version: 1.0
 * @Description: 获取平台token响应体接受类
 */
@Data
public class ReceiveData {

    private Integer code;

    private String message;

    private TokenData data;

}
