package com.citc.nce.robotfile.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.dto.AudioReq;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.robotfile.entity.AudioDo;
import com.citc.nce.vo.AudioResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IAudioService
 */
public interface IAudioService extends IService<AudioDo> {

    PageResultResp<AudioResp> selectAll(PageReq pageReq);

    void saveAudio(AudioReq audioDto);

    void saveAudioList(List<AudioReq> audioReq);
    Map<Long, Csp4CustomerAudio> saveAudioListCsp4customer(List<Csp4CustomerAudio> list);

    DeleteResp deleteAudio(IdReq req);

    AudioResp getFileInfoByAudioUrlId(@RequestBody AudioReq audioReq);

}
