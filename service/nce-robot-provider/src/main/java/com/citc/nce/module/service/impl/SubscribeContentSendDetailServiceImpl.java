package com.citc.nce.module.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.dao.SubscribeContentSendDetailDao;
import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.dto.SubscribeContentSendDetailDto;
import com.citc.nce.module.dto.SubscribeSendInfoForPhoneItemDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.entity.SubscribeContentSendDetailDo;
import com.citc.nce.module.service.SubscribeContentSendDetailService;
import com.citc.nce.robot.util.DateUtil;
import com.citc.nce.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("subscribeContentSendDetailService")
public class SubscribeContentSendDetailServiceImpl implements SubscribeContentSendDetailService {

    @Resource
    private SubscribeContentSendDetailDao subscribeContentSendDetailDao;

    @Override
    public void saveSubscribeSendRecord(List<String> phones, SendContentForSubscribeDto sendContentForSubscribeInfo) {
        if(!CollectionUtils.isEmpty(phones)){
            List<SubscribeContentSendDetailDo> insertRecords = new ArrayList<>();
            SubscribeContentSendDetailDo subscribeContentSendDetailDo;
            for(String phone : phones){
                subscribeContentSendDetailDo = new SubscribeContentSendDetailDo();
                subscribeContentSendDetailDo.setSubscribeId(sendContentForSubscribeInfo.getSubscribeId());
                subscribeContentSendDetailDo.setSubContentId(sendContentForSubscribeInfo.getSubContentId());
                subscribeContentSendDetailDo.setSubscribeSendTime(new Date());
                subscribeContentSendDetailDo.setPhone(phone);
                subscribeContentSendDetailDo.setTitle(sendContentForSubscribeInfo.getTitle());
                subscribeContentSendDetailDo.setMsg5gId(sendContentForSubscribeInfo.getMsg5GId());
                subscribeContentSendDetailDo.setStatus(1);//已发送
                subscribeContentSendDetailDo.setCreator("system");
                subscribeContentSendDetailDo.setCreateTime(new Date());
                insertRecords.add(subscribeContentSendDetailDo);
            }
            subscribeContentSendDetailDao.insertBatch(insertRecords);
        }
    }

    @Override
    public String getRecentAdvances(String phone, String subscribeId) {
        List<SubscribeContentSendDetailDo> subscribeContentSendDetailDos = getSubscribeSendInfo(phone,subscribeId);
        if(!CollectionUtils.isEmpty(subscribeContentSendDetailDos)){
            SubscribeContentSendDetailDo subscribeContentSendDetailDo = subscribeContentSendDetailDos.get(0);
            return "【"+subscribeContentSendDetailDo.getTitle()+"】";
        }
        return "";
    }

    @Override
    public List<SubscribeSendInfoForPhoneItemDto> getSubscribeSendInfoForPhone(String phone, String subscribeId) {
        List<SubscribeContentSendDetailDo> subscribeContentSendDetailDos = getSubscribeSendInfo(phone,subscribeId);
        if(!CollectionUtils.isEmpty(subscribeContentSendDetailDos)){
            List<SubscribeSendInfoForPhoneItemDto> subscribeSendInfoForPhoneItemDtos = new ArrayList<>();
            SubscribeSendInfoForPhoneItemDto subscribeSendInfoForPhoneItemDto;
            for(SubscribeContentSendDetailDo item : subscribeContentSendDetailDos){
                subscribeSendInfoForPhoneItemDto = new SubscribeSendInfoForPhoneItemDto();
                subscribeSendInfoForPhoneItemDto.setMsg5GId(item.getMsg5gId());
                subscribeSendInfoForPhoneItemDto.setTitle(item.getTitle());
                subscribeSendInfoForPhoneItemDto.setSendTime(item.getCreateTime());
                subscribeSendInfoForPhoneItemDto.setSubContentId(item.getSubContentId());
                subscribeSendInfoForPhoneItemDtos.add(subscribeSendInfoForPhoneItemDto);
            }
            return subscribeSendInfoForPhoneItemDtos;
        }
        return Collections.emptyList();
    }


    private List<SubscribeContentSendDetailDo> getSubscribeSendInfo(String phone, String subscribeId){
        if(StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(subscribeId)){
            LambdaQueryWrapper<SubscribeContentSendDetailDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SubscribeContentSendDetailDo::getSubscribeId, subscribeId);
            queryWrapper.eq(SubscribeContentSendDetailDo::getPhone,phone);
            queryWrapper.isNotNull(SubscribeContentSendDetailDo::getSubContentId);
            queryWrapper.orderByAsc(SubscribeContentSendDetailDo::getSubscribeSendTime);
            return subscribeContentSendDetailDao.selectList(queryWrapper);
        }
        return Collections.emptyList();
    }

}
