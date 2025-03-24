package com.citc.nce.robot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.robot.Consts.ProcessStatusEnum;
import com.citc.nce.robot.dao.RobotProcessDesDao;
import com.citc.nce.robot.dao.RobotProcessSettingNodeDao;
import com.citc.nce.robot.dao.RobotProcessTriggerNodeDao;
import com.citc.nce.robot.dao.RobotSceneNodeDao;
import com.citc.nce.robot.entity.RobotProcessDesDo;
import com.citc.nce.robot.entity.RobotProcessSettingNodeDo;
import com.citc.nce.robot.entity.RobotSceneNodeDo;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotProcessSettingNodeService;
import com.citc.nce.robot.service.RobotSceneNodeService;
import com.citc.nce.robot.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.exception.GlobalErrorCode.USER_AUTH_ERROR;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 17:28
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorUserIdConfigure.class)
public class RobotSceneNodeServicelmpI implements RobotSceneNodeService {

    @Resource
    private RobotSceneNodeDao robotSceneNodeDao;
    //流程管理
    @Resource
    private RobotProcessSettingNodeDao robotProcessSettingNodeDao;
    //触发器
    @Resource
    private RobotProcessTriggerNodeDao robotProcessTriggerNodeDao;

    @Resource
    private RobotProcessSettingNodeService robotProcessSettingNodeService;
    @Resource
    private RobotProcessDesDao robotProcessDesDao;

    @Resource
    AccountManagementApi accountManagementApi;

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    @Override
    public PageResultResp getRobotSceneNodes(PageParam pageParam) {

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        List<Map<String, Long>> maps = null;
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
            //流程管理数据统计
            maps = robotProcessSettingNodeDao.selectCountBySceneId(userId);
        } else {
            //流程管理数据统计
            maps = robotProcessSettingNodeDao.selectCountBySceneIdAdmin();
        }

        //根据创建时间排序
        wrapper.orderByDesc("create_time");

        PageResult<RobotSceneNodeDo> robotSceneNodeDoPageResult = robotSceneNodeDao.selectPage(pageParam, wrapper);
        List<RobotSceneNodeResp> robotSceneNodeResps = BeanUtil.copyToList(robotSceneNodeDoPageResult.getList(), RobotSceneNodeResp.class);
        //流程管理数据统计
        HashMap<Long, Long> vMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Long> map : maps) {
                Long groupId = map.get("sceneId");
                Long nums = map.get("nums");
                vMap.put(groupId, nums);
            }
        }
        for (RobotSceneNodeResp robotSceneNodeResp : robotSceneNodeResps) {
            if (vMap.get(robotSceneNodeResp.getId()) != null) {
                robotSceneNodeResp.setProcessNum((vMap.get(robotSceneNodeResp.getId()).intValue()));
            } else {
                robotSceneNodeResp.setProcessNum(0);
            }
            String[] accountList = new String[0];
            if (StringUtils.hasText(robotSceneNodeResp.getAccounts())) {
                String accounts = robotSceneNodeResp.getAccounts();
                accountList = accounts.split(";");
            }
            String accountNames = "";
            String accounts = "";
            List list = new ArrayList();
            if (accountList.length > 0) {
                List<AccountManagementResp> accountManagementlist = accountManagementApi.getAccountManagementlist(userId);
                //账户信息map
                HashMap<String, AccountManagementResp> accountMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(accountManagementlist)) {
                    for (AccountManagementResp accountManagementResp : accountManagementlist) {
                        accountMap.put(accountManagementResp.getChatbotAccount(), accountManagementResp);
                    }
                }
                for (String accountId : accountList) {
                    try {
                        Map<String, String> map = new HashMap<>();
                        if (accountMap.get(accountId) != null) {
                            AccountManagementResp accountManagementResp = accountMap.get(accountId);
                            //1删除  0正常
                            accounts += accountId + ";";
                            String accountName = accountManagementResp.getAccountName();
                            accountNames = accountNames + ";" + accountName;
                            map.put("accountId", accountId);
                            map.put("accountName", accountName);
                            map.put("accountType", accountManagementResp.getAccountType());
                            list.add(map);
                        }
                    } catch (Exception e) {
                        log.error("账号查询名称异常", e, e.getMessage());
                    }
                }
                if (StringUtils.hasText(accounts) && accounts.length() > 1) {
                    accounts = accounts.substring(0, accounts.length() - 1);
                }
                robotSceneNodeResp.setAccounts(accounts);
            }

            robotSceneNodeResp.setAccountInfo(list);
            robotSceneNodeResp.setAccountNum(list.size());
            robotSceneNodeResp.setAccountNames(accountNames);
        }
        return new PageResultResp(robotSceneNodeResps, robotSceneNodeDoPageResult.getTotal(), pageParam.getPageNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveRobotSceneNode(RobotSceneNodeReq robotSceneNodeReq) {
        QueryWrapper<RobotSceneNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("scene_name", robotSceneNodeReq.getSceneName());
        String userId = SessionContextUtil.getUser().getUserId();
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNodeDao.selectList(wrapper);
        if (CollectionUtils.isNotEmpty(robotSceneNodeDos)) {
            throw new BizException(RobotErrorCode.SceneNodeDATA_EXIST);
        }
        RobotSceneNodeDo robotSceneNodeDo = new RobotSceneNodeDo();
        BeanUtil.copyProperties(robotSceneNodeReq, robotSceneNodeDo);
        robotSceneNodeDao.insert(robotSceneNodeDo);
        return robotSceneNodeDo.getId();
    }

    @Override
    @Transactional
    public int updateRobotSceneNode(RobotSceneNodeEditReq req) {
        String userId = SessionContextUtil.getUser().getUserId();
        RobotSceneNodeDo dbEntity = robotSceneNodeDao.selectById(req.getId());
        if (Objects.nonNull(dbEntity) && userId.equals(dbEntity.getCreator())) {
            //判断场景名称是否重复
            if (robotSceneNodeDao.exists(new QueryWrapper<RobotSceneNodeDo>()
                    .eq("deleted", 0)
                    .eq("scene_name", req.getSceneName())
                    .eq("creator", userId)
                    .ne("id", req.getId()))) {
                throw new BizException(RobotErrorCode.SceneNodeDATA_EXIST);
            }
            //关联账号发生变化，记录账号变化时间
            if (!dbEntity.getAccounts().equals(req.getAccounts())) {
                dbEntity.setAccountChangeTime(DateTime.now());
                if (!changeSceneProcessForAccountChange(dbEntity.getId(), req.getAccounts())) {
                    throw new BizException(RobotErrorCode.PROCESS_APPENDING_ERROR);
                }
            }
            //设置新数据
            dbEntity.setSceneName(req.getSceneName());
            dbEntity.setSceneValue(req.getSceneValue());
            dbEntity.setAccounts(req.getAccounts());
            return robotSceneNodeDao.updateById(dbEntity);
        } else {
            throw new BizException(USER_AUTH_ERROR);
        }
    }

    @Override
    public void removeChatbotAccount(String chatbotAccount) {
        String userId = SessionContextUtil.getUser().getUserId();
        QueryWrapper<RobotSceneNodeDo> queryWrapper=new QueryWrapper<>();
        boolean isSuperAdmin = com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator());
        if(isSuperAdmin) {
            queryWrapper.likeRight("creator", userId);
        }
        queryWrapper.like("accounts",chatbotAccount);
        queryWrapper.eq("deleted", 0);
       List<RobotSceneNodeDo> robotSceneNodeDos= robotSceneNodeDao.selectList(queryWrapper);

       if(ObjectUtil.isEmpty(robotSceneNodeDos))
       { log.info("取消关联场景未找到记录,accounts："+chatbotAccount);
           return;}
       Date date=DateTime.now();
       for (int i = 0; i < robotSceneNodeDos.size(); i++) {
            RobotSceneNodeDo robotSceneNodeDo=robotSceneNodeDos.get(i);
            List<String> accounts=Arrays.asList(robotSceneNodeDo.getAccounts().split(";"));
            accounts= accounts.stream().filter(item->!item.equals(chatbotAccount)).collect(Collectors.toList());
            robotSceneNodeDo.setAccounts(Strings.join(accounts).with(";"));
            robotSceneNodeDo.setAccountChangeTime(date);
        }
        robotSceneNodeDao.updateBatch(robotSceneNodeDos);
    }

    /**
     * 场景关联账号发生变化，流程重置为待发布状态 发布中不能修改关联账号
     */
    boolean changeSceneProcessForAccountChange(Long sceneId, String newAccounts) {
        QueryWrapper<RobotProcessSettingNodeDo> robotProcessSettingNodeDoQueryWrapper = new QueryWrapper<>();
        robotProcessSettingNodeDoQueryWrapper.eq("scene_id", sceneId);
        robotProcessSettingNodeDoQueryWrapper.eq("deleted", 0);
        List<RobotProcessSettingNodeDo> robotProcessSettingNodeDos = robotProcessSettingNodeDao.selectList(robotProcessSettingNodeDoQueryWrapper);
        if (ObjectUtil.isEmpty(robotProcessSettingNodeDos))
            return true;
        String[] newAccountsArray = newAccounts.split(";");
        for (RobotProcessSettingNodeDo item : robotProcessSettingNodeDos) {
            String oldAccounts = item.getAccounts();
            //表示只是删减了账号，流程状态不更改
            if (StrUtil.isNotEmpty(oldAccounts) && Arrays.stream(newAccountsArray).filter(accountItem -> oldAccounts.contains(accountItem)).count() == newAccountsArray.length)
                continue;
            //审核中不能修改关联账号
            if (item.getStatus() == ProcessStatusEnum.Appending.getCode())
                return false;
            else if (item.getStatus() == ProcessStatusEnum.Success.getCode() || item.getStatus() == ProcessStatusEnum.Updated.getCode()) {
                updateRobotProcessSettingNodeStatusById(item, ProcessStatusEnum.Updated.getCode());
            } else
                updateRobotProcessSettingNodeStatusById(item, ProcessStatusEnum.Waiting.getCode());
        }
        return true;
    }

    void updateRobotProcessSettingNodeStatusById(RobotProcessSettingNodeDo robotProcessSettingNodeDo, int status) {
        QueryWrapper<RobotProcessDesDo> robotProcessDesDoQueryWrapper = new QueryWrapper<>();
        robotProcessSettingNodeDo.setStatus(status);
        robotProcessSettingNodeDo.setAuditResult("");
        robotProcessSettingNodeDao.updateById(robotProcessSettingNodeDo);
        //更新流程配置
        robotProcessDesDoQueryWrapper.eq("deleted", 0);
        //不能更新已发布成功的记录，更新其他状态记录
        robotProcessDesDoQueryWrapper.ne("release_type", ProcessStatusEnum.Success.getCode());
        robotProcessDesDoQueryWrapper.in("process_id", robotProcessSettingNodeDo.getId());
        List<RobotProcessDesDo> robotProcessDesDos = robotProcessDesDao.selectList(robotProcessDesDoQueryWrapper);
        for (RobotProcessDesDo processDesDo :
                robotProcessDesDos) {
            processDesDo.setReleaseType(status);
            robotProcessDesDao.updateById(processDesDo);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delRobotSceneNodeById(Long id) {
        try {
            RobotSceneNodeDo robotSceneNodeDo = robotSceneNodeDao.selectById(id);
            String userId = SessionContextUtil.getUser().getUserId();
            if (userId.equals(robotSceneNodeDo.getCreator())) {
                //当前场景存在发布中的流程,不可删除
                LambdaQueryWrapper<RobotProcessSettingNodeDo> robotProcessSettingNodeDoQueryWrapper = new LambdaQueryWrapper<>();
                robotProcessSettingNodeDoQueryWrapper.eq(RobotProcessSettingNodeDo::getSceneId, id);
                robotProcessSettingNodeDoQueryWrapper.eq(RobotProcessSettingNodeDo::getDeleted, 0);
                robotProcessSettingNodeDoQueryWrapper.eq(RobotProcessSettingNodeDo::getStatus, 0);
                if (robotProcessSettingNodeDao.exists(robotProcessSettingNodeDoQueryWrapper)) {
                    throw new BizException("当前场景存在发布中的流程,不可删除");
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("deleted", 1);
                map.put("deleteTime", DateUtil.date());
                //删除场景对应的流程
                int update = robotProcessSettingNodeDao.delRobotProcessSettingNode(map);

                //删除场景管理对应触发器
                int updates = robotProcessTriggerNodeDao.delRobotProcessTriggerNode(map);
                //删除场景
                return robotSceneNodeDao.delRobotSceneNode(map);
            } else {
                throw new BizException(USER_AUTH_ERROR);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RobotSceneNodeResp> getRobotSceneNodeList() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }

        //根据创建时间排序
        wrapper.orderByDesc("create_time");
        List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNodeDao.selectList(wrapper);
        List<RobotSceneNodeResp> robotSceneNodeResps = BeanUtil.copyToList(robotSceneNodeDos, RobotSceneNodeResp.class);
        return robotSceneNodeResps;
    }

    @Override
    public List<RobotSceneNodeTreeResp> getRobotSceneNodeListTree() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }

        //根据创建时间排序
        wrapper.orderByDesc("create_time");
        List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNodeDao.selectList(wrapper);
        List<RobotSceneNodeTreeResp> robotSceneNodeResps = BeanUtil.copyToList(robotSceneNodeDos, RobotSceneNodeTreeResp.class);

        //流程管理数据归类
        List<RobotProcessSettingNodeResp> robotProcessSettingNodes = robotProcessSettingNodeService.getRobotProcessSettingNodes();
        HashMap<Long, List<RobotProcessSettingNodeTreeResp>> robotProcessSettingNodeMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(robotProcessSettingNodes)) {
            List<RobotProcessSettingNodeTreeResp> robotProcessSettingNodeTreeResps = BeanUtil.copyToList(robotProcessSettingNodes, RobotProcessSettingNodeTreeResp.class);
            for (RobotProcessSettingNodeTreeResp robotProcessSettingNode : robotProcessSettingNodeTreeResps) {
                if (robotProcessSettingNodeMap.get(robotProcessSettingNode.getSceneId()) == null) {
                    ArrayList<RobotProcessSettingNodeTreeResp> robotProcessSettingNodeResps = new ArrayList<>();
                    robotProcessSettingNodeResps.add(robotProcessSettingNode);
                    robotProcessSettingNodeMap.put(robotProcessSettingNode.getSceneId(), robotProcessSettingNodeResps);
                } else {
                    robotProcessSettingNodeMap.get(robotProcessSettingNode.getSceneId()).add(robotProcessSettingNode);
                }
            }
        }
        for (RobotSceneNodeTreeResp robotSceneNodeResp : robotSceneNodeResps) {
            if (robotProcessSettingNodeMap.get(robotSceneNodeResp.getId()) != null) {
                robotSceneNodeResp.setRobotProcessSettingNodes(robotProcessSettingNodeMap.get(robotSceneNodeResp.getId()));
            }
        }
        return robotSceneNodeResps;
    }

    @Override
    public RobotSceneNodeResp getRobotSceneNodeById(Long id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("id", id);

        RobotSceneNodeDo robotSceneNodeDo = robotSceneNodeDao.selectOne(wrapper);
        if (robotSceneNodeDo == null) {
            return null;
        }
        RobotSceneNodeResp robotSceneNodeResp = BeanUtil.copyProperties(robotSceneNodeDo, RobotSceneNodeResp.class);
        String[] accountList = new String[0];
        if (StringUtils.hasText(robotSceneNodeResp.getAccounts())) {
            String accounts = robotSceneNodeResp.getAccounts();
            accountList = accounts.split(";");
            robotSceneNodeResp.setAccountNum(accountList.length);
        }
        String accountNames = "";
        List<Map<String, String>> list = new ArrayList<>();
        for (String accountId : accountList) {
            Map<String, String> map = new HashMap<>();
            AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(accountId);
            if (Objects.nonNull(accountManagement) && StringUtils.hasText(accountManagement.getChatbotAccount()) && accountManagement.getDeleted() == 0) {
                String accountName = accountManagement.getAccountName();
                accountNames = accountNames + ";" + accountName;
                map.put("accountId", accountManagement.getChatbotAccount());
                map.put("accountName", accountName);
                map.put("accountType", accountManagement.getAccountType());
                robotSceneNodeResp.setAccountNames(accountNames);
                list.add(map);
            }
        }
        robotSceneNodeResp.setAccountInfo(list);
        return robotSceneNodeResp;
    }

    @Override
    public boolean checkAccountId(String id) {
        log.info("接收到的id为{}", id);
        QueryWrapper<RobotSceneNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        String userId = SessionContextUtil.getUser().getUserId();
        if (!com.baomidou.mybatisplus.core.toolkit.StringUtils.equals(userId, superAdministratorUserIdConfigure.getSuperAdministrator())) {
            wrapper.eq("creator", userId);
        }
        id = id.replace("\"", "");
        wrapper.like("accounts", id);
        List<RobotSceneNodeDo> robotSceneNodeDos = robotSceneNodeDao.selectList(wrapper);
        log.info("查询到的数据为{}", robotSceneNodeDos);
        if (CollectionUtils.isNotEmpty(robotSceneNodeDos)) {
            for (RobotSceneNodeDo robotSceneNodeDo : robotSceneNodeDos) {
                String accounts = robotSceneNodeDo.getAccounts();
                String[] split = accounts.split(";");
                for (int i = 0; i < split.length; i++) {
                    if (split[i].equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RobotSceneNodeResp clearChatbotWhenSceneOpen(RobotSceneNodeResp req) {
        String oldAccounts = req.getAccounts();
        String[] accountList = new String[0];
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(oldAccounts)) {
            accountList = oldAccounts.split(";");
        }

        // 先判定场景是否有绑定机器人
        if (accountList.length == 0) {
            // 如果机器人是空就直接返回
            return req;
        }
        boolean needUpdate = false;
        String creator = SessionContextUtil.getUser().getUserId();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(req.getCreator())) {
            creator = req.getCreator();
        }

        StringBuilder accountNames = new StringBuilder();
        StringBuilder accounts = new StringBuilder();
        List list = new ArrayList();
        // 通过账号去查对应的机器人
        List<AccountManagementResp> accountManagementList = accountManagementApi.getAccountManagementlist(creator);
        //账户信息map
        HashMap<String, AccountManagementResp> accountMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(accountManagementList)) {
            for (AccountManagementResp accountManagementResp : accountManagementList) {
                // 硬核桃一直可用
                if (0 == accountManagementResp.getAccountTypeCode()) {
                    accountMap.put(accountManagementResp.getChatbotAccount(), accountManagementResp);
                    continue;
                }
                // 如果机器人的状态是下线
                if (42 == accountManagementResp.getChatbotStatus() || 31 == accountManagementResp.getChatbotStatus()) {
                    // 不放入数据
                    needUpdate = true;
                } else {
                    accountMap.put(accountManagementResp.getChatbotAccount(), accountManagementResp);
                }
            }
        }
        // 不需更新则时直接返回
        if (!needUpdate) {
            return req;
        }
        for (String accountId : accountList) {
            try {
                Map<String, String> map = new HashMap<>();
                if (accountMap.get(accountId) != null) {
                    AccountManagementResp accountManagementResp = accountMap.get(accountId);
                    //1删除  0正常
                    accounts.append(accountId).append(";");
                    String accountName = accountManagementResp.getAccountName();
                    accountNames.append(";").append(accountName);
                    map.put("accountId", accountId);
                    map.put("accountName", accountName);
                    map.put("accountType", accountManagementResp.getAccountType());
                    list.add(map);
                }
            } catch (Exception e) {
                log.error("账号查询名称异常", e, e.getMessage());
            }
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(accounts)) {
            accounts = new StringBuilder(accounts.substring(0, accounts.length() - 1));
        }
        // 新旧数据不一致时才更新
        if (!org.apache.commons.lang3.StringUtils.equals(oldAccounts, accounts)) {
            req.setAccounts(accounts.toString());
            req.setAccountInfo(list);
            req.setAccountNum(list.size());
            req.setAccountNames(accountNames.toString());
            RobotSceneNodeDo robotSceneNodeDo = BeanUtil.copyProperties(req, RobotSceneNodeDo.class);
            robotSceneNodeDao.updateById(robotSceneNodeDo);
        }
        return req;
    }

    @Override
    public RobotSceneNodeResp queryByName(String sceneName) {
        QueryWrapper<RobotSceneNodeDo> wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("scene_name", sceneName);
        wrapper.eq("creator", SessionContextUtil.getUser().getUserId());
        wrapper.orderByDesc("id");
        wrapper.last("limit 1");
        RobotSceneNodeDo robotSceneNodeDo = robotSceneNodeDao.selectOne(wrapper);
        RobotSceneNodeResp sceneNodeResp = BeanUtil.copyProperties(robotSceneNodeDo, RobotSceneNodeResp.class);
        return sceneNodeResp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long removeBandingChatbotByChatbotAccount(String chatbotAccount) {
        List<RobotSceneNodeDo> sceneList = robotSceneNodeDao.selectList(Wrappers.<RobotSceneNodeDo>lambdaQuery().like(RobotSceneNodeDo::getAccounts, chatbotAccount));
        if (sceneList.isEmpty()) return 0L;
        Date now = new Date();
        for (RobotSceneNodeDo scene : sceneList) {
            List<String> relatedChatbotAccountList = new ArrayList<>(Arrays.asList(scene.getAccounts().split(";")));
            relatedChatbotAccountList.remove(chatbotAccount);
            scene.setAccounts(String.join(";", relatedChatbotAccountList));
            scene.setAccountChangeTime(now);
        }
        return (long) robotSceneNodeDao.updateBatch(sceneList);
    }
}
