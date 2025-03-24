package com.citc.nce.authcenter.sysmsg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.admin.dao.AdminUserMapper;
import com.citc.nce.authcenter.admin.entity.AdminUserDo;
import com.citc.nce.authcenter.sysmsg.dao.UserMsgDetailDao;
import com.citc.nce.authcenter.sysmsg.dto.MsgReq;
import com.citc.nce.authcenter.sysmsg.entity.UserMsgDetailDo;
import com.citc.nce.authcenter.sysmsg.service.UserSysMsgService;
import com.citc.nce.authcenter.systemmsg.vo.UserMsgInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.QueryMsgDetailsReq;
import com.citc.nce.authcenter.systemmsg.vo.req.QuerySysMsgReq;
import com.citc.nce.authcenter.systemmsg.vo.req.SendSystemUserMessageReq;
import com.citc.nce.authcenter.systemmsg.vo.req.UserMsgReq;
import com.citc.nce.authcenter.systemmsg.vo.resp.QueryMsgDetailsResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.SendSystemUserMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UnreadSysMsgQueryResp;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.misc.msg.MsgApi;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class UserSysMsgServiceImpl implements UserSysMsgService {

    @Resource
    private UserMsgDetailDao userMsgDetailDao;
    @Resource
    private MsgApi msgApi;
    @Resource
    private AdminUserMapper adminUserMapper;


    @Override
    public Object querySysMsgList(QuerySysMsgReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        Page<UserMsgDetailDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        QueryWrapper<UserMsgDetailDo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_uuid", userId);
        wrapper.orderByDesc("post_time");
        if (req.getTitle() != null && !"".equals(req.getTitle())) {
            wrapper.like("msg_title", req.getTitle());
        }
        Page<UserMsgDetailDo> selectPage = userMsgDetailDao.selectPage(page, wrapper);
        List<UserMsgDetailDo> list = checkoutMsgId(selectPage, page, wrapper);
        List<UserMsgInfo> result = new ArrayList<>();
        list.forEach(userMsgDetailDo -> {
            UserMsgInfo resp = new UserMsgInfo();
            BeanUtils.copyProperties(userMsgDetailDo, resp);
            result.add(resp);
        });
        PageResult<UserMsgInfo> pageResult = new PageResult<>();
        pageResult.setList(result);
        pageResult.setTotal(selectPage.getTotal());
        return pageResult;
    }

    /**
     * 检查msgId是否添加
     *
     * @param selectPage 第一次查询结果
     * @param page       分页
     * @param wrapper    查询条件
     * @return 响应
     */
    private List<UserMsgDetailDo> checkoutMsgId(Page<UserMsgDetailDo> selectPage, Page<UserMsgDetailDo> page, QueryWrapper<UserMsgDetailDo> wrapper) {
        List<UserMsgDetailDo> list = selectPage.getRecords();
        if (!CollectionUtils.isEmpty(list)) {
            List<UserMsgDetailDo> updateList = new ArrayList<>();
            for (UserMsgDetailDo item : list) {
                if (Strings.isNullOrEmpty(item.getMsgId())) {
                    item.setMsgId(createMsgId());
                    updateList.add(item);
                }
            }
            if (!CollectionUtils.isEmpty(updateList)) {
                userMsgDetailDao.updateBatch(updateList);
                selectPage = userMsgDetailDao.selectPage(page, wrapper);
                list = selectPage.getRecords();
            }
        }
        return list;
    }

    @Override
    public QueryMsgDetailsResp querySysMsgDetails(QueryMsgDetailsReq req) {
        QueryMsgDetailsResp resp = new QueryMsgDetailsResp();
        QueryWrapper<UserMsgDetailDo> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id", req.getMsgId());
        List<UserMsgDetailDo> msgDetailDos = userMsgDetailDao.selectList(wrapper);
        UserMsgDetailDo msgDetailDo = msgDetailDos.get(0);
        BeanUtils.copyProperties(msgDetailDo, resp);
        if (msgDetailDo.getMsgType() == 0) {
            UserMsgDetailDo userMsgDetailDo = new UserMsgDetailDo();
            userMsgDetailDo.setId(msgDetailDo.getId());
            userMsgDetailDo.setReadTime(new Date());
            userMsgDetailDo.setMsgType(1);
            userMsgDetailDao.updateById(userMsgDetailDo);
        }
        return resp;
    }

    @Override
    public UnreadSysMsgQueryResp unreadSysMsgQuery() {
        UnreadSysMsgQueryResp resp = new UnreadSysMsgQueryResp();
        QueryWrapper<UserMsgDetailDo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_uuid", SessionContextUtil.getUser().getUserId());
        wrapper.eq("deleted", 0);
        wrapper.eq("msg_type", 0);
        Long count = userMsgDetailDao.selectCount(wrapper);
        if (count > 99) {
            resp.setUnread("99+");
        }
        resp.setUnread(count + "");
        return resp;
    }

    @Override
    public void addMsgIntoUser(MsgReq req) {
        MsgTemplateResp msgTemplateResp = msgApi.getMsgTemplateByCode(new MsgTemplateReq().setTempldateCode(req.getTempldateCode()));
        if (StringUtils.isNotBlank(req.getRemark())) {
            msgTemplateResp.setTempldateContent(msgTemplateResp.getTempldateContent().replace("{detail}", req.getRemark()));
        } else {
            msgTemplateResp.setTempldateContent(msgTemplateResp.getTempldateContent().replace("由于{detail}", req.getRemark()));
        }
        UserMsgDetailDo msgDo = new UserMsgDetailDo()
                .setPostTime(new Date())
                .setMsgTitle(msgTemplateResp.getTempldateSubject())
                .setMsgDetail(msgTemplateResp.getTempldateContent())
                .setUserUuid(req.getUserId())
                .setMsgId(createMsgId());
        userMsgDetailDao.insert(msgDo);
    }

    @Override
    public void saveMsg(UserMsgReq req) {
        UserMsgDetailDo msgDo = new UserMsgDetailDo();
        BeanUtils.copyProperties(req, msgDo);
        msgDo.setUpdater(req.getUserUuid()).setCreator(req.getUserUuid());
        msgDo.setMsgId(createMsgId());
        userMsgDetailDao.insert(msgDo);
    }

    @Override
    public SendSystemUserMessageResp sendSystemMessage(SendSystemUserMessageReq sendSystemMessageReq) {
        SendSystemUserMessageResp resp = new SendSystemUserMessageResp();
        Map<String, String> paraMap = sendSystemMessageReq.getParaMap();
        String messageCode = sendSystemMessageReq.getMessageCode();
        Long sourceId = sendSystemMessageReq.getSourceId() == null ? 1 : Long.parseLong(sendSystemMessageReq.getSourceId().toString());
        if (!paraMap.isEmpty() && !Strings.isNullOrEmpty(messageCode)) {
            MsgTemplateReq msgTemplateReq = new MsgTemplateReq();
            msgTemplateReq.setTempldateCode(messageCode);
            MsgTemplateResp msgTemplateResp = msgApi.getMsgTemplateByCode(msgTemplateReq);
            String title = msgTemplateResp.getTempldateSubject();
            String content = msgTemplateResp.getTempldateContent();
            for (String name : paraMap.keySet()) {
                title = title.replace("{" + name + "}", paraMap.get(name));
                content = content.replace("{" + name + "}", paraMap.get(name));
            }
            if ("API_TIMES_WARNING".equals(messageCode)) {
                UserMsgDetailDo userMsgDetailDo = new UserMsgDetailDo();
                userMsgDetailDo.setMsgId(createMsgId());
                userMsgDetailDo.setMsgTitle(title);
                userMsgDetailDo.setMsgDetail(content);
                userMsgDetailDo.setSourceId(sourceId);
                userMsgDetailDo.setBusinessType(sendSystemMessageReq.getBusinessType());
                userMsgDetailDo.setUserUuid(sendSystemMessageReq.getUserUuid());
                userMsgDetailDo.setPostTime(new Date());
                userMsgDetailDo.setMsgType(0);
                userMsgDetailDao.insert(userMsgDetailDo);
            } else {
                //查询所有拥有工单菜单权限的管理员
                List<AdminUserDo> adminUserRespList = adminUserMapper.findWorkOrderPermissionUsers();
                AdminUserDo adminUser = new AdminUserDo();
                adminUser.setUserId(sendSystemMessageReq.getUserUuid());
                adminUserRespList.add(adminUser);

                List<UserMsgDetailDo> userMsgDetailDoList = new ArrayList<>();
                for (AdminUserDo adminUserDo : adminUserRespList) {
                    UserMsgDetailDo userMsgDetailDo = new UserMsgDetailDo();
                    userMsgDetailDo.setMsgId(createMsgId());
                    userMsgDetailDo.setMsgTitle(title);
                    userMsgDetailDo.setMsgDetail(content);
                    userMsgDetailDo.setSourceId(sourceId);
                    userMsgDetailDo.setBusinessType(sendSystemMessageReq.getBusinessType());
                    userMsgDetailDo.setUserUuid(adminUserDo.getUserId());
                    userMsgDetailDo.setPostTime(new Date());
                    userMsgDetailDo.setMsgType(0);
                    userMsgDetailDoList.add(userMsgDetailDo);
                }
                userMsgDetailDao.insertBatch(userMsgDetailDoList);
            }
            resp.setContent(content);
        }
        return resp;
    }

    @Override
    public void batchSaveMsg(List<UserMsgReq> req) {
        List<UserMsgDetailDo> userMsgDetailDoList = new ArrayList<>();
        req.forEach(item -> {
            UserMsgDetailDo msgDo = new UserMsgDetailDo();
            BeanUtils.copyProperties(item, msgDo);
            msgDo.setUpdater(item.getUserUuid()).setCreator(item.getUserUuid());
            msgDo.setMsgId(createMsgId());
            msgDo.setMsgType(0);
            userMsgDetailDoList.add(msgDo);
        });
        userMsgDetailDao.insertBatch(userMsgDetailDoList);
    }

    /**
     * 获取msgId
     *
     * @return msgId
     */
    public static String createMsgId() {
        SimpleDateFormat formatTime = new SimpleDateFormat("yyyyMMddHHmmss");
        return "MSG" + formatTime.format(new Date()) + String.format("%06d", new Random().nextInt(1000000));
    }

}
