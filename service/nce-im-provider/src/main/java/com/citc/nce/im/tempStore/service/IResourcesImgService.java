package com.citc.nce.im.tempStore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.api.tempStore.bean.images.ImgMove;
import com.citc.nce.robot.api.tempStore.bean.images.ImgPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组管理 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 11:11:28
 */
public interface IResourcesImgService extends IService<ResourcesImg> {

    /**
     * 查询用户的图片
     * @param userId 用户id
     * @param page 分页
     * @param query 查询条件
     */
    void pageName(String userId, Page<ResourcesImg> page, ImgPageQuery query);

    /**
     * 移动图片分类
     * @param move 移动信息
     */
    void moveImgGroup(ImgMove move);

    List<ResourcesImg> listByIdsDel(Collection<Long> ids);
}
