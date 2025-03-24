package com.citc.nce.fileApi;

import com.citc.nce.dto.*;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.VideoResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日17:50:49
 * @Version: 1.0
 * @Description: UpFileDto
 */
@FeignClient(contextId = "video", value = "robot-files-service", url = "${robotFile:}")
public interface VideoApi {

    @PostMapping(value = "/material/video/save")
    void saveVideo(@RequestBody VideoReq req);

    @PostMapping(value = "/material/video/saveVideoList")
    void saveVideoList(@RequestBody VideoSaveReq saveReq);

    @PostMapping(value = "/material/video/saveVideoList/Csp4CustomerVideo")
    Map<Long, Csp4CustomerVideo> saveVideoList(@RequestBody List<Csp4CustomerVideo> list);

    @PostMapping(value = "/material/video/findOne")
    VideoResp findOne(@RequestBody IdReq req);

    @PostMapping(value = "/material/video/list")
    PageResultResp<VideoResp> selectAllVideos(@RequestBody PageReq req);

    @PostMapping(value = "/material/video/update")
    void updateVideo(@RequestBody VideoReq req);

    @PostMapping(value = "/material/video/delete")
    DeleteResp deleteVideo(@RequestBody IdReq req);

    @PostMapping(value = "/material/video/findOneByUuid")
    VideoResp findOneByUuid(@RequestBody VideoReq req);

    /**
     * 更新指定文件的tid
     *
     * @param updateTidList
     */
    @PostMapping(value = "/material/video/updateTid")
    void updateTid(@RequestBody List<UpdateTid> updateTidList);

    @PostMapping(value = "/material/video/thumbnail")
    void updateVideoThumbnail(@RequestBody VideoThumbnailReq thumbnailReq);
}
