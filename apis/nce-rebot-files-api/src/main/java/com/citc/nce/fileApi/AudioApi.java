package com.citc.nce.fileApi;

import com.citc.nce.dto.AudioReq;
import com.citc.nce.dto.AudioSaveReq;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.vo.AudioResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileDto
 */
@FeignClient(contextId = "audio", value = "robot-files-service", url = "${robotFile:}")
public interface AudioApi {
    @PostMapping(value = "/material/audio/save")
    void saveAudio(@RequestBody AudioReq audioReq);

    @PostMapping(value = "/material/audio/saveAudioList")
    void saveAudioList(@RequestBody AudioSaveReq saveReq);

    /**
     * 用户使用模板时添加音频素材
     */
    @PostMapping(value = "/material/audio/saveAudioList/csp4customer")
    Map<Long, Csp4CustomerAudio> saveAudioList(@RequestBody List<Csp4CustomerAudio> list);

    @PostMapping(value = "/material/audio/list")
    PageResultResp<AudioResp> selectAllAudios(@RequestBody PageReq pageReq);

    @PostMapping(value = "/material/audio/delete")
    DeleteResp deleteAudio(@RequestBody IdReq req);

    @PostMapping(value = "/material/audio/getFileInfoByAudioUrlId")
    AudioResp getFileInfoByAudioUrlId(@RequestBody AudioReq audioReq);

}
