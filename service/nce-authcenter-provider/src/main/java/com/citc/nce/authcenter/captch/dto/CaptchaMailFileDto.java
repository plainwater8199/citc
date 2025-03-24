package com.citc.nce.authcenter.captch.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/30 15:20
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CaptchaMailFileDto implements Serializable {
    /**
     * 附件的文件字节流的base64编码
     */
    private String content;
    /**
     * 附件的文件名
     */
    private String name;
}
