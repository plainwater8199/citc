package com.citc.nce.dto;

import lombok.Data;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月11日15:46:31
 * @Version: 1.0
 * @Description: 文件审核结果接收
 */
@Data
public class FileAccept {

    //文件传输Id
    private String fileId;

    //素材是否可用
    private Integer useable;

    private String until;

    private String type;

    private String time;

    private String thumbnailTid;

    //应用Id
    private String appId;
}
