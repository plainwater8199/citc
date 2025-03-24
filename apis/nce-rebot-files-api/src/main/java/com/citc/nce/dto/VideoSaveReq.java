package com.citc.nce.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: VideoDto
 */
@Data
public class VideoSaveReq {
    private List<VideoReq> list;
}
