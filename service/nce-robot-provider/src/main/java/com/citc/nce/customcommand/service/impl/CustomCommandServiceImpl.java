package com.citc.nce.customcommand.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.customcommand.dao.CustomCommandMapper;
import com.citc.nce.customcommand.entity.CustomCommand;
import com.citc.nce.customcommand.enums.CustomCommandOperation;
import com.citc.nce.customcommand.enums.CustomCommandState;
import com.citc.nce.customcommand.enums.CustomCommandType;
import com.citc.nce.customcommand.properties.CustomCommandBlockKeywordConfigurationProperties;
import com.citc.nce.customcommand.service.ICustomCommandOperationLogService;
import com.citc.nce.customcommand.service.ICustomCommandService;
import com.citc.nce.customcommand.vo.*;
import com.citc.nce.customcommand.vo.resp.SearchListForMSResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 自定义指令 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomCommandServiceImpl extends ServiceImpl<CustomCommandMapper, CustomCommand> implements ICustomCommandService {
    private final CspCustomerApi cspCustomerApi;
    private final ICustomCommandOperationLogService customCommandOperationLogService;
    private final CustomCommandBlockKeywordConfigurationProperties blockKeywordConfigurationProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CustomCommandAddReq addReq) {
        if (addReq.getType() == CustomCommandType.CUSTOM) {
            String customerId = addReq.getCustomerId();
            Assert.isTrue(
                    customerId != null && customerId.length() == 15 && org.apache.commons.lang3.StringUtils.isNumeric(customerId),
                    "定制指令客户id为空或不正确"
            );
        }

        this.blockKeywordsCheck(addReq.getContent());
        CustomCommand customCommand = new CustomCommand()
                .setUuid(UUID.fastUUID().toString(true))
                .setName(addReq.getName())
                .setDescription(addReq.getDescription())
                .setType(addReq.getType())
                .setCustomerId(addReq.getCustomerId())
                .setContentType(addReq.getContentType())
                .setContent(addReq.getContent())
                .setProduceTime(LocalDateTime.now())
                .setStatus(CustomCommandState.UN_PUBLISH);
        this.save(customCommand);
        customCommandOperationLogService.operationRecord(customCommand.getId(), customCommand.getUuid(),
                CustomCommandOperation.ADD);
    }

    @Override
    public PageResult<CustomCommandSimpleVo> searchCommand(CustomCommandSearchReq searchReq) {
        Page<CustomCommandSimpleVo> page = new Page<>(searchReq.getPageNo(), searchReq.getPageSize());
        page.setOrders(OrderItem.descs("produce_time"));
        baseMapper.searchCommand(page);
        this.fillCustomerInfo(page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public PageResult<CustomCommandSimpleVo> searchPublishCommand(CustomCommandSearchReq searchReq) {
        Page<CustomCommandSimpleVo> page = new Page<>(searchReq.getPageNo(), searchReq.getPageSize());
        page.setOrders(OrderItem.descs("produce_time"));
        baseMapper.searchPublishCommand(searchReq.getType(), page);
        List<CustomCommandSimpleVo> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records) && searchReq.getActive() != null) {
            records.removeIf(record -> !Objects.equals(searchReq.getActive(), record.getActive()));
        }
        return new PageResult<>(records, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long id) {
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        if (command.getStatus() == CustomCommandState.PUBLISH)
            throw new BizException(500, "不能发布已发布指令");
        command.setStatus(CustomCommandState.PUBLISH);
        CustomCommand historyCommand = this.lambdaQuery().eq(CustomCommand::getUuid, command.getUuid()).isNotNull(CustomCommand::getActive).orderByDesc(CustomCommand::getCreateTime).last("limit 1").one();
        if(historyCommand != null){//指令仅在第一次发布时，默认为开启状态，后续发布不应该影响其状态
            command.setActive(historyCommand.getActive());
        }else{
            command.setActive(true);
        }
        this.updateById(command);
        customCommandOperationLogService.operationRecord(command.getId(), command.getUuid(),
                CustomCommandOperation.PUBLISH);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(CustomCommandEditReq editReq) {
        Long id = editReq.getId();
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        if (editReq.getType() == CustomCommandType.CUSTOM) {
            String customerId = editReq.getCustomerId();
            Assert.isTrue(
                    customerId != null && customerId.length() == 15 && org.apache.commons.lang3.StringUtils.isNumeric(customerId),
                    "定制指令客户id为空或不正确"
            );
        }
        this.blockKeywordsCheck(editReq.getContent());
        switch (command.getStatus()) {
            case UN_PUBLISH:
            case EDIT_PUBLISH: {
                BeanUtils.copyProperties(editReq, command);
                this.updateById(command);
                break;
            }
            case PUBLISH: {
                CustomCommand customCommand = new CustomCommand();
                BeanUtils.copyProperties(editReq, customCommand);
                customCommand.setId(null);
                customCommand.setUuid(command.getUuid());
                customCommand.setStatus(CustomCommandState.EDIT_PUBLISH);
                customCommand.setProduceTime(command.getProduceTime());
                customCommand.setCreateTime(LocalDateTime.now());
                customCommand.setCreator(command.getCreator());
                this.save(customCommand);
                break;
            }
        }
        customCommandOperationLogService.operationRecord(id, command.getUuid(), CustomCommandOperation.EDIT);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        if (command.getStatus() != CustomCommandState.UN_PUBLISH)
            throw new BizException(500, "不能删除已发布指令");
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        CustomCommand editPublishCommand = this.getById(id);
        if (editPublishCommand == null)
            throw new BizException(500, "指令不存在");
        if (editPublishCommand.getStatus() != CustomCommandState.EDIT_PUBLISH)
            throw new BizException(500, "只能还原编辑已发布指令");
        CustomCommand publishCommand = this.lambdaQuery()
                .eq(CustomCommand::getUuid, editPublishCommand.getUuid())
                .eq(CustomCommand::getStatus, CustomCommandState.PUBLISH)
                .orderByDesc(CustomCommand::getCreateTime)
                .last("limit 1")
                .one();
        if (publishCommand == null)
            throw new BizException(500, "业务异常，未能查询到该指令的已发布指令,uuid:" + editPublishCommand.getUuid());
        this.blockKeywordsCheck(publishCommand.getContent());
        //插入最新版本
        editPublishCommand
                .setName(publishCommand.getName())
                .setUuid(publishCommand.getUuid())
                .setDescription(publishCommand.getDescription())
                .setType(publishCommand.getType())
                .setCustomerId(publishCommand.getCustomerId())
                .setContentType(publishCommand.getContentType())
                .setContent(publishCommand.getContent())
                .setStatus(CustomCommandState.PUBLISH)
                .setActive(true);
        this.updateById(editPublishCommand);
        customCommandOperationLogService.operationRecord(editPublishCommand.getId(), editPublishCommand.getUuid(),
                CustomCommandOperation.RESTORE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void active(Long id, Boolean active) {
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        if (command.getStatus() != CustomCommandState.PUBLISH)
            throw new BizException(500, "只能激活或禁用已发布指令");
        CustomCommand publishCommand = this.lambdaQuery()
                .eq(CustomCommand::getUuid, command.getUuid())
                .eq(CustomCommand::getStatus, CustomCommandState.PUBLISH)
                .orderByDesc(CustomCommand::getCreateTime)
                .last("limit 1")
                .one();
        //查询该指令的已发布指令的ID，如果没有已发布或者已发布的ID和请求不一致则不能修改
        if (publishCommand == null || !Objects.equals(id, publishCommand.getId()))
            throw new BizException(500, "指令ID非已发布ID，请刷新界面后重试");
        publishCommand.setActive(active);
        this.updateById(publishCommand);
        customCommandOperationLogService.operationRecord(command.getId(), command.getUuid(), active ?
                CustomCommandOperation.ACTIVE : CustomCommandOperation.DISABLE);
    }

    @Override
    public CustomCommandDetailVo getDetail(Long id) {
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        CustomCommandDetailVo detailVo = new CustomCommandDetailVo();
        BeanUtils.copyProperties(command, detailVo);
        return detailVo;
    }

    @Override
    public PageResult<MyAvailableCustomCommandVo> getMyAvailableCommand(MyAvailableCustomCommandReq req) {
        String customerId = SessionContextUtil.getUser().getUserId();
        Page<MyAvailableCustomCommandVo> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setOrders(OrderItem.descs("produce_time"));
        baseMapper.getMyAvailableCommand(customerId, req.getNeedContent(), page);
        page.getRecords().forEach(s -> s.setCustomerId(customerId));
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public Long countMyCommand() {
        return lambdaQuery().eq(CustomCommand::getCustomerId, SessionContextUtil.getUser().getUserId()).eq(CustomCommand::getActive, 1).count();
    }

    @Override
    public PageResult<MyAvailableCustomCommandVo> getMyCommand(MyAvailableCustomCommandReq req) {
        String customerId = SessionContextUtil.getUser().getUserId();
        Page<MyAvailableCustomCommandVo> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setOrders(OrderItem.descs("produce_time"));
        baseMapper.getMyCommand(customerId, req.getNeedContent(), page);
        page.getRecords().forEach(s -> s.setCustomerId(customerId));
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public CustomCommandDetailVo getDetailByUuid(String uuid) {
        CustomCommand command = this.lambdaQuery()
                .eq(CustomCommand::getUuid, uuid)
                .isNull(CustomCommand::getCustomerId)
                .eq(CustomCommand::getStatus, CustomCommandState.PUBLISH)
                .orderByDesc(CustomCommand::getCreateTime)
                .last("limit 1")
                .one();
        if (command == null)
            throw new BizException(500, "指令不存在");
        CustomCommandDetailVo detailVo = new CustomCommandDetailVo();
        BeanUtils.copyProperties(command, detailVo);
        return detailVo;
    }

    @Override
    public void useCommand(CustomCommandDetailVo commandDetailVo) {
        CustomCommand command = new CustomCommand();
        BeanUtils.copyProperties(commandDetailVo, command);
        String userId = SessionContextUtil.getUserId();
        command.setId(null);
        command.setCustomerId(userId);
        command.setCreator(null);
        command.setCreateTime(null);
        command.setUpdater(null);
        command.setUpdateTime(null);
        save(command);
    }

    @Override
    public SearchListForMSResp searchListForMS(SearchListForMSReq req) {
        SearchListForMSResp searchListForMSResp = new SearchListForMSResp();

        List<CustomCommand> customCommandResult = new ArrayList<>();
        List<CustomCommand> queryResult = baseMapper.searchListForMS();
        if (!CollectionUtils.isEmpty(queryResult)) {
            customCommandResult.addAll(queryResult);
        }
        if (req.getId() != null) {
            CustomCommand customCommand = this.getById(req.getId());
            if (customCommand != null) {
                customCommandResult.add(customCommand);
            }
        }

        if (!CollectionUtils.isEmpty(customCommandResult)) {
            List<CustomCommandItem> customCommandItems = new ArrayList<>();
            for (CustomCommand item : customCommandResult) {
                if (item.getMssId() == null || Objects.equals(item.getId(), req.getId())) {
                    CustomCommandItem customCommandItem = new CustomCommandItem();
                    BeanUtils.copyProperties(item, customCommandItem);
                    customCommandItems.add(customCommandItem);
                }
            }
            searchListForMSResp.setCustomCommandItems(customCommandItems);
        }
        return searchListForMSResp;
    }

    @Override
    public CustomCommandDetailVo getByUuid(String uuid) {
        List<CustomCommand> commands = this.lambdaQuery()
                .eq(CustomCommand::getUuid, uuid)
                .eq(CustomCommand::getStatus, CustomCommandState.PUBLISH)
                .orderByDesc(CustomCommand::getCreateTime)
                .list();
        if (CollectionUtils.isEmpty(commands))
            throw new BizException(500, "指令不存在");
        CustomCommandDetailVo detailVo = new CustomCommandDetailVo();
        BeanUtils.copyProperties(commands.get(0), detailVo);
        return detailVo;
    }

    @Override
    public void updateMssID(String id, Long mssId) {
        this.lambdaUpdate().eq(CustomCommand::getMssId, mssId).set(CustomCommand::getMssId, null).update();
        CustomCommand command = this.getById(id);
        if (command != null) {
            command.setMssId(mssId);
            this.updateById(command);
        } else {
            throw new BizException(500, "指令不存在");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMssIDForIds(List<Long> mssIDList) {
        if (!CollectionUtils.isEmpty(mssIDList)) {
            baseMapper.updateMssIDNullById(mssIDList);
        }
    }

    @Override
    public CustomCommandDetailVo getByMsId(String id) {
        CustomCommand command = this.getById(id);
        if (command == null)
            throw new BizException(500, "指令不存在");
        CustomCommandDetailVo detailVo = new CustomCommandDetailVo();
        BeanUtils.copyProperties(command, detailVo);
        return detailVo;
    }

    /**
     * 填充用户信息
     */
    private void fillCustomerInfo(Page<CustomCommandSimpleVo> page) {
        List<String> customerIds = new ArrayList<>();
        List<CustomCommandSimpleVo> records = page.getRecords();
        for (CustomCommandSimpleVo record : records) {
            if (record.getCustomerId() != null) {
                customerIds.add(record.getCustomerId());
            }
        }
        if (CollectionUtils.isEmpty(customerIds))
            return;
        List<UserInfoVo> customerBaseVoList = cspCustomerApi.getByCustomerIds(customerIds);
        page.getRecords().forEach(
                customCommandSimpleVo ->
                        customerBaseVoList.stream()
                                .filter(baseVo -> baseVo.getCustomerId().equals(customCommandSimpleVo.getCustomerId()))
                                .findFirst()
                                .map(UserInfoVo::getName)
                                .ifPresent(customCommandSimpleVo::setCustomerName)
        );
    }

    private void blockKeywordsCheck(String str) {
        if (!StringUtils.hasLength(str)) return;
        //全部转换为消息，与输入的黑名单关键字判断
        String s = new String(str.getBytes()).toLowerCase();
        List<String> blockKeywords = blockKeywordConfigurationProperties.getBlockKeywords();
        if (!CollectionUtils.isEmpty(blockKeywords)) {
            for (String blockKeyword : blockKeywords) {
                if (s.contains(blockKeyword)) {
                    log.warn("自定义指令内容包含屏蔽关键词:{},指令内容:{}", blockKeyword, str);
                    throw new BizException("指令内容包含屏蔽关键词，请删除后重试");
                }
            }
        }
    }
}
