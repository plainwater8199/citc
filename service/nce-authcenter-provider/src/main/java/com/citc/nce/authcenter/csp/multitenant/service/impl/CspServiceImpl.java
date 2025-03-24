package com.citc.nce.authcenter.csp.multitenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.constant.CspChannelEnum;
import com.citc.nce.authcenter.csp.customer.dao.CheckCspCustomerMapper;
import com.citc.nce.authcenter.csp.multitenant.dao.CspMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.multitenant.utils.CspUtils;
import com.citc.nce.authcenter.csp.vo.CspAgentLoginReq;
import com.citc.nce.authcenter.csp.vo.CspInfoPage;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspMealCspInfo;
import com.citc.nce.authcenter.identification.entity.UserPlatformPermissionsDo;
import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.tenantdata.user.dao.Csp1Dao;
import com.citc.nce.authcenter.tenantdata.user.dao.CspChannelDao;
import com.citc.nce.authcenter.tenantdata.user.entity.CspChannelDo;
import com.citc.nce.authcenter.tenantdata.user.entity.CspDo;
import com.citc.nce.authcenter.user.entity.UserDo;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.authcenter.utils.AuthUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author jiancheng
 */
@Service
@Slf4j
public class CspServiceImpl extends ServiceImpl<CspMapper, Csp> implements CspService {
    @Resource
    private Csp1Dao csp1Dao;
    @Resource
    private UserService userService;

    @Resource
    private CheckCspCustomerMapper checkCspCustomerMapper;

    @Resource
    private IdentificationService identificationService;

    @Resource
    private CspChannelDao cspChannelDao;


    /**
     * 创建csp，为csp分表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCsp(String userId) {
        LambdaQueryWrapper<CspDo> cspQueryWrapper = new LambdaQueryWrapper<>();
        cspQueryWrapper.eq(CspDo::getCspId, userId);
        List<CspDo> cspDos = csp1Dao.selectList(cspQueryWrapper);
        if (CollectionUtils.isEmpty(cspDos)) {
            Csp csp = new Csp()
                    .setCspId(createCspId(userId))
                    .setCspActive(true)
                    .setUserId(userId);
            this.save(csp);
        }
    }

    /**
     * 创建csp_channel
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCspChannel(String userId) {
        LambdaQueryWrapper<CspChannelDo> cspChannelQueryWrapper = new LambdaQueryWrapper<>();
        cspChannelQueryWrapper.eq(CspChannelDo::getUserId, userId);
        CspChannelDo cspChannelDo = cspChannelDao.selectOne(cspChannelQueryWrapper);
        if (cspChannelDo == null) {
            cspChannelDo = new CspChannelDo();
            cspChannelDo.setUserId(userId);
            cspChannelDo.setMobileChannel(CspChannelEnum.DIRECT.getValue()); // 默认都是直连
            cspChannelDo.setTelecomChannel(CspChannelEnum.DIRECT.getValue());
            cspChannelDo.setUnicomChannel(CspChannelEnum.DIRECT.getValue());
            cspChannelDao.insert(cspChannelDo);
        }
    }

    private String createCspId(String userId) {
        if (userId.length() == 10) {
            return userId;
        } else {
            String uuid = AuthUtils.randomID(10);
            UserDo userDo = userService.findByUserId(uuid);
            if (userDo == null) {
                return uuid;
            } else {
                return createCspId("");
            }
        }
    }

    @Override
    public String getCspLoginAddress() {
        BaseUser user = SessionContextUtil.getUser();
        Csp csp = this.getByUserId(user.getUserId())
                .orElseThrow(() -> new BizException(500, "用户权限不足"));
        return String.format("/user/login/%s", CspUtils.encodeCspId(csp.getCspId()));
    }

    @Override
    public String obtainCspId(String userId) {
        Assert.notNull(userId, "userId不能为空");
        if (userId.length() == 10)
            return userId;
        return this.lambdaQuery()
                .eq(Csp::getUserId, userId)
                .select(Csp::getCspId)
                .oneOpt()
                .map(Csp::getCspId)
                .orElseThrow(() -> new BizException(500, "未找到csp:+userId"));
    }

    @Override
    public List<String> queryAllCspId() {
        LambdaQueryWrapper<CspDo> cspQueryWrapper = new LambdaQueryWrapper<>();
        cspQueryWrapper.eq(CspDo::getIsSplite, 1);
        List<CspDo> cspDos = csp1Dao.selectList(cspQueryWrapper);
        Set<String> cspIdSet = new HashSet<>();
        String cspId;
        for (CspDo cspDo : cspDos) {
            cspId = cspDo.getCspId();
            if (!Strings.isNullOrEmpty(cspId) && cspId.length() == 10) {
                cspIdSet.add(cspId);
            }
        }
        return new ArrayList<>(cspIdSet);
    }

    @Override
    public List<UserInfoVo> queryByNameOrPhone(String nameOrPhone) {
        return baseMapper.queryByNameOrPhone(nameOrPhone);
    }

    @Override
    public List<UserInfoVo> getByIdList(Collection<String> idList) {
        return baseMapper.getByIdList(idList);
    }


    @Override
    public boolean getCspStatus(String userId) {
        String cspId = obtainCspId(userId);
        //1、csp_active
        if (!Long.valueOf(1).equals(checkCspCustomerMapper.checkCspStatusByCspId(cspId))) {
            return false;
        }
        //2、user_platform_permissions
        boolean user_platform_permissions = false;
        Csp csp = getCspByCspId(cspId);
        if (Objects.nonNull(csp)) {
            //前端登录csp是时候传平台3
            UserPlatformPermissionsDo permissions = identificationService.findByPlatAndUserId(3, csp.getUserId());
            if (Objects.isNull(permissions)) return false;//客户在表里没有
            //用户状态不能是2禁用 申请审核必须是3
            user_platform_permissions = permissions.getUserStatus() != 2 && permissions.getApprovalStatus() == 3;
        }
        return user_platform_permissions;
    }

    @Override
    public Csp getCspByCspId(String cspId) {
        return lambdaQuery().eq(Csp::getCspId, cspId).one();
    }

    @Override
    public boolean isCspInUser(String userId) {
        //1、查询用户是否user表中
        UserDo user = userService.findByUserId(userId);
        if(user != null){
            Csp csp = getCspByCspId(userId);
            return csp != null ;
        }
        return false;
    }

    @Override
    public void checkUserPublishAuth(String userId) {
        UserDo user = userService.findByUserId(userId);
        if(user != null){
            if(user.getTempStorePerm() == 0){
                throw new BizException(500,"用户的模版发布权限已被禁用");
            }
        }
    }


    @Override
    public Page<CspMealCspInfo> allList(Page<CspMealCspInfo> page, CspInfoPage query){
        return getBaseMapper().listAll(page,query);
    }

    private Optional<Csp> getByUserId(String userId) {
        return this.lambdaQuery()
                .eq(Csp::getUserId, userId)
                .eq(Csp::getCspActive, 1)
                .oneOpt();
    }
}
