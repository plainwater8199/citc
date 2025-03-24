package com.citc.nce.robotfile.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.dto.CustomCommandPictureReq;
import com.citc.nce.dto.MoveGroupReq;
import com.citc.nce.dto.PageReq;
import com.citc.nce.dto.PictureReq;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.robotfile.entity.PictureDo;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.GroupResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.PictureResp;
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
public interface IPictureService extends IService<PictureDo> {

    PageResultResp<PictureResp> selectAll(PageReq req);

    void savePicture(PictureReq dto);

    void customCommandSave(CustomCommandPictureReq dto);

    void movePicture(MoveGroupReq req);

    DeleteResp deletePicture(MoveGroupReq req);

    List<GroupResp> manage(PageReq pageReq);

    void savePictureList(List<PictureReq> req);

    Map<Long, Csp4CustomerImg> savePictureListCsp4CustomerImg(List<Csp4CustomerImg> list);


    void updateTid(List<UpdateTid> updateTidList);

    void updatePictureThumbnail(String pictureUrlId, String thumbnailId);
}
