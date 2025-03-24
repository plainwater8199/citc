package com.citc.nce.robotfile.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.dto.*;
import com.citc.nce.common.vo.tempStore.Csp4CustomerImg;
import com.citc.nce.fileApi.PictureApi;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.robotfile.entity.PictureDo;
import com.citc.nce.robotfile.service.IPictureService;
import com.citc.nce.vo.DeleteResp;
import com.citc.nce.vo.PageResultResp;
import com.citc.nce.vo.PictureResp;
import com.citc.nce.vo.tempStore.UpdateTid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: PictureController
 */
@Api(value = "图片管理")
@RestController
@Slf4j
public class PictureController implements PictureApi {

    @Resource
    private IPictureService pictureService;

    @Resource
    private FileApi fileApi;

    @Override
    @ApiOperation(value = "自定义指令送审保存图片")
    @PostMapping(value = "/material/picture/customCommandSave")
    public void customCommandSave(@RequestBody @Valid CustomCommandPictureReq req) {
        pictureService.customCommandSave(req);
    }

    @Override
    @ApiOperation(value = "保存图片")
    @PostMapping(value = "/material/picture/save")
    public void savePicture(@RequestBody @Valid PictureReq req) {
        pictureService.savePicture(req);
    }

    @Override
    public void savePictureList(@RequestBody PictureSaveReq saveReq) {
        pictureService.savePictureList(saveReq.getList());
    }

    @Override
    @PostMapping(value = "/material/picture/savePictureList/Csp4CustomerImg")
    public Map<Long, Csp4CustomerImg> savePictureList(@RequestBody List<Csp4CustomerImg> list) {
        return pictureService.savePictureListCsp4CustomerImg(list);
    }


    @Override
    @ApiOperation(value = "展示所有图片")
    @PostMapping(value = "/material/picture/list")
    public PageResultResp<PictureResp> selectByPage(@RequestBody @Valid PageReq req) {
        return pictureService.selectAll(req);
    }

    @Override
    @ApiOperation(value = "批量删除图片")
    @PostMapping(value = "/material/picture/delete")
    public DeleteResp deletePicture(@RequestBody @Valid MoveGroupReq req) {
        return pictureService.deletePicture(req);
    }

    @Override
    @ApiOperation(value = "对图片进行分组")
    @PostMapping(value = "/material/picture/move")
    public void movePicture(@RequestBody @Valid MoveGroupReq req) {
        pictureService.movePicture(req);
    }


    @PostMapping(value = "/material/picture/findByUuid")
    @Override
    public PictureResp findByUuid(@RequestBody PictureReq req) {
        LambdaQueryWrapper<PictureDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PictureDo::getPictureUrlId, req.getPictureUrlId());
        PictureDo one = pictureService.getOne(wrapper);
        if (Objects.isNull(one)) {
            log.error("文件资源不存在 pictureUrlId:{}", req.getPictureUrlId());
            throw new BizException("文件资源不存在");
        }
        PictureResp pictureResp = new PictureResp();
        BeanUtils.copyProperties(one, pictureResp);
        return pictureResp;
    }

    @Override
    @PostMapping(value = "/material/picture/updateTid")
    public void updateTid(@RequestBody List<UpdateTid> updateTidList) {
        pictureService.updateTid(updateTidList);
    }

    @Override
    public void updatePictureThumbnail(@RequestBody @Valid PictureThumbnailReq thumbnailReq) {
        pictureService.updatePictureThumbnail(thumbnailReq.getPictureUrlId(),thumbnailReq.getThumbnailId());
    }
}
