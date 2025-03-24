package com.citc.nce.robotfile.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.VideoReq;
import com.citc.nce.common.vo.tempStore.Csp4CustomerVideo;
import com.citc.nce.robotfile.entity.VideoDo;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.VideoResp;
import com.citc.nce.vo.tempStore.UpdateTid;

import java.util.List;
import java.util.Map;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
public interface IVideoService extends IService<VideoDo> {

    VideoResp findOne(Long id);

    PageResultResp<VideoResp> selectByPage(PageReq req);

    void saveVideo(VideoReq dto);

    void updateVideo(VideoReq dto);

    DeleteResp deleteVideo(Long id);

    VideoResp findOneByUuid(String videoUrlId);

    void saveVideoList(List<VideoReq> req);

    Map<Long, Csp4CustomerVideo> saveVideoListCsp4CustomerVideo(List<Csp4CustomerVideo> list);

    void updateTid(List<UpdateTid> updateTidList);

    void updateVideoThumbnail(String videoUrlId, String thumbnailId);
}
