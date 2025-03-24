package com.citc.nce.authcenter.csp.trumpet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.csp.domain.CspTrumpetAdd;
import com.citc.nce.authcenter.csp.domain.CspTrumpetEdit;
import com.citc.nce.authcenter.csp.domain.CspTrumpetUser;
import com.citc.nce.authcenter.csp.trumpet.dao.CspTrumpetUserMapper;
import com.citc.nce.authcenter.csp.trumpet.service.ICspTrumpetUserService;
import com.citc.nce.authcenter.user.service.UserService;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * csp小号 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 10:01:59
 */
@Service
public class CspTrumpetUserServiceImpl extends ServiceImpl<CspTrumpetUserMapper, CspTrumpetUser> implements ICspTrumpetUserService {

    @Resource
    private UserService userService;

    @Override
    public List<CspTrumpetUser> listByCspId(String cspId) {
        return lambdaQuery().eq(CspTrumpetUser::getCspId, cspId).list();
    }

    @Override
    public void add(CspTrumpetAdd cspTrumpet) {
        String cspId = SessionContextUtil.verifyCspLogin();
        if (lambdaQuery().eq(CspTrumpetUser::getCspId, cspId).count() >= 5) {
            throw new BizException("子账号最多设置5个");
        }
        checkTrumpetUser(cspTrumpet, null);
        checkUserTableUser(cspTrumpet);
        CspTrumpetUser trumpetUser = new CspTrumpetUser();
        BeanUtils.copyProperties(cspTrumpet, trumpetUser);
        save(trumpetUser);
    }

    private void checkTrumpetUser(CspTrumpetAdd cspTrumpet, Long excludeCtuId) {
        CspTrumpetUser byAccountName = getByAccountName(cspTrumpet.getAccountName());
        if (Objects.nonNull(byAccountName) && !byAccountName.getCtuId().equals(excludeCtuId)) {
            throw new BizException("账号名称已被平台用户注册");
        }
        CspTrumpetUser byPhone = getByPhone(cspTrumpet.getPhone());

        if (Objects.nonNull(byPhone) && !byPhone.getCtuId().equals(excludeCtuId)) {
            throw new BizException("该手机号已被平台用户注册");
        }
    }

    private void checkUserTableUser(CspTrumpetAdd cspTrumpet) {
        if (Objects.nonNull(userService.findByAccount(cspTrumpet.getAccountName()))) {
            throw new BizException("账号名称已被平台用户注册");
        }
        if (Objects.nonNull(userService.findByAccount(cspTrumpet.getPhone()))) {
            throw new BizException("该手机号已被平台用户注册");
        }
    }

    @Override
    public CspTrumpetUser getByAccountName(String accountName) {
        return lambdaQuery().eq(CspTrumpetUser::getAccountName, accountName).one();
    }

    @Override
    public CspTrumpetUser getByPhone(String phone) {
        return lambdaQuery().eq(CspTrumpetUser::getPhone, phone).one();
    }

    @Override
    public void edit(CspTrumpetEdit cspTrumpet) {
        checkTrumpetUser(cspTrumpet, cspTrumpet.getCtuId());
        checkUserTableUser(cspTrumpet);

        CspTrumpetUser user = getById(cspTrumpet.getCtuId());
        if (Objects.isNull(user)) {
            throw new BizException(AuthCenterError.ACCOUNT_USER_ABSENT);
        }
        if (!user.getCspId().equals(cspTrumpet.getCspId())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        CspTrumpetUser trumpetUser = new CspTrumpetUser();
        BeanUtils.copyProperties(cspTrumpet, trumpetUser);
        updateById(trumpetUser);
    }

    @Override
    public void del(Long ctuId) {
        CspTrumpetUser trumpetUser = getById(ctuId);
        if (Objects.isNull(trumpetUser)) return;
        if (!SessionContextUtil.getLoginUser().getCspId().equals(trumpetUser.getCreator())) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        removeById(ctuId);
    }

}
