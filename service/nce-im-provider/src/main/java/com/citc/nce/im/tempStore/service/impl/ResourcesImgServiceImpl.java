package com.citc.nce.im.tempStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.mapper.ResourcesImgMapper;
import com.citc.nce.im.tempStore.service.IResourcesImgService;
import com.citc.nce.robot.api.tempStore.bean.images.ImgMove;
import com.citc.nce.robot.api.tempStore.bean.images.ImgPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组管理 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 11:11:28
 */
@Service
public class ResourcesImgServiceImpl extends ServiceImpl<ResourcesImgMapper, ResourcesImg> implements IResourcesImgService {

    @Override
    public void pageName(String userId, Page<ResourcesImg> page, ImgPageQuery query) {
        LambdaQueryWrapper<ResourcesImg> eq = new LambdaQueryWrapper<ResourcesImg>()
                .eq(ResourcesImg::getCreator, userId)
                .eq(Objects.nonNull(query.getImgGroupId()), ResourcesImg::getImgGroupId, query.getImgGroupId())
                .eq(Objects.isNull(query.getImgGroupId()), ResourcesImg::getImgGroupId,0)
                .like(StringUtils.hasLength(query.getName()), ResourcesImg::getPictureName, query.getName())
                .orderByDesc(ResourcesImg::getCreateTime);
        page(page, eq);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveImgGroup(ImgMove move) {
        List<ResourcesImg> imgs = listByIds(move.getImgIds());
        if(!imgs.isEmpty()){
            List<ResourcesImg> addImgs = new ArrayList<>();
            for (ResourcesImg img : imgs) {
                SessionContextUtil.sameCsp(img.getCreator());
                img.setImgGroupId(move.getImgGroupId());
                img.setImgId(null);
                addImgs.add(img);
            }
            saveBatch(addImgs);
        }
    }

    @Override
    public List<ResourcesImg> listByIdsDel(Collection<Long> ids) {
        return getBaseMapper().listByIdsDel(ids);
    }
}
