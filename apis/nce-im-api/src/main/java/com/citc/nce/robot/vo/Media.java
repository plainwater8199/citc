package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class Media {
    private String mediaType;
    //多媒体文件id
    private String mediaId;
    //缩略图
    private String thumbnailId;
    //卡片高度
    private String height;
    //卡片内容描述
    private String contentDescription;
    //卡片标题
    private String title;
    //卡片描述
    private String description;
    //卡片上的按钮的数组
    private List<Suggestions> suggestions;
}
