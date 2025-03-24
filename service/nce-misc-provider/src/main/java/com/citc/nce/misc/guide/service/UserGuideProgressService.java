package com.citc.nce.misc.guide.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.guide.entity.SysGuide;
import com.citc.nce.misc.guide.entity.UserGuideProgress;
import com.citc.nce.misc.guide.mapper.SysGuideMapper;
import com.citc.nce.misc.guide.mapper.UserGuideProgressMapper;
import com.citc.nce.misc.guide.req.UserGuideProgressUpdateReq;
import com.citc.nce.misc.guide.resp.UserGuideProgressResp;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户引导进度服务
 *
 * @author jcrenc
 * @since 2024/11/6 17:08
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class UserGuideProgressService extends ServiceImpl<UserGuideProgressMapper, UserGuideProgress> implements IService<UserGuideProgress> {
    private final SysGuideMapper sysGuideMapper;

    /**
     * 更新引导进度
     */
    public void updateProgress(UserGuideProgressUpdateReq updateReq) {
        SysGuide sysGuide = sysGuideMapper.selectOne(new LambdaQueryWrapper<SysGuide>().eq(SysGuide::getName, updateReq.getGuideName()));
        assert sysGuide != null;
        Long guideId = sysGuide.getId();
        String userId = SessionContextUtil.getUserId();
        UserGuideProgress progress = this.baseMapper.selectOne(
                new LambdaQueryWrapper<UserGuideProgress>()
                        .eq(UserGuideProgress::getGuideId, guideId)
                        .eq(UserGuideProgress::getCreator, userId)
        );
        if (progress == null) {
            progress = new UserGuideProgress();
            progress.setGuideId(guideId);
            progress.setCustomerId(userId);
            save(progress);
        }
        //兼容负数，step为负数时直接完成
        int currentStep = updateReq.getCurrentStep() < 0 ? sysGuide.getTotalSteps() : Math.min(updateReq.getCurrentStep(), sysGuide.getTotalSteps());
        progress.setCurrentStep(currentStep);
        if (currentStep == sysGuide.getTotalSteps() && !Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }
        updateById(progress);
    }

    /**
     * 获取用户所有引导进度
     */
    public UserGuideProgressResp getProgress() {
        String userId = SessionContextUtil.getUserId();
        Map<Long, SysGuide> guideMap = sysGuideMapper.selectList(null).stream()
                .collect(Collectors.toMap(SysGuide::getId, Function.identity()));

        List<UserGuideProgress> userGuideProgresses = this.baseMapper.selectList(
                new LambdaQueryWrapper<UserGuideProgress>()
                        .eq(UserGuideProgress::getCreator, userId)
        );
        HashMap<String, UserGuideProgressResp.Progress> progresses = new HashMap<>();
        for (UserGuideProgress progress : userGuideProgresses) {
            SysGuide sysGuide = guideMap.get(progress.getGuideId());
            progresses.put(sysGuide.getName(), new UserGuideProgressResp.Progress(sysGuide.getTotalSteps(), progress.getCurrentStep(), progress.getCompleted(), progress.getCompletedAt()));
        }
        return new UserGuideProgressResp(progresses);
    }
}
