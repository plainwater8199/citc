package com.citc.nce.robot.domain.mediatemplate;

import com.citc.nce.robot.domain.common.NameItem;
import lombok.Data;

import java.util.List;

/**
 * 视频短信模板文本内容实体定义
 *
 * @author jcrenc
 * @since 2024/3/5 10:02
 */
@Data
public class TextContent {
    private String name;
    private String value;
    private List<NameItem> names;
    private Long length;
}
