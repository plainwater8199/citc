package com.citc.nce.im.tempStore.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.im.tempStore.service.IResourcesImgService;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.robot.api.tempStore.ResourcesImgApi;
import com.citc.nce.robot.api.tempStore.bean.images.ImgAdd;
import com.citc.nce.robot.api.tempStore.bean.images.ImgMove;
import com.citc.nce.robot.api.tempStore.bean.images.ImgPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组管理 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 11:11:28
 */
@RestController
@AllArgsConstructor
public class ResourcesImgController implements ResourcesImgApi {
    private IResourcesImgService imgGroupService;

    private final FileApi fileApi;

    @Override
    public List<ResourcesImg> listByIdsDel(List<Long> ids) {
        return imgGroupService.listByIdsDel(ids);
    }

    @Override
    public PageResult<ResourcesImg> page(ImgPageQuery query) {
        Page<ResourcesImg> page = PageSupport.getPage(ResourcesImg.class, query.getPageNo(), query.getPageSize());
        imgGroupService.pageName(SessionContextUtil.verifyCspLogin(), page, query);
        List<ResourcesImg> records = page.getRecords();
        List<String> fileUrls = records.stream().map(ResourcesImg::getPictureUrlid).distinct().collect(Collectors.toList());
        Map<String, String> autoThumbnail = fileApi.getAutoThumbnail(fileUrls);
        for (ResourcesImg record : records) {
            record.setAutoThumbnail(autoThumbnail.get(record.getPictureUrlid()));
        }
        return new PageResult<>(records, page.getTotal());
    }

    @Override
    public void add(ImgAdd add) {
        checkName(add.getPictureName());
        ResourcesImg resourcesImgGroup = new ResourcesImg();
        BeanUtils.copyProperties(add, resourcesImgGroup);
        resourcesImgGroup.setDeleted(0);
        imgGroupService.save(resourcesImgGroup);
    }

    /**
     * 检查重复
     *
     * @param pictureName 名称
     */
    private void checkName(String pictureName) {
        ResourcesImg one = imgGroupService.getOne(new LambdaUpdateWrapper<ResourcesImg>()
                .eq(ResourcesImg::getPictureName, pictureName)
                .eq(ResourcesImg::getCreator, SessionContextUtil.verifyCspLogin())
        );
        if (Objects.nonNull(one)) {
            throw new BizException("名称不能重复");
        }
    }

    @Override
    public boolean remove(List<Long> ids) {
        List<ResourcesImg> imgs = imgGroupService.listByIds(ids);
        imgs.forEach(s->SessionContextUtil.sameCsp(s.getCreator()));
        return imgGroupService.removeBatchByIds(ids);
    }

    @Override
    public void moveImg(ImgMove move) {
        imgGroupService.moveImgGroup(move);
    }
}

