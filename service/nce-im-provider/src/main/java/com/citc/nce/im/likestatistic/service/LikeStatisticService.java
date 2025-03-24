package com.citc.nce.im.likestatistic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.likestatistic.dao.LikeStatisticMapper;
import com.citc.nce.im.likestatistic.entity.LikeStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author jcrenc
 * @since 2024/5/31 10:38
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LikeStatisticService extends ServiceImpl<LikeStatisticMapper, LikeStatistic> implements IService<LikeStatistic> {

    @Transactional(rollbackFor = Exception.class)
    public void like(Long likedSubjectId) {
        LikeStatistic statistic = this.lambdaQuery()
                .eq(LikeStatistic::getLikedId, likedSubjectId)
                .one();
        if (statistic == null) {
            statistic = new LikeStatistic();
            statistic.setLikedId(likedSubjectId);
            statistic.setLikedNumber(0L);
            save(statistic);
        }
    }

}
