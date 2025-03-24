package com.citc.nce.auth.contactbacklist.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.contactbacklist.dao.ContactBackListDao;
import com.citc.nce.auth.contactbacklist.entity.ContactBackListDo;
import com.citc.nce.auth.contactbacklist.exception.ContactBackListCode;
import com.citc.nce.auth.contactbacklist.service.ContactBackListService;
import com.citc.nce.auth.contactbacklist.vo.*;
import com.citc.nce.auth.contactlist.vo.ContactListImportExcelReq;
import com.citc.nce.auth.contactlist.vo.ImportContactListResp;
import com.citc.nce.auth.messagetemplate.vo.PageResultResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class ContactBackListServiceImpl implements ContactBackListService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;
    @Resource
    private ContactBackListDao contactBackListDao;

    @Override
    public PageResultResp<ContactBackListResp> getContactBackLists(ContactBackListPageReq contactBackListPageReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(contactBackListPageReq.getPersonName())) {
            wrapper.like("person_name", contactBackListPageReq.getPersonName());
        }

        if (StringUtils.isNotEmpty(contactBackListPageReq.getPhoneNum())) {
            wrapper.like("phone_num", contactBackListPageReq.getPhoneNum());
        }
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("id");
        PageResult<ContactBackListDo> ContactBackListDoPageResult = contactBackListDao.selectPage(contactBackListPageReq.getPageParam(), wrapper);
        List<ContactBackListResp> contactListResps = BeanUtil.copyToList(ContactBackListDoPageResult.getList(), ContactBackListResp.class);
        return new PageResultResp<>(contactListResps, ContactBackListDoPageResult.getTotal(), contactBackListPageReq.getPageParam().getPageNo());

    }

    @Override
    public int saveContactBackList(ContactBackListReq contactBackListReq) {
        String phoneNum = contactBackListReq.getPhoneNum();
        if (StringUtils.isNotEmpty(phoneNum) && !Validator.isMobile(phoneNum)) {
            throw new BizException(ContactBackListCode.VARIABLE_BAD_PHONE);
        }
        String userId = SessionContextUtil.getLoginUser().getUserId();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(phoneNum)) {
            wrapper.eq("phone_num", phoneNum);
        }
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<ContactBackListDo> ContactBackListDos = contactBackListDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(ContactBackListDos)) {
            throw new BizException(ContactBackListCode.VARIABLE_BAD_REQUEST);
        }
        ContactBackListDo ContactBackListDo = new ContactBackListDo();
        BeanUtil.copyProperties(contactBackListReq, ContactBackListDo);
        return contactBackListDao.insert(ContactBackListDo);
    }


    @Override
    public int updateContactBackList(ContactBackListEditReq contactBackListEditReq) {
        if (StringUtils.isNotEmpty(contactBackListEditReq.getPhoneNum()) && !Validator.isMobile(contactBackListEditReq.getPhoneNum())) {
            throw new BizException(ContactBackListCode.VARIABLE_BAD_PHONE);
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(contactBackListEditReq.getPhoneNum())) {
            wrapper.eq("phone_num", contactBackListEditReq.getPhoneNum());
        }
        wrapper.ne("id", contactBackListEditReq.getId());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<ContactBackListDo> ContactBackListDos = contactBackListDao.selectList(Wrappers.<ContactBackListDo>lambdaQuery()
                .eq(ContactBackListDo::getDeleted, 0)
                .eq(ContactBackListDo::getPhoneNum, contactBackListEditReq.getPhoneNum()));
        if (CollectionUtils.isNotEmpty(ContactBackListDos)) {
            for (ContactBackListDo ContactBackListDo : ContactBackListDos) {
                if (!contactBackListEditReq.getId().equals(ContactBackListDo.getId())) {
                    throw new BizException(ContactBackListCode.VARIABLE_BAD_REQUEST);
                }
            }
        }
        ContactBackListDo ContactBackListDo = new ContactBackListDo();
        BeanUtil.copyProperties(contactBackListEditReq, ContactBackListDo);
        return contactBackListDao.updateById(ContactBackListDo);
    }

    @Override
    public int delContactBackListById(Long id) {
        ContactBackListDo aDo = contactBackListDao.selectById(id);
        if (Objects.isNull(aDo)) return 0;
        if (!SessionContextUtil.getUser().getUserId().equals(aDo.getCreator())) {
            throw new BizException("不能移除别人的创建的黑名单");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return contactBackListDao.delContactBackListById(map);
    }

    @Override
    public ResponseEntity<byte[]> exportContactBackListTemple() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        ArrayList<ContactBackListExcelReq> exportList = new ArrayList<>();
        exportList.add(new ContactBackListExcelReq());
        try {
            byte[] data = exportExcel(ContactBackListExcelReq.class, exportList, "联系人信息");
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


    @Override
    public ImportContactListResp importContactBackListTemple(MultipartFile file) {

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
                throw new BizException(ContactBackListCode.VARIABLE_BAD_IMPORT);
            }
            //获取黑名单信息
            QueryWrapper<ContactBackListDo> wrapper = new QueryWrapper();
            wrapper.eq("deleted", 0);

            String userId = SessionContextUtil.getUser().getUserId();
            if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
                wrapper.eq("creator", userId);
            }

            List<ContactBackListDo> ContactBackListDoss = contactBackListDao.selectList(wrapper);
            HashMap<String, String> phoneMap = new HashMap<>();
            for (ContactBackListDo ContactBackListDo : ContactBackListDoss) {
                phoneMap.put(ContactBackListDo.getPhoneNum(), ContactBackListDo.getPhoneNum());
            }
            ArrayList<ContactBackListDo> ContactBackListDos = new ArrayList<>();
            for (ContactListImportExcelReq importExcelReq : importDtoList) {
                ContactBackListDo ContactBackListDo = new ContactBackListDo();
                if (StringUtils.isEmpty(importExcelReq.getPhoneNum()) || phoneMap.get(importExcelReq.getPhoneNum()) != null) {
                    errorNum++;
                    continue;
                } else {
                    if (Validator.isMobile(importExcelReq.getPhoneNum())) {
                        ContactBackListDo.setPhoneNum(importExcelReq.getPhoneNum());
                    } else {
                        errorNum++;
                        continue;
                    }

                }
                phoneMap.put(importExcelReq.getPhoneNum(), importExcelReq.getPhoneNum());
                ContactBackListDos.add(ContactBackListDo);
            }
            int i = 0;
            if (CollectionUtils.isNotEmpty(ContactBackListDos)) {
                i = contactBackListDao.insertBatch(ContactBackListDos);
            }
            importContactListResp.setErrorNum(errorNum);
            importContactListResp.setSuccessNum(i);
        }
        return importContactListResp;
    }

    @Override
    public List<ContactBackListResp> getAllList(ContactBackListPageReq contactListBackPageReq) {
        QueryWrapper<ContactBackListDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        if (StringUtils.isNotEmpty(contactListBackPageReq.getCreator())) {
            wrapper.eq("creator", contactListBackPageReq.getCreator());
        }
        List<ContactBackListDo> contactBackListDos = contactBackListDao.selectList(wrapper);
        List<ContactBackListResp> contactListResps = BeanUtil.copyToList(contactBackListDos, ContactBackListResp.class);
        if (CollectionUtils.isEmpty(contactListResps)) {
            return new ArrayList<>();
        }
        return contactListResps;
    }

    @Override
    public boolean getContactInBlack(String create, String phone) {
        return contactBackListDao.exists(new LambdaQueryWrapper<ContactBackListDo>()
                .eq(ContactBackListDo::getDeleted, 0)
                .eq(ContactBackListDo::getPhoneNum, phone)
                .eq(BaseDo::getCreator, create));
    }
}
