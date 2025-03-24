package com.citc.nce.robotfile.controller;


import com.citc.nce.common.vo.tempStore.Csp4CustomerAudio;
import com.citc.nce.dto.AudioReq;
import com.citc.nce.dto.AudioSaveReq;
import com.citc.nce.dto.IdReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.fileApi.AudioApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.robotfile.service.IAudioService;
import com.citc.nce.vo.AudioResp;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: AudioController
 */
@Api(value = "音频管理")
@RestController
public class AudioController implements AudioApi {

    @Resource
    private IAudioService audioService;


    @Resource
    private FileApi fileApi;

    @Override
    @ApiOperation(value = "保存音频")
    @PostMapping(value = "/material/audio/save")
    public void saveAudio(@RequestBody @Valid AudioReq audioReq) {
        audioService.saveAudio(audioReq);
    }

    @Override
    @ApiOperation(value = "保存音频")
    @PostMapping(value = "/material/audio/saveAudioList")
    public void saveAudioList(@RequestBody AudioSaveReq saveReq) {
        audioService.saveAudioList(saveReq.getList());
    }

    @Override
    @PostMapping(value = "/material/audio/saveAudioList/csp4customer")
    public Map<Long, Csp4CustomerAudio> saveAudioList(@RequestBody List<Csp4CustomerAudio> list) {
        return audioService.saveAudioListCsp4customer(list);
    }

    @Override
    @ApiOperation(value = "展示所有音频")
    @PostMapping(value = "/material/audio/list")
    public PageResultResp<AudioResp> selectAllAudios(@RequestBody @Valid PageReq pageReq) {
        return audioService.selectAll(pageReq);
    }

    @Override
    @ApiOperation(value = "删除一个音频")
    @PostMapping(value = "/material/audio/delete")
    public DeleteResp deleteAudio(@RequestBody @Valid IdReq req) {
        return audioService.deleteAudio(req);
    }

    @Override
    @ApiOperation(value = "获取音频信息")
    @PostMapping(value = "/material/audio/getFileInfoByAudioUrlId")
    public AudioResp getFileInfoByAudioUrlId(@RequestBody AudioReq audioReq) {
        return audioService.getFileInfoByAudioUrlId(audioReq);
    }

}
