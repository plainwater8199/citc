package com.citc.nce.auth.messageplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.messageplan.dao.VideoSmsPlanMapper;
import com.citc.nce.auth.messageplan.entity.VideoSmsPlan;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.service.IVideoSmsPlanService;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanAddVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanListVo;
import com.citc.nce.auth.messageplan.vo.VideoSmsPlanVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 视频短信套餐表 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:42
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VideoSmsPlanServiceImpl extends ServiceImpl<VideoSmsPlanMapper, VideoSmsPlan> implements IVideoSmsPlanService {
    public static final List<Integer> CHANNELS = Collections.singletonList(0);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPlan(VideoSmsPlanAddVo addVo) {
        if (!CHANNELS.contains(addVo.getChannel()))
            throw new BizException("invalid channel!");
        if (exists(new LambdaQueryWrapper<VideoSmsPlan>().eq(VideoSmsPlan::getName, addVo.getName()))) {
            throw new BizException("套餐名称重复");
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        int priceScale = 5;
        RoundingMode priceRoundingMode = RoundingMode.DOWN;
        VideoSmsPlan smsPlan = new VideoSmsPlan()
                .setName(addVo.getName())
                .setChannel(addVo.getChannel())
                .setPlanId("MP" + df.format(LocalDateTime.now()) + RandomUtils.nextInt(10))
                .setNumber(addVo.getNumber())
                .setPrice(addVo.getPrice().setScale(priceScale, priceRoundingMode).stripTrailingZeros())
                .setStatus(MessagePlanStatus.ON_SHELVES);
        smsPlan.setAmount(smsPlan.getPrice().multiply(BigDecimal.valueOf(smsPlan.getNumber())).stripTrailingZeros().toPlainString());
        this.save(smsPlan);
    }

    @Override
    public Page<VideoSmsPlanListVo> selectPlan(MessagePlanSelectReq selectReq) {
        Page<VideoSmsPlanListVo> page = new Page<>();
        page.setOrders(OrderItem.descs("create_time"));
        MessagePlanStatus status = null;
        if (SessionContextUtil.getUser().getIsCustomer())  //客户只能看到已上架的计划
            status = MessagePlanStatus.ON_SHELVES;
        return baseMapper.selectPlan(selectReq.getPlanId(), SessionContextUtil.getUser().getCspId(),status, page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateState(Long id, Integer status) {
        if (Arrays.stream(MessagePlanStatus.values()).noneMatch(state -> state.getCode().equals(status)))
            throw new BizException("invalid status");
        boolean updated = this.lambdaUpdate()
                .eq(VideoSmsPlan::getId, id)
                .ne(VideoSmsPlan::getStatus, status)
                .eq(VideoSmsPlan::getCreator, SessionContextUtil.getUser().getCspId())
                .set(VideoSmsPlan::getStatus, status)
                .update();
        if (!updated)
            throw new BizException("更新失败");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePlan(Long id) {
        boolean removed = this.remove(
                Wrappers.<VideoSmsPlan>lambdaQuery()
                        .eq(VideoSmsPlan::getId, id)
                        .eq(VideoSmsPlan::getStatus, MessagePlanStatus.OFF_SHELVES)
                        .eq(VideoSmsPlan::getCreator, SessionContextUtil.getUser().getCspId())
        );
        if (!removed)
            throw new BizException("删除失败");
    }

    @Override
    public VideoSmsPlanVo getPlanDetail(Long id) {
        return baseMapper.selectPlanDetail(id, SessionContextUtil.getUser().getCspId());
    }
}
