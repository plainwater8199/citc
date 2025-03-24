package com.citc.nce.authcenter.sysmsg.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.vo.UserNameInfo;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.sysmsg.dao.SystemMsgManageDao;
import com.citc.nce.authcenter.sysmsg.dao.UserMsgDetailDao;
import com.citc.nce.authcenter.sysmsg.entity.SystemMsgManageDo;
import com.citc.nce.authcenter.sysmsg.entity.UserMsgDetailDo;
import com.citc.nce.authcenter.sysmsg.service.AdminSysMsgManageService;
import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.AddSysMsgManageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.ImportUserByCSVResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.QuerySysMsgManageDetailResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UpdateSysMsgManageResp;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.constant.BusinessTypeEnum;
import com.citc.nce.misc.constant.ProcessingContentEnum;
import com.citc.nce.misc.msg.MsgApi;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.*;


@Service
@Slf4j
public class AdminSysMsgManageServiceImpl implements AdminSysMsgManageService {

    @Resource
    private UserMsgDetailDao userMsgDetailDao;
    @Resource
    private SystemMsgManageDao systemMsgManageDao;
    @Resource
    private UserService userService;
    @Resource
    private IdentificationService identificationService;


    @Override
    @Transactional
    public AddSysMsgManageResp addSysMsgManage(AddSysMsgManageReq req) {
        AddSysMsgManageResp resp = new AddSysMsgManageResp();
        Integer isSend = req.getIsSend();
        Date sendTime = req.getSendTime();
        if (!(0 == isSend && sendTime == null)) {
            SystemMsgManageDo systemMsgManageDo = new SystemMsgManageDo();
            BeanUtils.copyProperties(req, systemMsgManageDo);
            systemMsgManageDo.setSendTime((1 == isSend) ? new Date() : req.getSendTime());
            systemMsgManageDo.setCreator(SessionContextUtil.getUser().getUserId());
            systemMsgManageDo.setCreateTime(new Date());
            systemMsgManageDo.setDeleted(0);
            systemMsgManageDao.insert(systemMsgManageDo);
            //1、获取用户信息
            if (1 == isSend) {
                sendSysMsgByManage(systemMsgManageDo.getId());
            }

            //添加处理记录
            identificationService.addProcessingRecord(String.valueOf(systemMsgManageDo.getId()), ProcessingContentEnum.CJXX.getName(), BusinessTypeEnum.ZNXGL.getCode());
        } else {
            throw new BizException(AuthCenterError.SEND_TIME_NOT_NULL);
        }
        resp.setResult(true);
        return resp;
    }

    /**
     * 发送站内信从管理端
     *
     * @param id 站内信管理ID
     */
    private void sendSysMsgByManage(Long id) {
        if (id != null) {
            SystemMsgManageDo systemMsgManageDo = systemMsgManageDao.selectById(id);
            if (systemMsgManageDo != null) {
                List<String> userIds;
                Integer receiveType = systemMsgManageDo.getReceiveType();
                if (receiveType == 1) {
                    userIds = getUserIds(systemMsgManageDo);
                } else {
                    List<Integer> tags = getObjectIntList(systemMsgManageDo.getReceiveObjects());
                    if (tags.contains(0)) {//获取所有用户
                        userIds = userService.findAllActiveUser();
                    } else {//根据标签获取用户
                        userIds = identificationService.findUsersByCertificateOptions(tags);
                    }
                }
                SendSystemMessageReq req = new SendSystemMessageReq();
                req.setMsgTitle(systemMsgManageDo.getTitle());
                req.setMsgDetail(systemMsgManageDo.getContent());
                req.setBusinessType(9);
                req.setSourceId(id);
                req.setCreator(systemMsgManageDo.getCreator());
                req.setUserIds(userIds);
                sendSysMsg(req);
            }
        }
    }

    private List<String> getUserIds(SystemMsgManageDo systemMsgManageDo) {
        List<String> userIds;
        userIds = getObjectStrList(systemMsgManageDo.getReceiveObjects());
        //校验用户有效性
        List<String> userIdList = userService.findAllActiveUser();
        Long noExistence = userIds.stream().filter(x -> !userIdList.contains(x)).count();
        if (noExistence > 0) {
            throw new BizException(AuthCenterError.PARAM_USER_ID_ERROR);
        }
        return userIds;
    }

    private List<Integer> getObjectIntList(String receiveObjects) {
        List<Integer> objectList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(receiveObjects)) {
            String[] objectArr = receiveObjects.split(",");
            for (String s : objectArr) {
                objectList.add(Integer.valueOf(s));
            }
        }
        return objectList;
    }

    /**
     * 将对应的字符串转成List
     *
     * @param receiveObjects 对象字符串
     * @return 数组
     */
    private List<String> getObjectStrList(String receiveObjects) {
        List<String> objectList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(receiveObjects)) {
            String[] objectArr = receiveObjects.split(",");
            objectList.addAll(Arrays.asList(objectArr));
        }
        return objectList;
    }

    @Override
    @Transactional
    public UpdateSysMsgManageResp updateSysMsgManage(UpdateSysMsgManageReq req) {
        UpdateSysMsgManageResp resp = new UpdateSysMsgManageResp();
        Long id = req.getId();
        Integer isSend = req.getIsSend();
        Date sendTime = req.getSendTime();
        if (!(0 == isSend && sendTime == null)) {
            SystemMsgManageDo systemMsgManageDo = systemMsgManageDao.selectById(id);
            if (systemMsgManageDo != null) {
                if (systemMsgManageDo.getIsSend() != 1) {
                    BeanUtils.copyProperties(req, systemMsgManageDo);
                    systemMsgManageDo.setSendTime((1 == isSend) ? new Date() : req.getSendTime());
                    systemMsgManageDo.setId(id);
                    systemMsgManageDo.setUpdater(SessionContextUtil.getUser().getUserId());
                    systemMsgManageDo.setUpdateTime(new Date());
                    systemMsgManageDao.updateById(systemMsgManageDo);
                    //1、获取用户信息
                    if (1 == isSend) {
                        sendSysMsgByManage(systemMsgManageDo.getId());
                    }

                    //添加处理记录
                    identificationService.addProcessingRecord(String.valueOf(id), ProcessingContentEnum.BJXX.getName(), BusinessTypeEnum.ZNXGL.getCode());
                } else {
                    throw new BizException(AuthCenterError.SYS_MSG_IS_SEND);
                }
            } else {
                throw new BizException(AuthCenterError.Execute_SQL_QUERY);

            }
        } else {
            throw new BizException(AuthCenterError.SEND_TIME_NOT_NULL);
        }
        resp.setResult(true);
        return resp;
    }


    @Override
    public PageResult<SysMsgManageListInfo> querySysMsgManageList(QuerySysMsgManageListReq req) {
        IPage<SystemMsgManageDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        LambdaQueryWrapper<SystemMsgManageDo> query = Wrappers.<SystemMsgManageDo>lambdaQuery();
        //条件筛选
        if (StrUtil.isNotBlank(req.getTitle())) {
            query.like(SystemMsgManageDo::getTitle, req.getTitle());
        }
        query.orderByDesc(SystemMsgManageDo::getCreateTime);
        IPage<SystemMsgManageDo> manageDoIPage = systemMsgManageDao.selectPage(page, query);
        List<SystemMsgManageDo> pageRecords = manageDoIPage.getRecords();
        if (CollectionUtils.isEmpty(pageRecords)) {
            return new PageResult<>();
        }
        List<SysMsgManageListInfo> manageListInfoList = BeanUtil.copyToList(pageRecords, SysMsgManageListInfo.class);
        com.citc.nce.common.core.pojo.PageResult<SysMsgManageListInfo> pageResult = new com.citc.nce.common.core.pojo.PageResult<>();
        pageResult.setList(manageListInfoList);
        pageResult.setTotal(manageDoIPage.getTotal());
        return pageResult;
    }

    @Override
    public QuerySysMsgManageDetailResp querySysMsgManageDetail(QuerySysMsgManageDetailReq req) {
        QuerySysMsgManageDetailResp resp = new QuerySysMsgManageDetailResp();
        SystemMsgManageDo systemMsgManageDo = systemMsgManageDao.selectById(req.getId());
        if (systemMsgManageDo != null) {
            //转义回来XSS拦截改变后的数据
            String content = HtmlUtils.htmlUnescape(systemMsgManageDo.getContent());
            resp.setContent(content);
            resp.setCreateTime(systemMsgManageDo.getCreateTime());
            resp.setSendTime(systemMsgManageDo.getSendTime());
            resp.setTitle(systemMsgManageDo.getTitle());
            resp.setCreator(systemMsgManageDo.getCreator());
            resp.setIsSend(systemMsgManageDo.getIsSend());
            resp.setReceiveType((systemMsgManageDo.getReceiveType()));
            if (StrUtil.isNotBlank(systemMsgManageDo.getReceiveObjects())) {
                resp.setReceiveObjectMap(getReceiveObjectMap(systemMsgManageDo.getReceiveType(), systemMsgManageDo.getReceiveObjects()));
            }
        } else {
            throw new BizException(AuthCenterError.Execute_SQL_QUERY);
        }
        return resp;
    }

    private Map<String, String> getReceiveObjectMap(Integer receiveType, String receiveObjects) {
        Map<String, String> receiveObjectMap;
        if (1 == receiveType) {
            List<String> userIds = getObjectStrList(receiveObjects);
            receiveObjectMap = userService.findUserNameByIds(userIds);
        } else {
            List<Integer> tags = getObjectIntList(receiveObjects);
            receiveObjectMap = identificationService.findCertificateNameMap(tags);
            if (tags.contains(0)) {
                receiveObjectMap.put("0", "全部");
            }
        }
        return receiveObjectMap;
    }

    /**
     * 发送站内信息
     *
     * @param req 请求信息
     * @return 响应结果
     */
    @Override
    public SendSystemMessageResp sendSysMsg(SendSystemMessageReq req) {
        SendSystemMessageResp resp = new SendSystemMessageResp();
        List<String> userIds = req.getUserIds();
        if (CollectionUtils.isNotEmpty(userIds)) {
            Long sourceId = req.getSourceId() == null ? 1L : Long.parseLong(req.getSourceId().toString());
            List<UserMsgDetailDo> userMsgDetailDoList = new ArrayList<>();
            UserMsgDetailDo userMsgDetailDo;
            for (String item : userIds) {
                userMsgDetailDo = new UserMsgDetailDo();
                userMsgDetailDo.setMsgId(UserSysMsgServiceImpl.createMsgId());
                userMsgDetailDo.setMsgTitle(req.getMsgTitle());
                userMsgDetailDo.setMsgDetail(req.getMsgDetail());
                userMsgDetailDo.setSourceId(sourceId);
                userMsgDetailDo.setBusinessType(req.getBusinessType());
                userMsgDetailDo.setUserUuid(item);
                userMsgDetailDo.setPostTime(new Date());
                userMsgDetailDo.setMsgType(0);
                userMsgDetailDo.setCreator(req.getCreator());
                userMsgDetailDo.setCreateTime(new Date());
                userMsgDetailDoList.add(userMsgDetailDo);
            }
            userMsgDetailDao.insertBatch(userMsgDetailDoList);
        }
        resp.setResult(true);
        return resp;
    }

    @Override
    public ImportUserByCSVResp importUserByCSV(MultipartFile file) {
        ImportUserByCSVResp resp = new ImportUserByCSVResp();
        if (file != null) {
            try {

                try {
                    List<UserNameInfo> list1 = EasyExcel.read(file.getInputStream()).sheet(0).head(UserNameInfo.class).doReadSync();
                    resp.setUserNameInfos(list1);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw BizException.build(AuthCenterError.IMPORT_API_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resp;
    }

    @Override
    public void deleteSysMsgManage(DeleteSysMsgManageReq req) {
        SystemMsgManageDo systemMsgManageDo = systemMsgManageDao.selectById(req.getId());
        if (systemMsgManageDo != null) {
            LambdaQueryWrapperX<SystemMsgManageDo> queryWrapper = new LambdaQueryWrapperX<>();
            queryWrapper.eq(SystemMsgManageDo::getDeleted, 0);
            queryWrapper.eq(SystemMsgManageDo::getId, req.getId());
            systemMsgManageDao.delete(queryWrapper);

            //添加处理记录
            identificationService.addProcessingRecord(SessionContextUtil.getUser().getUserId(), ProcessingContentEnum.SCXX.getName(), BusinessTypeEnum.ZNXGL.getCode());
        } else {
            throw new BizException(AuthCenterError.Execute_SQL_QUERY);
        }
    }

    @Override
    @Transactional
    public void sendTimeSysMsg() {
        LambdaQueryWrapperX<SystemMsgManageDo> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(SystemMsgManageDo::getDeleted, 0);
        queryWrapper.eq(SystemMsgManageDo::getIsSend, 0);
        queryWrapper.lt(SystemMsgManageDo::getSendTime, new Date());
        List<SystemMsgManageDo> systemMsgManageDos = systemMsgManageDao.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(systemMsgManageDos)) {
            List<SystemMsgManageDo> isSendList = new ArrayList<>();
            for (SystemMsgManageDo item : systemMsgManageDos) {
                sendSysMsgByManage(item.getId());
                item.setIsSend(1);
                isSendList.add(item);
            }
            systemMsgManageDao.updateBatch(isSendList);
        }
    }
}
