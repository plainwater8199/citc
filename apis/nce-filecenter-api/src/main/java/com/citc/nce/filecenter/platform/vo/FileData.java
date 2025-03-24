package com.citc.nce.filecenter.platform.vo;

import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月19日10:58:25
 * @Version: 1.0
 * @Description: 上传文件响应
 */
@Data
public class FileData {
    private String fileTid;

    private String thumbnailTid;

    private Integer fileCount;

    private Integer totalCount;
}
