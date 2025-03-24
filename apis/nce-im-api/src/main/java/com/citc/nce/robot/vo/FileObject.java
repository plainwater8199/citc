package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileObject {
    //中旬系统id
    private String fileId;

    //缩略图id
    private String thumbnailId;

    private List<Suggestions> suggestions;
}
