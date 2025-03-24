package com.citc.nce.im.mall.snapshot.model;

import com.citc.nce.auth.cardstyle.vo.CardStyleResp;
import lombok.Data;

@Data
public class TsCardStyleContent {
    private Long oldCardStyleId;
    //card_style 表 数据快照
    private CardStyleResp cardStyle;
    //json中的图片访问路径  "image": "/fileApi/file/download/id?req=78de0c0989214063bb4fdad066df97fc"
    private String reqUrlId;
}
