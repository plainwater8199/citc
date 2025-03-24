package com.citc.nce.materialSquare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.entity.MsManageBanner;
import com.citc.nce.materialSquare.dao.MsManageBannerMapper;
import com.citc.nce.materialSquare.service.IMsManageActivityService;
import com.citc.nce.materialSquare.service.IMsManageBannerService;
import com.citc.nce.materialSquare.vo.banner.MsBannerAdd;
import com.citc.nce.materialSquare.vo.banner.MsBannerChangeOrder;
import com.citc.nce.materialSquare.vo.banner.MsBannerUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 素材广场_管理端_banner管理 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-09 10:05:26
 */
@Service
public class MsManageBannerServiceImpl extends ServiceImpl<MsManageBannerMapper, MsManageBanner> implements IMsManageBannerService {


    @Autowired
    private IMsManageActivityService activityService;

    @Override
    public void updateByEntity(MsBannerUpdate update) {
        MsManageBanner banner = getById(update.getMsBannerId());
        if (Objects.isNull(banner)) {
            throw new BizException("banner已被删除不能编辑");
        }
        banner.setName(update.getName());
        banner.setMsActivityId(update.getMsActivityId());
        banner.setImgFileId(update.getImgFileId());
        banner.setImgFormat(update.getImgFormat());
        updateById(banner);
    }

    @Override
    public List<MsManageBanner> listOrderNum() {
        List<MsManageBanner> list = lambdaQuery().orderByAsc(MsManageBanner::getOrderNum).list();
        if (CollectionUtils.isEmpty(list)) return list;
        Set<Long> longs = list.stream().map(MsManageBanner::getMsActivityId).collect(Collectors.toSet());
        Map<Long, MsManageActivity> activityNameMap = activityService.listByIds(longs).stream().collect(Collectors.toMap(MsManageActivity::getMsActivityId, Function.identity()));
        list.forEach(s -> {
            MsManageActivity activity = activityNameMap.get(s.getMsActivityId());
            s.setMsActivityName(activity.getName());
            s.setH5Id(activity.getH5Id());
        });
        return list;
    }


    @Override
    @Transactional
    public void changeOrderNum(List<MsBannerChangeOrder> bannerList) {
        if (CollectionUtils.isEmpty(bannerList)) return;

        List<MsManageBanner> updateList = new ArrayList<>(bannerList.size());
        for (int i = 0; i < bannerList.size(); i++) {
            MsManageBanner update = new MsManageBanner();
            update.setOrderNum(bannerList.get(i).getOrderNum());
            update.setMsBannerId(bannerList.get(i).getMsBannerId());
            updateList.add(update);
        }
        updateBatchById(updateList);
    }

    public List<MsManageBanner> listByMsActivityId(Long msActivityId) {
        return lambdaQuery().eq(MsManageBanner::getMsActivityId, msActivityId).list();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeMsActivityId(Long msActivityId) {
        remove(new LambdaQueryWrapper<MsManageBanner>()
                .eq(MsManageBanner::getMsActivityId, msActivityId));
    }

    @Override
    @Transactional
    public void addBanner(MsBannerAdd save) {
        MsManageBanner banner = new MsManageBanner();
        banner.setName(save.getName());
        banner.setMsActivityId(save.getMsActivityId());
        banner.setImgFileId(save.getImgFileId());
        banner.setImgFormat(save.getImgFormat());
        banner.setOrderNum(0L);
        banner.setImgName(save.getImgName());
        banner.setImgLength(save.getImgLength());
        save(banner);
        getBaseMapper().autoIncrement();
    }

    @Override
    public List<MsManageBanner> listByMsActivityIds(List<Long> activityIds) {
        return lambdaQuery().in(MsManageBanner::getMsActivityId, activityIds).eq(MsManageBanner::getDeleted, 0).list();
    }
}
