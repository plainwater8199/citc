package com.citc.nce.materialSquare.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.AdminAuthApi;
import com.citc.nce.authcenter.auth.vo.resp.AdminUserResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.filecenter.FileApi;
import com.citc.nce.materialSquare.dao.MsManageSuggestMapper;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.dao.MsManageActivityMapper;
import com.citc.nce.materialSquare.entity.MsManageSuggest;
import com.citc.nce.materialSquare.service.IMsManageActivityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.materialSquare.service.IMsManageBannerService;
import com.citc.nce.materialSquare.vo.activity.ActivityAdd;
import com.citc.nce.materialSquare.vo.activity.ActivityDesign;
import com.citc.nce.materialSquare.vo.activity.ActivityEdit;
import com.citc.nce.materialSquare.vo.activity.ActivityPageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场_后台管理_活动封面	 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Service
public class MsManageActivityServiceImpl extends ServiceImpl<MsManageActivityMapper, MsManageActivity> implements IMsManageActivityService {

    @Autowired
    private IMsManageBannerService bannerService;
    @Autowired
    private MsManageSuggestMapper suggestMapper;
    @Autowired
    private AdminAuthApi authApi;
    @Autowired
    private FileApi fileApi;

    @Override
    public void addActivity(ActivityAdd activityAdd) {
        checkName(null,activityAdd.getName());
        MsManageActivity activity = new MsManageActivity();
        BeanUtil.copyProperties(activityAdd, activity);
        save(activity);
    }

    private void checkName(Long msActivityId, String name) {
        if (exists(new LambdaQueryWrapper<MsManageActivity>()
                .eq(MsManageActivity::getName, name)
                .ne(Objects.nonNull(msActivityId), MsManageActivity::getMsActivityId, msActivityId))) {
            throw new BizException("活动名称不能重复");
        }
    }

    @Override
    @Transactional
    public void deleteById(Long msActivityId) {
        MsManageActivity activity = getById(msActivityId);
        if (Objects.isNull(activity)) {
            return;
        }
        bannerService.removeMsActivityId(msActivityId);
        removeById(msActivityId);
    }


    @Override
    public PageResult<MsManageActivity> pageResult(ActivityPageQuery pageQuery) {
        Page<MsManageActivity> page = new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize());
        page(page, new LambdaQueryWrapper<MsManageActivity>()
                .like(StringUtils.hasLength(pageQuery.getName()), MsManageActivity::getName, pageQuery.getName())
                .orderByDesc(MsManageActivity::getCreateTime)
        );
        //查询是否在使用
        List<MsManageActivity> records = page.getRecords();
        Map<Long,Boolean> isUsingMap = queryIsUsing(records.stream().map(MsManageActivity::getMsActivityId).collect(Collectors.toList()));
        //查询操作人名称
        List<String> userIds = records.stream().map(MsManageActivity::getCreator).distinct().collect(Collectors.toList());
        List<String> fileUrls = records.stream().map(MsManageActivity::getCoverFileId).distinct().collect(Collectors.toList());
        Map<String, String> autoThumbnail = fileApi.getAutoThumbnail(fileUrls);

        Map<String, String> map = authApi.getAdminUserByUserId(userIds).stream()
                .collect(Collectors.toMap(AdminUserResp::getUserId, AdminUserResp::getFullName));
        for (MsManageActivity resp : records) {
            resp.setCreatorName(map.get(resp.getCreator()));
            resp.setOccupant(isUsingMap.getOrDefault(resp.getMsActivityId(),false));
            resp.setAutoThumbnail(autoThumbnail.get(resp.getCoverFileId()));
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    private Map<Long, Boolean> queryIsUsing(List<Long> activityIds) {
        if (!CollectionUtils.isEmpty(activityIds)) {
            Map<Long, Boolean> result = new HashMap<>();
            //默认为未使用
            for(Long activityId : activityIds) {
                result.put(activityId,false);
            }
            //查询是否在Banner使用
            List<MsManageBanner> banners = bannerService.listByMsActivityIds(activityIds);
            if (!CollectionUtils.isEmpty(banners)) {
                for (MsManageBanner banner : banners) {
                    result.put(banner.getMsActivityId(), true);
                }
            }
            //查询是否在建议使用
            List<MsManageSuggest> suggests = suggestMapper.selectList(new LambdaQueryWrapper<MsManageSuggest>().in(MsManageSuggest::getMssId, activityIds).eq(MsManageSuggest::getDeleted, 0));
            if (!CollectionUtils.isEmpty(suggests)) {
                for (MsManageSuggest banner : suggests) {
                    result.put(banner.getMssId(), true);
                }
            }
            return result;
        }else{
            return Collections.emptyMap();
        }
    }

    @Override
    public void editActivity(ActivityEdit activityEdit) {
        MsManageActivity activity = getOne( new LambdaQueryWrapper<MsManageActivity>().eq(MsManageActivity::getMsActivityId, activityEdit.getMsActivityId()));
        if (!Objects.isNull(activity)) {
            checkName(activity.getMsActivityId(),activityEdit.getName());
            activity.setName(activityEdit.getName());
            activity.setCoverFileId(activityEdit.getCoverFileId());
            activity.setCoverFormat(activityEdit.getCoverFormat());
            activity.setCoverName(activityEdit.getCoverName());
            activity.setCoverLength(activityEdit.getCoverLength());
            updateById(activity);
        }else{
            throw new BizException("活动不存在！");
        }
    }

    @Override
    public void designActivity(ActivityDesign activityDesign) {
        MsManageActivity activity = getOne( new LambdaQueryWrapper<MsManageActivity>().eq(MsManageActivity::getMsActivityId, activityDesign.getMsActivityId()));
        if (!Objects.isNull(activity)) {
            activity.setH5Id(activityDesign.getH5Id());
            updateById(activity);
        }else{
            throw new BizException("活动不存在！");
        }

    }

    @Override
    public void updateActivityH5Info(Long msActivityId, Long h5Id) {
        MsManageActivity activity = getById(msActivityId);
        if (!Objects.isNull(activity)) {
            activity.setH5Id(h5Id);
            updateById(activity);
        }else{
            throw new BizException("活动不存在！");
        }
    }

    @Override
    public Map<Long, MsManageActivity> queryInSuggestInfo(List<Long> activityIds) {
        List<MsManageActivity> activities = this.lambdaQuery().in(MsManageActivity::getMsActivityId, activityIds).list();
        if(!CollectionUtils.isEmpty(activities)){
            return activities.stream().collect(Collectors.toMap(MsManageActivity::getMsActivityId, a -> a));
        }
        return Collections.emptyMap();
    }

}
