package com.citc.nce.customcommand.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.captcha.CaptchaApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.dao.CustomCommandRequirementMapper;
import com.citc.nce.customcommand.entity.CustomCommandRequirement;
import com.citc.nce.customcommand.enums.CustomCommandRequirementOperation;
import com.citc.nce.customcommand.enums.CustomCommandRequirementState;
import com.citc.nce.customcommand.service.ICustomCommandRequirementOperationLogService;
import com.citc.nce.customcommand.service.ICustomCommandRequirementService;
import com.citc.nce.customcommand.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 定制需求管理(自定义指令) 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomCommandRequirementServiceImpl extends ServiceImpl<CustomCommandRequirementMapper, CustomCommandRequirement> implements ICustomCommandRequirementService {
    private final ICustomCommandRequirementOperationLogService requirementOperationLogService;
    private final CaptchaApi captchaApi;
    private final CspCustomerApi customerApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CustomCommandRequirementAddReq addReq) {
        BaseUser user = SessionContextUtil.getUser();
        CustomCommandRequirement customCommandRequirement = new CustomCommandRequirement()
                .setCustomerId(user.getUserId())
                .setDescription(addReq.getDescription())
                .setContactName(addReq.getContactName())
                .setContactPhone(addReq.getContactPhone())
                .setStatus(CustomCommandRequirementState.WAIT);
        this.save(customCommandRequirement);
    }

    @Override
    public Long countWaitRequirement() {
        return lambdaQuery()
                .eq(CustomCommandRequirement::getStatus, CustomCommandRequirementState.WAIT)
                .count();
    }

    @Override
    public PageResult<CustomCommandRequirementSimpleVo> searchCustomCommandRequirement(CustomCommandRequirementSearchReq searchReq) {
        Page<CustomCommandRequirement> page = Page.of(searchReq.getPageNo(), searchReq.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        lambdaQuery()
                .select(CustomCommandRequirement::getId,
                        CustomCommandRequirement::getContactName,
                        CustomCommandRequirement::getContactPhone,
                        CustomCommandRequirement::getCloseReason,
                        CustomCommandRequirement::getStatus,
                        CustomCommandRequirement::getCreateTime
                )
                .eq(searchReq.getState() != null, CustomCommandRequirement::getStatus, searchReq.getState())
                .and(StringUtils.hasText(searchReq.getContactNameOrContactPhone()),
                        qw -> qw.like(CustomCommandRequirement::getContactPhone, searchReq.getContactNameOrContactPhone())
                                .or()
                                .like(CustomCommandRequirement::getContactName, searchReq.getContactNameOrContactPhone())
                )
                .page(page);
        List<CustomCommandRequirementSimpleVo> simpleVos = page.getRecords()
                .stream()
                .map(requirement -> {
                    CustomCommandRequirementSimpleVo simpleVo = new CustomCommandRequirementSimpleVo();
                    BeanUtils.copyProperties(requirement, simpleVo);
                    return simpleVo;
                }).collect(Collectors.toList());
        return new PageResult<>(simpleVos, page.getTotal());
    }

    @Override
    public CustomCommandRequirementDetailVo getRequirementDetail(Long requirementId) {
        CustomCommandRequirement requirement = this.getById(requirementId);
        CustomCommandRequirementDetailVo detailVo = new CustomCommandRequirementDetailVo();
        if (requirement != null) {
            BeanUtils.copyProperties(requirement, detailVo);
            if (Objects.nonNull(requirement.getCustomerId())) {
                List<UserInfoVo> voList = customerApi.getByCustomerIds(Collections.singletonList(requirement.getCustomerId()));
                if (!CollectionUtils.isEmpty(voList)) {
                    UserInfoVo userInfoVo = voList.get(0);
                    detailVo.setName(userInfoVo.getName());
                }
            }
        }
        return detailVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRequirementNote(Long id, String note) {
        boolean updated = lambdaUpdate()
                .eq(CustomCommandRequirement::getId, id)
                .set(CustomCommandRequirement::getNote, note)
                .update(new CustomCommandRequirement());
        if (!updated)
            throw new BizException(500, "更新沟通记录失败");
        requirementOperationLogService.operationRecord(id, CustomCommandRequirementOperation.NOTE, note);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRequirement(CustomCommandRequirementProcessReq processReq) {
        CustomCommandRequirementState state = processReq.getStatus();
        if (state == CustomCommandRequirementState.WAIT)
            throw new BizException(500, "非法状态");
        boolean updated = this.lambdaUpdate()
                .eq(CustomCommandRequirement::getId, processReq.getId())
                .set(CustomCommandRequirement::getStatus, state)
                .set(StringUtils.hasText(processReq.getCloseReason()) && state == CustomCommandRequirementState.CLOSE,
                        CustomCommandRequirement::getCloseReason, processReq.getCloseReason())
                .update(new CustomCommandRequirement());
        if (!updated)
            throw new BizException(500, "处理失败");
        requirementOperationLogService.operationRecord(
                processReq.getId(),
                state == CustomCommandRequirementState.PROCESSED ? CustomCommandRequirementOperation.PROCESS : CustomCommandRequirementOperation.CLOSE,
                processReq.getCloseReason()
        );
    }

    @Override
    public PageResult<CustomCommandRequirementSimpleVo> getMyRequirements(PageParam pageParam) {
        Page<CustomCommandRequirement> page = Page.of(pageParam.getPageNo(), pageParam.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        lambdaQuery()
                .select(CustomCommandRequirement::getId,
                        CustomCommandRequirement::getContactName,
                        CustomCommandRequirement::getContactPhone,
                        CustomCommandRequirement::getStatus,
                        CustomCommandRequirement::getCreateTime,
                        CustomCommandRequirement::getCloseReason
                )
                .eq(CustomCommandRequirement::getCustomerId, SessionContextUtil.getUser().getUserId())
                .page(page);
        List<CustomCommandRequirementSimpleVo> simpleVos = page.getRecords()
                .stream()
                .map(requirement -> {
                    CustomCommandRequirementSimpleVo simpleVo = new CustomCommandRequirementSimpleVo();
                    BeanUtils.copyProperties(requirement, simpleVo);
                    return simpleVo;
                }).collect(Collectors.toList());
        return new PageResult<>(simpleVos, page.getTotal());
    }


}
