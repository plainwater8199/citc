package com.citc.nce.filecenter.platform.vo;

import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月18日14:51:11
 * @Version: 1.0
 * @Description: 获取平台token响应体接受类
 */
@Data
public class TokenData {
    private String token;

    private Integer expires;
}
