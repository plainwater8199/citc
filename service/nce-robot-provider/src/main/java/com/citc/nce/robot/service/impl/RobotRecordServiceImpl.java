package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.citc.nce.auth.csp.customer.CustomerApi;
import com.citc.nce.auth.csp.customer.vo.CustomerReq;
import com.citc.nce.auth.csp.customer.vo.CustomerResp;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robot.dao.RobotRecordDao;
import com.citc.nce.robot.entity.RobotRecordDo;
import com.citc.nce.robot.service.RobotRecordService;
import com.citc.nce.robot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
@Slf4j
public class RobotRecordServiceImpl implements RobotRecordService {
    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Resource
    RobotRecordDao robotRecordDao;

    @Autowired
    private CustomerApi customerApi;


    @Override
    public RobotRecordPageResultResp pageRobotRecordList(RobotRecordPageReq robotRecordPageReq) {
        QueryWrapper wrapper = new QueryWrapper();
        if (StringUtils.isNotEmpty(robotRecordPageReq.getConversationId())) {
            wrapper.eq("conversation_id", robotRecordPageReq.getConversationId());
        }
        if (StringUtils.isNotEmpty(robotRecordPageReq.getMobileNum())) {
            wrapper.eq("mobile_num", robotRecordPageReq.getMobileNum());
        }
        if (StringUtils.isNotEmpty(robotRecordPageReq.getAccount())) {
            wrapper.eq("account", robotRecordPageReq.getAccount());
        }
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        /**
         * 迭代三BUG反馈修改
         * 查看聊天记录时不查账号的创建时间，查对应记录的创建时间
         * @createdTime 2023/2/15 11:37
         */
        if (StringUtils.isNotEmpty(robotRecordPageReq.getStartTime()) && StringUtils.isNotEmpty(robotRecordPageReq.getEndTime())) {
            wrapper.le("create_time", robotRecordPageReq.getEndTime() + " 23:59:59");
            wrapper.ge("create_time", robotRecordPageReq.getStartTime() + " 0:00:00");
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("serial_num");
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(robotRecordPageReq.getPageSize());
        pageParam.setPageNo(robotRecordPageReq.getPageNo());
        PageResult pageResult = robotRecordDao.selectPage(pageParam, wrapper);
        List<RobotRecordResp> robotRecordResps = BeanUtil.copyToList(pageResult.getList(), RobotRecordResp.class);
        return new RobotRecordPageResultResp(robotRecordResps, pageResult.getTotal(), robotRecordPageReq.getPageNo());
    }

    @Override
    public List<SendQuantityResp> queryChannelSendQuantity() {
        List<SendQuantityResp> list = new ArrayList<>();
        // 获取当前CSP名下的用户
        CustomerReq req = new CustomerReq();
        req.setPageNo(-1);
        PageResult<CustomerResp> customerRespPageResult = customerApi.queryList(req);
        List<String> userIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(customerRespPageResult.getList())) {
            for (CustomerResp customerResp :
                    customerRespPageResult.getList()) {
                userIdList.add(customerResp.getUserId());
            }
        }
        // 当前CSP名下无用户时不查询，直接返回空数据
        if (CollectionUtils.isNotEmpty(userIdList)) {
            String tableName = "csp_customer_" + SessionContextUtil.getUser().getUserId();
            list = robotRecordDao.queryChannelSendQuantity(userIdList, tableName);
        }
        return list;
    }

    @Override
    public int saveRobotRecord(RobotRecordReq robotRecordReq) {
        RobotRecordDo robotRecordDo = new RobotRecordDo();
        BeanUtil.copyProperties(robotRecordReq, robotRecordDo);
        robotRecordDo.setCreateTime(new Date());
        return robotRecordDao.insert(robotRecordDo);
    }

    @Override
    public RobotRecordResp saveRobotRecord(RobotRecordSaveReq robotRecordReq) {
        RobotRecordDo robotRecordDo = new RobotRecordDo();
        BeanUtil.copyProperties(robotRecordReq, robotRecordDo);
        robotRecordDo.setAccount(robotRecordReq.getChatbotAccount());
//        log.info("robotRecordReq:{} robotRecordDo:{}", JsonUtils.obj2String(robotRecordReq), JsonUtils.obj2String(robotRecordDo));
        robotRecordDao.insert(robotRecordDo);
        RobotRecordResp resp = new RobotRecordResp();
        BeanUtil.copyProperties(robotRecordDo, resp);
        return resp;
    }

    @Override
    public int updateById(RobotRecordResp robotRecordDo) {
        LambdaUpdateWrapper<RobotRecordDo> update = new LambdaUpdateWrapper<>();
        update.eq(RobotRecordDo::getId, robotRecordDo.getId())
                .set(RobotRecordDo::getMessage, robotRecordDo.getMessage());
        return robotRecordDao.update(null, update);
    }

    @Override
    public PageResult<RobotRecordStatisticResp> getRobotRecordStatisticByTime(RobotRecordPageReq req) {
        log.info("===req===:{}", JsonUtils.obj2String(req));
        QueryWrapper<RobotRecordDo> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(req.getEndTime())) {
            wrapper.le("create_time", req.getEndTime());
        }
        if (StringUtils.isNotEmpty(req.getStartTime())) {
            wrapper.ge("create_time", req.getStartTime());
        }
        wrapper.eq("deleted", 0);
        if (CollectionUtils.isNotEmpty(req.getUserIdList())) {
            wrapper.in("creator", req.getUserIdList());
        }
        wrapper.ne("channel_type", 0);
//        wrapper.eq("send_person", "2");
        wrapper.orderByDesc("create_time");
        wrapper.orderByDesc("serial_num");
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(req.getPageSize());
        pageParam.setPageNo(req.getPageNo());
        PageResult<RobotRecordDo> result = robotRecordDao.selectPage(pageParam, wrapper);
        List<RobotRecordStatisticResp> respList = result.getList().stream().map(dto -> {
                    RobotRecordStatisticResp resp = new RobotRecordStatisticResp();
                    BeanUtils.copyProperties(dto, resp);
                    return resp;
                })
                .collect(Collectors.toList());
        return new PageResult<>(respList, (long) respList.size());
    }
}
