package com.citc.nce.auth.usermessage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.adminUser.dao.AdminUserMapper;
import com.citc.nce.auth.adminUser.entity.AdminUserDo;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.usermessage.dao.UserMsgDetailDao;
import com.citc.nce.auth.usermessage.entity.UserMsgDetailDo;
import com.citc.nce.auth.usermessage.service.IUserMsgDetailService;
import com.citc.nce.auth.usermessage.vo.req.*;
import com.citc.nce.auth.usermessage.vo.resp.ReadResp;
import com.citc.nce.auth.usermessage.vo.resp.SendSystemMessageResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgDetailResp;
import com.citc.nce.auth.usermessage.vo.resp.UserMsgResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.misc.msg.MsgApi;
import com.citc.nce.misc.msg.req.MsgTemplateReq;
import com.citc.nce.misc.msg.resp.MsgTemplateResp;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:14
 * @Version: 1.0
 * @Description:
 */
@Service
public class UserMsgDetailServiceImpl extends ServiceImpl<UserMsgDetailDao, UserMsgDetailDo> implements IUserMsgDetailService {

    @Resource
    private UserMsgDetailDao dao;
    @Resource
    private MsgApi msgApi;
    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public void saveMsg(UserMsgReq req) {
        UserMsgDetailDo msgDo = new UserMsgDetailDo();
        BeanUtils.copyProperties(req, msgDo);
        msgDo.setUpdater(req.getUserUuid()).setCreator(req.getUserUuid()).setMsgId(createMsgId());
        System.out.println(msgDo);
        if (!save(msgDo)) throw new BizException(AuthError.Execute_SQL_SAVE);
    }

    @Override
    @Transactional
    public void batchSaveMsg(List<UserMsgReq> req) {
        List<UserMsgDetailDo> userMsgDetailDoList = new ArrayList<>();
        req.forEach(item -> {
            UserMsgDetailDo msgDo = new UserMsgDetailDo();
            BeanUtils.copyProperties(item, msgDo);
            msgDo.setUpdater(item.getUserUuid()).setCreator(item.getUserUuid()).setMsgId(createMsgId());
            userMsgDetailDoList.add(msgDo);
        });
        saveBatch(userMsgDetailDoList);
    }

    @Override
    public PageResult<UserMsgResp> selectByPage(PageReq req) {
        Page<UserMsgDetailDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        QueryWrapper<UserMsgDetailDo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_uuid", req.getUserId());
        wrapper.orderByDesc("post_time");
        if (req.getTitle() != null && !"".equals(req.getTitle())) {
            wrapper.like("msg_title", req.getTitle());
        }
        Page<UserMsgDetailDo> selectPage = dao.selectPage(page, wrapper);
        List<UserMsgDetailDo> list = selectPage.getRecords();
        List<UserMsgResp> result = new ArrayList<>();
        list.forEach(userMsgDetailDo -> {
            UserMsgResp resp = new UserMsgResp();
            BeanUtils.copyProperties(userMsgDetailDo, resp);
            result.add(resp);
        });
        PageResult<UserMsgResp> pageResult = new PageResult<>();
        pageResult.setList(result);
        pageResult.setTotal(selectPage.getTotal());
        return pageResult;
    }

    @Override
    public UserMsgDetailResp selectById(IdReq req) {
        UserMsgDetailDo msgDetailDo = dao.selectById(req.getId());
        UserMsgDetailResp resp = new UserMsgDetailResp();
        BeanUtils.copyProperties(msgDetailDo, resp);
        if (msgDetailDo.getMsgType() == 0) {
            UpdateWrapper<UserMsgDetailDo> wrapper = new UpdateWrapper<>();
            wrapper.eq("id", req.getId());
            wrapper.set("read_time", new Date());
            wrapper.set("msg_type", 1);
            if (!update(wrapper)) throw new BizException(AuthError.Execute_SQL);
        }
        return resp;
    }

    @Override
    public ReadResp readOrUnread(UserIdReq req) {
        ReadResp resp = new ReadResp();
        QueryWrapper<UserMsgDetailDo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_uuid", req.getUserId());
        wrapper.eq("deleted", 0);
        wrapper.eq("msg_type", 0);
        Long count = dao.selectCount(wrapper);
        if (count > 99) {
            resp.setRead("99+");
        }
        resp.setRead(count + "");
        return resp;
    }

    @Override
    @Transactional
    public SendSystemMessageResp sendSystemMessage(SendSystemMessageReq sendSystemMessageReq) {
        SendSystemMessageResp resp = new SendSystemMessageResp();
        Map<String, String> paraMap = sendSystemMessageReq.getParaMap();
        String messageCode = sendSystemMessageReq.getMessageCode();
        Integer sourceId = sendSystemMessageReq.getSourceId() == null ? 1 : Integer.parseInt(sendSystemMessageReq.getSourceId().toString());
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
                save(userMsgDetailDo);
            } else {
                //查询所有拥有工单菜单权限的管理员
                List<AdminUserDo> adminUserRespList = adminUserMapper.findWorkOrderPermissionUsers();
                AdminUserDo adminUser = new AdminUserDo();
                adminUser.setUserId(sendSystemMessageReq.getUserUuid());
                adminUserRespList.add(adminUser);

                List<UserMsgDetailDo> userMsgDetailDoList = new ArrayList<>();
                for (AdminUserDo adminUserDo : adminUserRespList) {
                    UserMsgDetailDo userMsgDetailDo = new UserMsgDetailDo();
                    userMsgDetailDo.setMsgTitle(title);
                    userMsgDetailDo.setMsgId(createMsgId());
                    userMsgDetailDo.setMsgDetail(content);
                    userMsgDetailDo.setSourceId(sourceId);
                    userMsgDetailDo.setBusinessType(sendSystemMessageReq.getBusinessType());
                    userMsgDetailDo.setUserUuid(adminUserDo.getUserId());
                    userMsgDetailDo.setPostTime(new Date());
                    userMsgDetailDoList.add(userMsgDetailDo);
                }
                saveBatch(userMsgDetailDoList);
            }
            resp.setContent(content);
        }
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
                .setMsgId(createMsgId())
                .setPostTime(new Date())
                .setMsgTitle(msgTemplateResp.getTempldateSubject())
                .setMsgDetail(msgTemplateResp.getTempldateContent())
                .setUserUuid(req.getUserId());
        if (!save(msgDo)) throw new BizException(AuthError.Execute_SQL_SAVE);
    }

    public static String createMsgId() {
        SimpleDateFormat formatTime = new SimpleDateFormat("yyyyMMddHHmmss");
        return "MSG" + formatTime.format(new Date()) + String.format("%06d", new Random().nextInt(1000000));
    }
}
