package com.citc.nce.auth.csp.account.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.configure.ChatBotMobileConfigure;
import com.citc.nce.auth.configure.CspUnicomAndTelecomConfigure;
import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.auth.csp.account.dao.AccountManageDao;
import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.account.service.AccountManageService;
import com.citc.nce.auth.csp.account.vo.*;
import com.citc.nce.auth.csp.agent.dao.AgentDao;
import com.citc.nce.auth.csp.agent.entity.AgentDo;
import com.citc.nce.auth.csp.csp.service.CspService;
import com.citc.nce.auth.utils.AuthUtils;
import com.citc.nce.auth.utils.DateUtil;
import com.citc.nce.auth.utils.SHA256Utils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.validation.ValidationUtils;
import com.citc.nce.conf.CommonKeyPairConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @BelongsPackage: com.citc.nce.auth.csp.account.service.impl
 * @Author: litao
 * @CreateTime: 2023-02-14  15:11
 * @Version: 1.0
 */
@Service
@Slf4j
public class AccountManageServiceImpl extends ServiceImpl<AccountManageDao, AccountManageDo> implements AccountManageService {
    @Resource
    private AccountManageDao accountManageDao;
    @Resource
    private AgentDao agentDao;
    @Resource
    private CspService cspService;

    @Autowired
    private CspUnicomAndTelecomConfigure unTelConfigure;

    @Resource
    private RedisService redisService;
    @Resource
    private ChatBotMobileConfigure mobileConfigure;

    @Autowired
    private CommonKeyPairConfig commonKeyPairConfig;

    @Override
    public PageResult<AccountResp> queryList(AccountPageReq req) {
        PageResult<AccountResp> pageResult = new PageResult<>();
        Page<AccountManageDo> page = new Page<>(req.getPageNo(), req.getPageSize());
        //根据cspid查询csp的账号
        LambdaQueryWrapper<AccountManageDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountManageDo::getCspId, cspService.obtainCspId(SessionContextUtil.getUser().getUserId()));
        queryWrapper.isNotNull(AccountManageDo::getOperatorCode);
        page.setOrders(OrderItem.ascs("operator_code"));
        Page<AccountManageDo> result = accountManageDao.selectPage(page, queryWrapper);
        List<AccountResp> list = new ArrayList<>();
        pageResult.setTotal(0L);
        pageResult.setList(list);
        Key privateKey = RsaUtil.getPrivateKey(commonKeyPairConfig.getPrivateKey());
        if (CollectionUtils.isNotEmpty(result.getRecords())) {
            result.getRecords().forEach(item -> {
                AccountResp resp = new AccountResp();
                BeanUtil.copyProperties(item, resp);
                if (StringUtils.isNotEmpty(resp.getCspPassword()))
                    resp.setCspPassword(RsaUtil.decryptByAsymmetric(resp.getCspPassword(), privateKey));
                list.add(resp);
            });
            pageResult.setTotal(result.getTotal());
            pageResult.setList(list);
        }
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(AccountSaveReq req) {
        boolean update = req.getChatbotCspAccountId() != null;
        ValidationUtils.validate(req, update ? AccountSaveReq.Edit.class : AccountSaveReq.Add.class);
        AccountManageDo cspAccount;
        String cspId = req.isBoss() ? cspService.obtainCspId(req.getUserId()) : cspService.obtainCspId(SessionContextUtil.getUserId());
        Key publicKey = RsaUtil.getPublicKey(commonKeyPairConfig.getPublicKey());
        if (!update) {
            //新增csp
            cspAccount = new AccountManageDo();
            BeanUtil.copyProperties(req, cspAccount);
            //获取当前csp的cspId
            cspAccount.setCspId(cspId);
            //设置cspAccountId
            cspAccount.setChatbotCspAccountId("CCA" + DateUtil.dateToString(new Date(), "MMddHHmmss") + (new Random().nextInt(9000000) + 1000000));
            //设置运营商账号ID
            cspAccount.setOperatorAccountId("OPA" + AuthUtils.randomID(10));
            if (req.getOperatorCode() == 2) {
                //移动的appId=CspAccount
                cspAccount.setAppId(req.getCspAccount());
                try {
                    RsaUtil.RsaKeyPair rsaKeyPair = RsaUtil.generateKeyPair();
                    cspAccount.setPrivateKey(rsaKeyPair.getPrivateKey());
                    cspAccount.setPublicKey(rsaKeyPair.getPublicKey());
                    cspAccount.setToken(SHA256Utils.getSHA256(cspAccount.getCspPassword()));
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("创建秘钥失败");
                }
            } else {
                //电信联通
                cspAccount.setMsgCallbackUrl(unTelConfigure.getGatewayCallbackUrl());
                if (StringUtils.isEmpty(cspAccount.getToken()))
                    throw new BizException("token不能为空");
            }
            //csp秘钥加密存储
            cspAccount.setCspPassword(RsaUtil.encryptByAsymmetric(cspAccount.getCspPassword(), publicKey));
            accountManageDao.insert(cspAccount);
        } else {
            //修改账号
            cspAccount = accountManageDao.selectOne(
                    Wrappers.<AccountManageDo>lambdaQuery()
                            .eq(AccountManageDo::getChatbotCspAccountId, req.getChatbotCspAccountId())
                            .eq(AccountManageDo::getCspId, cspId)
            );
            if (Objects.isNull(cspAccount)) {
                throw new BizException(500, "csp账号不存在");
            }
            //电信联通删除redis——token
            if (cspAccount.getOperatorCode() != 2) {
                String tokenKey = "ISP:accessToken:" + req.getOperatorCode() + "_" + cspAccount.getCspAccount();
                redisService.deleteObject(tokenKey);
            }
            BeanUtils.copyProperties(req, cspAccount);
            //移动代理商数据，token
            if (req.getOperatorCode() == 2) {
                cspAccount.setToken(SHA256Utils.getSHA256(cspAccount.getCspPassword()));
                agentDao.delete(new LambdaQueryWrapper<AgentDo>().eq(AgentDo::getCspAccountManageId, cspAccount.getId()));
            }
            cspAccount.setCspPassword(RsaUtil.encryptByAsymmetric(cspAccount.getCspPassword(), publicKey));
            accountManageDao.updateById(cspAccount);
        }
        //添加移动代理商信息
        if (CollectionUtil.isNotEmpty(req.getAgentInfoList())) {
            return agentDao.insertBatch(req.getAgentInfoList().stream().map(item -> {
                AgentDo agentDo = new AgentDo();
                BeanUtil.copyProperties(item, agentDo);
                agentDo.setCspAccountManageId(cspAccount.getId());
                agentDo.setOperatorAccountId(cspAccount.getOperatorAccountId());
                if (StringUtils.isEmpty(agentDo.getAgentName())) {
                    agentDo.setAgentName("");
                }
                return agentDo;
            }).collect(Collectors.toList()));
        }
        return 0;
    }

    @Override
    public AccountDetailResp getDetailByCspAccountId(AccountReq req) {
        AccountDetailResp resp = new AccountDetailResp();
        //根据cspId和cspAccountId查询csp账号信息
        List<AccountManageDo> accountManageDos = accountManageDao.selectList(new LambdaQueryWrapper<AccountManageDo>()
                .eq(AccountManageDo::getCspId, cspService.obtainCspId(SessionContextUtil.getUser().getUserId()))
                .eq(AccountManageDo::getChatbotCspAccountId, req.getChatbotCspAccountId()));

        if (CollectionUtils.isNotEmpty(accountManageDos)) {
            AccountManageDo accountManageDo = accountManageDos.get(0);
            BeanUtil.copyProperties(accountManageDo, resp);
            if (Integer.valueOf(2).equals(resp.getOperatorCode())) {
                List<AgentDo> agentDoList = agentDao.selectList("csp_account_manage_id", accountManageDo.getId());
                List<AccountAgentInfo> accountAgentInfoList = new ArrayList<>();
                agentDoList.forEach(item -> {
                    AccountAgentInfo accountAgentInfo = new AccountAgentInfo();
                    BeanUtil.copyProperties(item, accountAgentInfo);
                    accountAgentInfoList.add(accountAgentInfo);
                });
                resp.setAgentInfoList(accountAgentInfoList);
                resp.setMsgCallbackUrl(mobileConfigure.getMsgCallbackUrl());
                resp.setMediaCallbackUrl(mobileConfigure.getMediaCallbackUrl());
                resp.setDataSyncUrl(mobileConfigure.getDataSyncUrl());
                resp.setIpAddress(mobileConfigure.getIpAddress());
                resp.setPrivateKey("***");
            }
            Key privateKey = RsaUtil.getPrivateKey(commonKeyPairConfig.getPrivateKey());
            resp.setCspPassword(RsaUtil.decryptByAsymmetric(resp.getCspPassword(), privateKey));
            return resp;
        }
        return null;
    }

    @Override
    public List<AgentResp> queryAgentList() {
        //通过cspid查询
        return agentDao.queryAgentList(cspService.obtainCspId(SessionContextUtil.getUser().getUserId()));
    }

    @Override
    public void queryByUserId() {
        BaseUser baseUser = SessionContextUtil.getUser();
        String cspId = cspService.obtainCspId(baseUser.getUserId());
        QueryWrapper<AccountManageDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("csp_id", cspId);
        queryWrapper.eq("operator_code", 2);
        if (!accountManageDao.exists(queryWrapper)) {
            throw BizException.build(AuthError.ADD_CONTRACT_FAIL);
        }
    }

    /**
     * 根据cspId和运营商类型查询第三方csp信息
     *
     * @param operatorCode 运营商类型 (数字)
     * @param cspId        用户csp id
     * @return 第三方csp账号信息
     * @throws BizException 当未查询到可用csp账号时抛出此异常
     */
    @Override
    public AccountManageDo getCspAccount(Integer operatorCode, String cspId) {
        AccountManageDo accountManageDo = accountManageDao.selectOne(
                Wrappers.<AccountManageDo>lambdaQuery().
                        eq(AccountManageDo::getCspId, cspId)
                        .eq(AccountManageDo::getOperatorCode, operatorCode)
        );
        if (ObjectUtil.isEmpty(accountManageDo)) {
            throw new BizException(820103020, "未查询到可用csp账号");
        }
        Key privateKey = RsaUtil.getPrivateKey(commonKeyPairConfig.getPrivateKey());
        accountManageDo.setCspPassword(RsaUtil.decryptByAsymmetric(accountManageDo.getCspPassword(), privateKey));
        return accountManageDo;
    }
}
