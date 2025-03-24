package com.citc.nce.im.tempStore.controller;


import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.service.IResourcesImgGroupService;
import com.citc.nce.robot.api.tempStore.ResourcesImgGroupApi;
import com.citc.nce.robot.api.tempStore.bean.images.GroupAdd;
import com.citc.nce.robot.api.tempStore.bean.images.GroupInfo;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImgGroup;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 02:11:37
 */
@RestController
@AllArgsConstructor
public class ResourcesImgGroupController implements ResourcesImgGroupApi {

    private final IResourcesImgGroupService groupService;

    @Override
    public void addOrUpdate(GroupAdd var) {
        groupService.addOrUpdate(var);
    }

    @Override
    public boolean remove(List<Long> ids) {
        List<ResourcesImgGroup> imgGroups = groupService.listByIds(ids);
        imgGroups.forEach(s -> SessionContextUtil.sameCsp(s.getCreator()));
        //原来代码没检查分组id是否使用
        return groupService.removeBatchByIds(ids);
    }

    @Override
    public List<GroupInfo> list() {
        String userId = SessionContextUtil.getUser().getUserId();
        return groupService.listGroupInfo(userId);
    }
}

