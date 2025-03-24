package com.citc.nce.filecenter.platform.vo;

import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月11日15:46:31
 * @Version: 1.0
 * @Description: 上传文件响应
 */
@Data
public class FileResp {

    private String success;

    private String message;

    //源文件的文件Id
    private String fileId;

    //fileType为素材时，如果传了缩略图，会返回缩略图的文件Id
    private String thumbnailId;
}
