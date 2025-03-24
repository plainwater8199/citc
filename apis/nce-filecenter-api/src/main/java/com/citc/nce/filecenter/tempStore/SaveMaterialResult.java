package com.citc.nce.filecenter.tempStore;

import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class SaveMaterialResult {
    Map<Long, Csp4CustomerAudio> audioMap;
    Map<Long, Csp4CustomerImg> imgMap;
    Map<Long, Csp4CustomerVideo> videoMap;

    private long threadId;
}
