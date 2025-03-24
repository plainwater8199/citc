package com.citc.nce.robot.vo;

import lombok.Data;
import java.util.Map;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateReq{
    /**
     * appId
     */
    private String appId;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     *加密算法
     */
    private String algorithm = "AES/ECB/PKCS5Padding";
    /**
     *模板内容组装
     */
    private Map<String, String> templateMap;
    /**
     *是否压缩
     */
    private boolean isGzip = true;
    /**
     *编码格式
     */
    private String encode = "utf-8";


}
