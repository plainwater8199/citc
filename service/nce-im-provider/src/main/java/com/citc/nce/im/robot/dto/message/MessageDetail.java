package com.citc.nce.im.robot.dto.message;

import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/13 9:21
 */
@Data
public class MessageDetail {
    private String imgSrc;
    private String pictureUrlId;
    private String audioUrlId;
    private String videoUrlId;
    private String name;
    private String size;
    private String mainCoverId;
    private Location location;
    private Baidu baidu;
    private LocationDetail locationDetail;
}
