package com.citc.nce.auth.contactlist.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.contactbacklist.dao.ContactBackListDao;
import com.citc.nce.auth.contactbacklist.entity.ContactBackListDo;
import com.citc.nce.auth.contactgroup.dao.ContactGroupDao;
import com.citc.nce.auth.contactgroup.entity.ContactGroupDo;
import com.citc.nce.auth.contactlist.dao.ContactListDao;
import com.citc.nce.auth.contactlist.entity.ContactListDo;
import com.citc.nce.auth.contactlist.exception.ContactListCode;
import com.citc.nce.auth.contactlist.service.ContactListService;
import com.citc.nce.auth.contactlist.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.citc.nce.auth.contactlist.util.ExcelUtils.exportExcel;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class ContactListServiceImpl implements ContactListService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    @Resource
    private ContactListDao contactListDao;
    //黑名单联系人
    @Resource
    private ContactBackListDao contactBackListDao;

    @Resource
    private ContactGroupDao contactGroupDao;

    @Override
    public PageResultResp getContactLists(ContactListPageReq contactListPageReq) {

        ContactGroupDo contact = contactGroupDao.selectById(contactListPageReq.getGroupId());
        if (Objects.isNull(contact)) {
            throw new BizException("联系人组不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(contact.getCreator())) {
            throw new BizException("不能查看别人的联系人组");
        }

        QueryWrapper<ContactListDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (contactListPageReq.getGroupId() != null) {
            wrapper.eq("group_id", contactListPageReq.getGroupId());
        }

        if (StringUtils.isNotEmpty(contactListPageReq.getPersonName())) {
            wrapper.like("person_name", contactListPageReq.getPersonName());
        }

        if (StringUtils.isNotEmpty(contactListPageReq.getPhoneNum())) {
            wrapper.like("phone_num", contactListPageReq.getPhoneNum());
        }

        if (null != contactListPageReq.getUpdateTime()) {
            wrapper.eq("update_time", contactListPageReq.getUpdateTime());
        }
        String userId = "";
        if (StringUtils.isNotEmpty(contactListPageReq.getUserId())) {
            userId = contactListPageReq.getUserId();
        } else {
            userId = SessionContextUtil.getUser().getUserId();
        }
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("id");
        PageResult<ContactListDo> contactListDoPageResult = contactListDao.selectPage(contactListPageReq.getPageParam(), wrapper);
        List<ContactListResp> contactListResps = BeanUtil.copyToList(contactListDoPageResult.getList(), ContactListResp.class);
        buildContact(userId, contactListResps);
        return new PageResultResp(contactListResps, contactListDoPageResult.getTotal(), contactListPageReq.getPageParam().getPageNo());
    }

    private void buildContact(String userId, List<ContactListResp> contactListResps) {
        if (CollectionUtils.isNotEmpty(contactListResps)) {
            QueryWrapper<ContactBackListDo> wrapperBack = new QueryWrapper();
            wrapperBack.eq("deleted", 0);
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapperBack.eq("creator", userId);
            }
            List<ContactBackListDo> contactBackListDos = contactBackListDao.selectList(wrapperBack);
            if (CollectionUtils.isNotEmpty(contactBackListDos)) {
                HashMap<String, String> phoneMap = new HashMap<>();
                for (ContactBackListDo contactBackListDo : contactBackListDos) {
                    phoneMap.put(contactBackListDo.getPhoneNum(), contactBackListDo.getPhoneNum());
                }
                for (ContactListResp contactListResp : contactListResps) {
                    if (phoneMap.get(contactListResp.getPhoneNum()) != null) {
                        contactListResp.setBlacklist(1);
                    } else {
                        contactListResp.setBlacklist(0);
                    }
                }
            }
        }
    }

    @Override
    public int saveContactList(ContactListReq contactListReq) {
        ContactGroupDo contact = contactGroupDao.selectById(contactListReq.getGroupId());
        if (Objects.isNull(contact)) {
            throw new BizException("联系人组不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(contact.getCreator())) {
            throw new BizException("不能操作别人的联系人组");
        }

        if (StringUtils.isNotEmpty(contactListReq.getPhoneNum()) && !Validator.isMobile(contactListReq.getPhoneNum())) {
            throw new BizException(ContactListCode.VARIABLE_BAD_PHONE);
        }
        QueryWrapper<ContactListDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);

        wrapper.eq("group_id", contactListReq.getGroupId());

        wrapper.eq("phone_num", contactListReq.getPhoneNum());

        if (StringUtils.isBlank(contactListReq.getCreator())) {
            String userId = SessionContextUtil.getUser().getUserId();
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapper.eq("creator", userId);
            }
        } else {
            wrapper.eq("creator", contactListReq.getCreator());
        }
        List<ContactListDo> contactListDos = contactListDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(contactListDos)) {
            throw new BizException(ContactListCode.VARIABLE_BAD_REQUEST);
        }
        ContactListDo contactListDo = new ContactListDo();
        BeanUtil.copyProperties(contactListReq, contactListDo);
        if (StringUtils.isNotEmpty(contactListReq.getCreator())) {
            contactListDo.setCreator(contactListReq.getCreator());
            contactListDo.setUpdater(contactListReq.getCreator());
        }
        return contactListDao.insert(contactListDo);
    }

    @Override
    public int updateContactList(ContactListEditReq contactListEditReq) {
        ContactGroupDo contact = contactGroupDao.selectById(contactListEditReq.getGroupId());
        if (Objects.isNull(contact)) {
            throw new BizException("联系人组不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(contact.getCreator())) {
            throw new BizException("不能操作别人的联系人组");
        }

        if (StringUtils.isNotEmpty(contactListEditReq.getPhoneNum()) && !Validator.isMobile(contactListEditReq.getPhoneNum())) {
            throw new BizException(ContactListCode.VARIABLE_BAD_PHONE);
        }
        QueryWrapper<ContactListDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);

        wrapper.eq("group_id", contactListEditReq.getGroupId());

        wrapper.eq("phone_num", contactListEditReq.getPhoneNum());

        wrapper.ne("id", contactListEditReq.getId());

        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<ContactListDo> contactListDos = contactListDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(contactListDos)) {
            for (ContactListDo contactListDo : contactListDos) {
                if (!contactListEditReq.getId().equals(contactListDo.getId())) {
                    throw new BizException(ContactListCode.VARIABLE_BAD_REQUEST);
                }
            }
        }
        ContactListDo contactListDo = new ContactListDo();
        BeanUtil.copyProperties(contactListEditReq, contactListDo);
        return contactListDao.updateById(contactListDo);
    }

    @Override
    public int delContactListById(Long id, Long groupId) {
        ContactListDo listDo = contactListDao.selectById(id);
        if (Objects.isNull(listDo)) {
            throw new BizException("联系人不存在");
        }
        if (!groupId.equals(listDo.getGroupId())) {
            throw new BizException("联系人组不一致");
        }
        ContactGroupDo groupDo = contactGroupDao.selectById(groupId);
        if (Objects.nonNull(groupDo) && !SessionContextUtil.getUser().getUserId().equals(groupDo.getCreator())) {
            throw new BizException("联系人组不是你的,无权操作");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return contactListDao.delContactListById(map);
    }

    @Override
    public ResponseEntity<byte[]> exportContactListTemple() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        ArrayList<ContactListExcelReq> exportList = new ArrayList<>();
        exportList.add(new ContactListExcelReq());
        try {
            byte[] data = exportExcel(ContactListExcelReq.class, exportList, "联系人信息");
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel; charset=UTF-8")
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment; filename=%s", URLEncoder.encode("联系人信息" + sdf.format(new Date()) + ".xlsx", "UTF-8")))
                    .body(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ImportContactListResp importContactListTemple(MultipartFile file, Long groupId) {
        int errorNum = 0;
        List<ContactListImportExcelReq> importDtoList = null;
        try {
            importDtoList = EasyExcel.read(file.getInputStream()).head(ContactListImportExcelReq.class).doReadAllSync();
        } catch (Exception e) {
            log.error("excel文件解析失败{}", e);
        }
        ImportContactListResp importContactListResp = new ImportContactListResp();
        if (CollectionUtils.isNotEmpty(importDtoList)) {
            if (importDtoList.size() > 10000) {
                throw new BizException(ContactListCode.VARIABLE_BAD_IMPORT);
            }
            //获取当前组下面信息
            QueryWrapper<ContactListDo> wrapper = new QueryWrapper();
            wrapper.eq("deleted", 0);

            wrapper.eq("group_id", groupId);
            String userId = SessionContextUtil.getUser().getUserId();
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapper.eq("creator", userId);
            }
            List<ContactListDo> contactListDoss = contactListDao.selectList(wrapper);
            HashMap<String, String> phoneMap = new HashMap<>();
            for (ContactListDo contactListDo : contactListDoss) {
                phoneMap.put(contactListDo.getPhoneNum(), contactListDo.getPhoneNum());
            }
            ArrayList<ContactListDo> contactListDos = new ArrayList<>();
            for (ContactListImportExcelReq importExcelReq : importDtoList) {
                ContactListDo contactListDo = new ContactListDo();

                if (StringUtils.isEmpty(importExcelReq.getPhoneNum()) || phoneMap.get(importExcelReq.getPhoneNum()) != null) {
                    errorNum++;
                    continue;
                } else {
                    if (Validator.isMobile(importExcelReq.getPhoneNum())) {
                        contactListDo.setPhoneNum(importExcelReq.getPhoneNum());
                    } else {
                        errorNum++;
                        continue;
                    }
                }
                phoneMap.put(importExcelReq.getPhoneNum(), importExcelReq.getPhoneNum());
                contactListDo.setGroupId(groupId);
                contactListDos.add(contactListDo);
            }
            int i = 0;
            if (CollectionUtils.isNotEmpty(contactListDos)) {
                i = contactListDao.insertBatch(contactListDos);
            }
            importContactListResp.setErrorNum(errorNum);
            importContactListResp.setSuccessNum(i);
        }
        return importContactListResp;
    }


    @Override
    public Map<String, Object> getContactListUpdateTimeByGroupId(Long groupId) {
        Date updatetime = contactListDao.getContactListUpdateTimeByGroupId(groupId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("updateTime", updatetime);
        return map;
    }

    @Override
    public int delContactListByPhone(ContactListReq contactListReq) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("groupId", contactListReq.getGroupId());
        map.put("phoneNum", contactListReq.getPhoneNum());
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return contactListDao.delContactListByPhone(map);
    }

    @Override
    public List<ContactListResp> getContactListAlls(ContactListPageReq contactListPageReq) {
        QueryWrapper<ContactListDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("group_id", contactListPageReq.getGroupId());
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("id");
        List<ContactListDo> contactListDos = contactListDao.selectList(wrapper);
        List<ContactListResp> contactListResps = BeanUtil.copyToList(contactListDos, ContactListResp.class);
        buildContact(contactListPageReq.getUserId(), contactListResps);
        return contactListResps;
    }

    @Override
    public Map<Long, List<ContactListResp>> getContactListByGroupIds(List<Long> groupIds) {
        return groupIds.stream()
                .collect(Collectors.toMap(Function.identity(), this::groupId2ContactListResp));
    }

    @Override
    public Integer importContact(Long groupId, List<String> phoneList) {
        if(groupId != null && CollectionUtils.isNotEmpty(phoneList)) {
            List<ContactListDo> newContactList = new ArrayList<>();
            LambdaQueryWrapper<ContactListDo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ContactListDo::getDeleted, 0);
            wrapper.eq(ContactListDo::getGroupId, groupId);
            List<ContactListDo> contactListDos = contactListDao.selectList(wrapper);
            ContactListDo contactListDo;
            if (CollectionUtils.isNotEmpty(contactListDos)) {
                List<String> isHaveList = contactListDos.stream().map(ContactListDo::getPhoneNum).collect(Collectors.toList());
                for (String phoneNum : phoneList) {
                    if(!isHaveList.contains(phoneNum)){
                        contactListDo = new ContactListDo();
                        contactListDo.setPhoneNum(phoneNum);
                        contactListDo.setGroupId(groupId);
                        newContactList.add(contactListDo);
                    }
                }
            }else{
                for(String phoneNum : phoneList) {
                    contactListDo = new ContactListDo();
                    contactListDo.setPhoneNum(phoneNum);
                    contactListDo.setGroupId(groupId);
                    newContactList.add(contactListDo);
                }
            }
            if(CollectionUtils.isNotEmpty(newContactList)){
                return contactListDao.insertBatch(newContactList);
            }
        }
        return 0;
    }

    private List<ContactListResp> groupId2ContactListResp(Long groupId) {
        return contactListDao.selectList(new LambdaQueryWrapper<ContactListDo>()
                .eq(ContactListDo::getGroupId, groupId)
                .orderByDesc(BaseDo::getCreateTime))
                .stream()
                .map(contactListDo -> {
                    ContactListResp contactListResp = new ContactListResp();
                    BeanUtils.copyProperties(contactListDo, contactListResp);
                    return contactListResp;
                })
                .collect(Collectors.toList());
    }

}
