package com.citc.nce.auth.contactgroup.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.contactgroup.dao.ContactGroupDao;
import com.citc.nce.auth.contactgroup.entity.ContactGroupDo;
import com.citc.nce.auth.contactgroup.exception.ContactGroupCode;
import com.citc.nce.auth.contactgroup.service.ContactGroupContService;
import com.citc.nce.auth.contactgroup.vo.*;
import com.citc.nce.auth.contactlist.dao.ContactListDao;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.contactlist.entity.ContactListDo;
import com.citc.nce.auth.contactlist.service.ContactListService;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.constants.CarrierEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.PhoneFilterUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.api.MassSegmentApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author: yangchuang
 * @Date: 2022/8/11 15:28
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class ContactGroupContServiceImpl implements ContactGroupContService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    @Resource
    private ContactGroupDao contactGroupDao;

    @Resource
    private ContactListDao contactListDao;
    @Resource
    private MassSegmentApi massSegmentApi;

    @Resource
    private ContactListService contactListService;


    @Override
    public PageResultResp getContactGroups(PageParam pageParam) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        List<Map<String, Long>> maps = null;
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
            maps = contactListDao.selectCountByGroupId(userId);
        } else {
            maps = contactListDao.selectCountByGroupIdAdmin();
        }
        wrapper.orderByDesc("create_time");
        PageResult<ContactGroupDo> contactGroupDoPageResult = contactGroupDao.selectPage(pageParam, wrapper);

        List<ContactGroupResp> contactGroupResps = BeanUtil.copyToList(contactGroupDoPageResult.getList(), ContactGroupResp.class);
        HashMap<Long, Long> vMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Long> map : maps) {
                Long groupId = map.get("groupId");
                Long nums = map.get("nums");
                vMap.put(groupId, nums);
            }
        }
        for (ContactGroupResp contactGroupResp : contactGroupResps) {
            if (vMap.get(contactGroupResp.getId()) == null) {
                contactGroupResp.setPersonNum(0L);
            } else {
                contactGroupResp.setPersonNum(vMap.get(contactGroupResp.getId()));
            }
        }
        return new PageResultResp(contactGroupResps, contactGroupDoPageResult.getTotal(), pageParam.getPageNo());

    }

    @Override
    public int saveContactGroup(ContactGroupReq contactGroupReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("group_name", contactGroupReq.getGroupName());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<ContactGroupDo> contactGroupDos = contactGroupDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(contactGroupDos)) {
            throw new BizException(ContactGroupCode.VARIABLE_BAD_REQUEST);
        }
        ContactGroupDo contactGroupDo = new ContactGroupDo();
        BeanUtil.copyProperties(contactGroupReq, contactGroupDo);
        return contactGroupDao.insert(contactGroupDo);
    }

    @Override
    public int updateContactGroup(ContactGroupEditReq contactGroupEditReq) {
        ContactGroupDo contact = contactGroupDao.selectById(contactGroupEditReq.getId());
        if (Objects.isNull(contact)) {
            throw new BizException("联系人组不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(contact.getCreator())) {
            throw new BizException("不能修改别人的联系人组");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("group_name", contactGroupEditReq.getGroupName());
        wrapper.ne("id", contactGroupEditReq.getId());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<ContactGroupDo> contactGroupDos = contactGroupDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(contactGroupDos)) {
            for (ContactGroupDo contactGroupDo : contactGroupDos) {
                if (!contactGroupDo.getId().equals(contactGroupEditReq.getId())) {
                    throw new BizException(ContactGroupCode.VARIABLE_BAD_REQUEST);
                }
            }
        }
        ContactGroupDo contactGroupDo = new ContactGroupDo();
        BeanUtil.copyProperties(contactGroupEditReq, contactGroupDo);
        return contactGroupDao.updateById(contactGroupDo);
    }

    @Override
    public int delContactGroupById(Long id) {
        ContactGroupDo group = contactGroupDao.selectById(id);
        if (Objects.isNull(group)) return 0;
        if (!SessionContextUtil.getLoginUser().getUserId().equals(group.getCreator())){
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return contactGroupDao.delContactGroupById(map);
    }

    @Override
    public List<ContactGroupTreeResp> getTreeList() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        List<ContactGroupDo> contactGroupDos = contactGroupDao.selectList(wrapper);
        List<ContactGroupTreeResp> contactGroupTreeResps = BeanUtil.copyToList(contactGroupDos, ContactGroupTreeResp.class);
        return contactGroupTreeResps;
    }

    @Override
    public ContactGroupResp getContactGroupById(Long id) {
        ContactGroupDo contactGroupDo = contactGroupDao.selectOne(Wrappers.<ContactGroupDo>lambdaQuery()
                .eq(ContactGroupDo::getDeleted, 0)
                .eq(ContactGroupDo::getId, id));
        if (ObjectUtils.isNotEmpty(contactGroupDo)) {
            ContactGroupResp contactGroupResp = BeanUtil.copyProperties(contactGroupDo, ContactGroupResp.class);
            return contactGroupResp;
        }
        return null;
    }

    @Override
    public ContactGroupSelectedCarriersResp getContactGroupMemberById(ContactGroupSelectedCarriersReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        ContactGroupSelectedCarriersResp result =  new ContactGroupSelectedCarriersResp();
        List<ContactListDo> contactListDos = contactListDao.selectList(Wrappers.<ContactListDo>lambdaQuery()
                .eq(ContactListDo::getDeleted, 0)
                .eq(ContactListDo::getGroupId, req.getContactId()));
        if (CollectionUtil.isEmpty(contactListDos)) {
            return result;
        }

        if (!contactListDos.get(0).getCreator().equals(userId)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        List<String> members = contactListDos.stream().map(ContactListDo::getPhoneNum).collect(Collectors.toList());

        Map<String, String> allSegment = massSegmentApi.queryAllSegment();
        Map<Integer, Integer> integerIntegerMap = PhoneFilterUtil.countPhonesOfOperator(members, allSegment);

        result.setMobileNumber(integerIntegerMap.get(CarrierEnum.CMCC.getValue()));
        result.setTelecomNumber(integerIntegerMap.get(CarrierEnum.Telecom.getValue()));
        result.setUnicomNumber(integerIntegerMap.get(CarrierEnum.Unicom.getValue()));
        return result;
    }

    @Override
    @Transactional
    public Integer importContactForModule(List<String> phoneList, Long groupId, String groupName) {
        if(CollectionUtils.isNotEmpty(phoneList)){
            if(groupId == null){//表示新增联系人组
                //1、查看联系人组是否存在
                List<ContactGroupDo> contactGroupDos = contactGroupDao.selectList(Wrappers.<ContactGroupDo>lambdaQuery()
                        .eq(ContactGroupDo::getDeleted, 0)
                        .eq(ContactGroupDo::getCreator, SessionContextUtil.getUserId())
                        .eq(ContactGroupDo::getGroupName, groupName));
                if(CollectionUtils.isEmpty(contactGroupDos)){
                    //2、创建联系人组
                    ContactGroupDo contactGroupDo = new ContactGroupDo();
                    contactGroupDo.setGroupName(groupName);
                    contactGroupDao.insert(contactGroupDo);
                    return contactListService.importContact(contactGroupDo.getId(),phoneList);

                }else{
                    throw new BizException(ContactGroupCode.VARIABLE_BAD_REQUEST);
                }
            }else{//导入到现有联系人组
                ContactGroupDo contactGroupDo = contactGroupDao.selectById(groupId);
                if(!Objects.isNull(contactGroupDo)){
                    if(SessionContextUtil.getUserId().equals(contactGroupDo.getCreator())){
                        return contactListService.importContact(contactGroupDo.getId(),phoneList);
                    }else{
                        throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
                    }
                }else{
                    throw new BizException(ContactGroupCode.CONTACT_GROUP_NOT_EXIST);
                }
            }
        }
        return 0;
    }
}
