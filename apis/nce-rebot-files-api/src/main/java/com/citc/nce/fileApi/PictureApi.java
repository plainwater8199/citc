package com.citc.nce.fileApi;

import com.citc.nce.dto.*;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.PictureResp;
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
@FeignClient(contextId = "picture",value = "robot-files-service", url = "${robotFile:}")
public interface PictureApi {
    @PostMapping(value = "/material/picture/save")
    void savePicture(@RequestBody PictureReq req);
    @PostMapping(value = "/material/picture/savePictureList")
    void savePictureList(@RequestBody PictureSaveReq saveReq);

    @PostMapping(value = "/material/picture/savePictureList/Csp4CustomerImg")
    Map<Long, Csp4CustomerImg> savePictureList(@RequestBody List<Csp4CustomerImg> list);

    @PostMapping(value = "/material/picture/customCommandSave")
    void customCommandSave(@RequestBody CustomCommandPictureReq req);

    @PostMapping(value = "/material/picture/list")
    PageResultResp<PictureResp> selectByPage(@RequestBody PageReq req);

    @PostMapping(value = "/material/picture/delete")
    DeleteResp deletePicture(@RequestBody MoveGroupReq req);

    @PostMapping(value = "/material/picture/move")
    void movePicture(@RequestBody MoveGroupReq req);

    @PostMapping(value = "/material/picture/findByUuid")
    PictureResp findByUuid(@RequestBody PictureReq req);

    /**
     * 更新指定文件的tid
     *
     * @param updateTidList
     */
    @PostMapping(value = "/material/picture/updateTid")
    void updateTid(@RequestBody List<UpdateTid> updateTidList);

    @PostMapping(value = "/material/picture/thumbnail")
    void updatePictureThumbnail(@RequestBody PictureThumbnailReq thumbnailReq);

}
