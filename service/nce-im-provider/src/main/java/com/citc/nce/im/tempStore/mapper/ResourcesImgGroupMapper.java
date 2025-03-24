package com.citc.nce.im.tempStore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.robot.api.tempStore.bean.images.GroupInfo;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImgGroup;

import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 02:11:37
 */
public interface ResourcesImgGroupMapper extends BaseMapper<ResourcesImgGroup> {

    /**
     * 查询用户图片的分组数量
     *
     * @param userId 用户id
     * @return 组id，组数量
     */
    List<GroupInfo>  groupInfo (String userId);
}
