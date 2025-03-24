package com.citc.nce.materialSquare.service;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.materialSquare.vo.activity.ActivityAdd;
import com.citc.nce.materialSquare.vo.activity.ActivityDesign;
import com.citc.nce.materialSquare.vo.activity.ActivityEdit;
import com.citc.nce.materialSquare.vo.activity.ActivityPageQuery;
import com.citc.nce.materialSquare.vo.activity.resp.ActivityAddResp;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 素材广场_后台管理_活动封面	 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
public interface IMsManageActivityService extends IService<MsManageActivity> {


    void addActivity(ActivityAdd activityAdd);

    /**
     * 安全删除，。。。。不能被使用
     *
     * @param msActivityId 待删除id
     */
    void deleteById(Long msActivityId);


    PageResult<MsManageActivity> pageResult(ActivityPageQuery pageQuery);

    void editActivity(ActivityEdit activityEdit);

    void designActivity(ActivityDesign activityDesign);

    /**
     * 更新活动的H5信息
     * @param msActivityId 活动ID
     * @param h5Id H5 ID
     */
    void updateActivityH5Info(Long msActivityId, Long h5Id);

    Map<Long, MsManageActivity> queryInSuggestInfo(List<Long> activityIds);
}
