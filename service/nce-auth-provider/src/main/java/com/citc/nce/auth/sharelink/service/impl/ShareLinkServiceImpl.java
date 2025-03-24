package com.citc.nce.auth.sharelink.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.accountmanagement.dao.AccountManagementDao;
import com.citc.nce.auth.accountmanagement.entity.AccountManagementDo;
import com.citc.nce.auth.configure.AccountUrlConfigure;
import com.citc.nce.auth.configure.SuperAdministratorUserIdConfigure;
import com.citc.nce.auth.sharelink.dao.ShareLinkDao;
import com.citc.nce.auth.sharelink.entity.ShareLinkDo;
import com.citc.nce.auth.sharelink.exception.ShareLinkCode;
import com.citc.nce.auth.sharelink.service.ShareLinkService;
import com.citc.nce.auth.sharelink.vo.*;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 16:32
 * @Version: 1.0
 * @Description:
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({SuperAdministratorUserIdConfigure.class, AccountUrlConfigure.class})
public class ShareLinkServiceImpl implements ShareLinkService {

    private final SuperAdministratorUserIdConfigure superAdministratorUserIdConfigure;

    private final AccountUrlConfigure accountUrlConfigure;
    @Resource
    private ShareLinkDao shareLinkDao;

    @Resource
    private AccountManagementDao accountManagementDao;



    @Override
    public PageResultResp getShareLinks(ShareLinkPageReq shareLinkPageReq) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("deleted", 0);
        wrapper.eq("chatbot_account_id", shareLinkPageReq.getChatbotAccountId());
        String userId = SessionContextUtil.getUser().getUserId();
        if(!StringUtils.equals(userId,superAdministratorUserIdConfigure.getSuperAdministrator())){
            wrapper.eq("creator", userId);
        }
        wrapper.orderByDesc("create_time");
        PageParam pageParam = new PageParam();
        pageParam.setPageSize(shareLinkPageReq.getPageSize());
        pageParam.setPageNo(shareLinkPageReq.getPageNo());
        PageResult<ShareLinkDo> shareLinkDoPageResult = shareLinkDao.selectPage(pageParam, wrapper);
        List<ShareLinkResp> shareLinkResps = BeanUtil.copyToList(shareLinkDoPageResult.getList(), ShareLinkResp.class);
        return new PageResultResp(shareLinkResps,shareLinkDoPageResult.getTotal(),shareLinkPageReq.getPageNo(),accountUrlConfigure.getShareLinkInfo());
    }

    @Override
    public int saveShareLink(ShareLinkReq shareLinkReq) {
        AccountManagementDo account = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, shareLinkReq.getChatbotAccountId());
        if (Objects.isNull(account)){
            throw new BizException("机器人不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(account.getCustomerId())){
            throw new BizException("不能给其他人机器人创建分享链接");
        }
        List<ShareLinkDo> shareLinkDos = shareLinkDao.selectList(Wrappers.<ShareLinkDo>lambdaQuery()
                .eq(ShareLinkDo::getDeleted, 0)
                .eq(ShareLinkDo::getLinkName, shareLinkReq.getLinkName())
                .eq(ShareLinkDo::getChatbotAccountId, shareLinkReq.getChatbotAccountId())
        );
        if(CollectionUtils.isNotEmpty(shareLinkDos)){
            throw new BizException(ShareLinkCode.VARIABLE_BAD_SHARELINK_NAME);
        }
        ShareLinkDo shareLinkDo = new ShareLinkDo();
        BeanUtil.copyProperties(shareLinkReq, shareLinkDo);
        shareLinkDo.setLinkInfo(RandomUtil.randomString(10));
        return shareLinkDao.insert(shareLinkDo);
    }

    @Override
    public int delShareLinkById(ShareLinkOneReq shareLinkOneReq) {
        ShareLinkDo shareLinkDo = shareLinkDao.selectById(shareLinkOneReq.getId());
        if (Objects.isNull(shareLinkDo)){
            throw new BizException("链接不存在");
        }
        if (!SessionContextUtil.getUser().getUserId().equals(shareLinkDo.getCreator())){
            throw new BizException("不能其他人创建分享链接");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", shareLinkOneReq.getId());
        map.put("deleted", 1);
        map.put("deleteTime", DateUtil.date());
        return shareLinkDao.delShareLinkById(map);
    }

    @Override
    public ShareLinkResp getShareLinkById(ShareLinkOneReq shareLinkOneReq) {
        ShareLinkDo shareLinkDo = shareLinkDao.selectOne(Wrappers.<ShareLinkDo>lambdaQuery()
                .eq(ShareLinkDo::getId, shareLinkOneReq.getId()));
        ShareLinkResp ShareLinkResp = BeanUtil.copyProperties(shareLinkDo, ShareLinkResp.class);
        return ShareLinkResp;
    }

    @Override
    public ShareLinkH5Resp checkShareLinkByLinkInfo(String linkInfo) {
        List<ShareLinkDo> shareLinkDos = shareLinkDao.selectList((Wrappers.<ShareLinkDo>lambdaQuery()
                .eq(ShareLinkDo::getDeleted, 0)
                .eq(ShareLinkDo::getLinkInfo, linkInfo)));
        ShareLinkH5Resp shareLinkH5Resp = new ShareLinkH5Resp();
        if(CollectionUtils.isNotEmpty(shareLinkDos)){
            ShareLinkDo shareLinkDo = shareLinkDos.get(0);
            AccountManagementDo accountManagementDo = accountManagementDao.selectOne(AccountManagementDo::getChatbotAccountId, shareLinkDo.getChatbotAccountId());
            if(accountManagementDo != null){
                shareLinkH5Resp.setChatbotAccountId(accountManagementDo.getChatbotAccountId());
                shareLinkH5Resp.setAccountName(accountManagementDo.getAccountName());
                shareLinkH5Resp.setChatbotAccount(accountManagementDo.getChatbotAccount());
            }
            shareLinkH5Resp.setChatbotAccountId(shareLinkDo.getChatbotAccountId());
            shareLinkH5Resp.setCheckStatus(true);
        }else{
            shareLinkH5Resp.setCheckStatus(false);
        }
        return shareLinkH5Resp;
    }

}
