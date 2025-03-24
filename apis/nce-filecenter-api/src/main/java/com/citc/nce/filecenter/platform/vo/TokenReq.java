package com.citc.nce.filecenter.platform.vo;
import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月11日15:44:12
 * @Version: 1.0
 * @Description: 获取平台token
 */
@Data
public class TokenReq {
    private String accessToken;

    //styleFile 样式文件
    //media     素材文件
    private String fileType;
}
