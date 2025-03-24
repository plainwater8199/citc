package com.citc.nce.robot.api.mall.common;

import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import lombok.Data;

import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:30
 */
@Data
public class Material {

    List<ResourcesImg> pictureList;
    List<ResourcesVideo> videoList;
    List<ResourcesAudio> audioList;
}
