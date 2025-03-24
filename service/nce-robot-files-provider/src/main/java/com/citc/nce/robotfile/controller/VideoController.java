package com.citc.nce.robotfile.controller;


import com.citc.nce.dto.*;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.fileApi.VideoApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.robotfile.service.IVideoService;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.VideoResp;
import com.citc.nce.vo.tempStore.UpdateTid;
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
 * @Description: VideoController
 */
@Api(value = "视频管理")
@RestController
public class VideoController implements VideoApi {

    @Resource
    private IVideoService videoService;

    @Resource
    private FileApi fileApi;

    @Override
    @ApiOperation(value = "保存视频")
    @PostMapping(value = "/material/video/save")
    public void saveVideo(@RequestBody @Valid VideoReq req) {
        videoService.saveVideo(req);
    }

    @Override
    @ApiOperation(value = "保存视频")
    @PostMapping(value = "/material/video/saveVideoList")
    public void saveVideoList(@RequestBody VideoSaveReq saveReq) {
        videoService.saveVideoList(saveReq.getList());
    }

    @Override
    @PostMapping(value = "/material/video/saveVideoList/Csp4CustomerVideo")
    public Map<Long, Csp4CustomerVideo> saveVideoList(@RequestBody List<Csp4CustomerVideo> list) {
        return videoService.saveVideoListCsp4CustomerVideo(list);
    }


    @Override
    @ApiOperation(value = "查找单个视频")
    @PostMapping(value = "/material/video/findOne")
    public VideoResp findOne(@RequestBody @Valid IdReq req) {
        return videoService.findOne(req.getId());
    }

    @Override
    @ApiOperation(value = "分页展示视频")
    @PostMapping(value = "/material/video/list")
    public PageResultResp<VideoResp> selectAllVideos(@RequestBody @Valid PageReq req) {
        return videoService.selectByPage(req);
    }

    @Override
    @ApiOperation(value = "更新视频信息")
    @PostMapping(value = "/material/video/update")
    public void updateVideo(@RequestBody @Valid VideoReq req) {
        videoService.updateVideo(req);
    }

    @Override
    @ApiOperation(value = "删除视频")
    @PostMapping(value = "/material/video/delete")
    public DeleteResp deleteVideo(@RequestBody @Valid IdReq req) {
        return videoService.deleteVideo(req.getId());
    }

    @PostMapping(value = "/material/video/findOneByUuid")
    @Override
    public VideoResp findOneByUuid(@RequestBody VideoReq req) {
        return videoService.findOneByUuid(req.getVideoUrlId());
    }

    @Override
    @PostMapping(value = "/material/video/updateTid")
    public void updateTid(@RequestBody List<UpdateTid> updateTidList) {
        videoService.updateTid(updateTidList);
    }

    @Override
    public void updateVideoThumbnail(@RequestBody @Valid VideoThumbnailReq thumbnailReq) {
        videoService.updateVideoThumbnail(thumbnailReq.getVideoUrlId(),thumbnailReq.getThumbnailId());
    }
}
