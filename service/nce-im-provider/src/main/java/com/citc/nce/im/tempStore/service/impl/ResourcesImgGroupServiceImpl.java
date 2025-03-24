package com.citc.nce.im.tempStore.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.mapper.ResourcesImgGroupMapper;
import com.citc.nce.im.tempStore.service.IResourcesImgGroupService;
import com.citc.nce.robot.api.tempStore.bean.images.GroupAdd;
import com.citc.nce.robot.api.tempStore.bean.images.GroupInfo;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImgGroup;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 02:11:37
 */
@Service
@AllArgsConstructor
public class ResourcesImgGroupServiceImpl extends ServiceImpl<ResourcesImgGroupMapper, ResourcesImgGroup> implements IResourcesImgGroupService {

    private final ResourcesImgGroupMapper groupMapper;

    @Override
    @Transactional
    public void addOrUpdate(GroupAdd var) {
        if (Objects.nonNull(var.getImgGroupId())) {
            ResourcesImgGroup imgGroup = getById(var.getImgGroupId());
            if (Objects.isNull(imgGroup)) {
                throw new BizException("分组不存在");
            }
            SessionContextUtil.sameCsp(imgGroup.getCreator());
        }
        ResourcesImgGroup resourcesImgGroup = new ResourcesImgGroup();
        resourcesImgGroup.setImgGroupId(var.getImgGroupId());
        resourcesImgGroup.setName(var.getName());
        saveOrUpdate(resourcesImgGroup);
    }

    @Override
    public List<GroupInfo> listGroupInfo(String userId) {
        return groupMapper.groupInfo(userId);
    }
}
