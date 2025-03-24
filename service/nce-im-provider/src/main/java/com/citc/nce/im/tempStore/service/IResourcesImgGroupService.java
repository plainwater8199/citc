package com.citc.nce.im.tempStore.service;

import com.citc.nce.robot.api.tempStore.bean.images.GroupAdd;
import com.citc.nce.robot.api.tempStore.bean.images.GroupInfo;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImgGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 02:11:37
 */
public interface IResourcesImgGroupService extends IService<ResourcesImgGroup> {

    /**
     * 修改或新增
     * @param var 参数
     */
    void addOrUpdate(GroupAdd var);

    List<GroupInfo> listGroupInfo(String userId);
}
