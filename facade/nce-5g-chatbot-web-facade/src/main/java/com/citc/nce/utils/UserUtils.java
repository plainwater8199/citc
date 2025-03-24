package com.citc.nce.utils;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.util.SpringUtils;
import com.citc.nce.common.web.utils.UserTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/11 16:58
 */
@Slf4j
public class UserUtils {

    public static void setContextUser(String userId) {
        List<String> tokenValueListByLoginId = StpUtil.getTokenValueListByLoginId(userId);
        BaseUser user = new BaseUser();
        if (userId.length() == 15) {
            CspCustomerApi customerApi = SpringUtils.getBean(CspCustomerApi.class);
            UserInfoVo userInfo = customerApi.getByCustomerId(userId);
            BeanUtils.copyProperties(userInfo, user);
            user.setUserId(userInfo.getCustomerId());
        } else {
            CspApi cspApi = SpringUtils.getBean(CspApi.class);
            List<UserInfoVo> userInfoVos = cspApi.getByIdList(Collections.singletonList(userId));
            if (!CollectionUtils.isEmpty(userInfoVos)) {
                user.setUserId(userInfoVos.get(0).getCustomerId());
                BeanUtils.copyProperties(userInfoVos.get(0), user);
            }
        }
        if (!CollectionUtils.isEmpty(tokenValueListByLoginId)) {
            if(ObjectUtil.isEmpty(StpUtil.getTokenSessionByToken(tokenValueListByLoginId.get(0)).get(UserTokenUtil.SESSION_USER_KEY)))
            {
                StpUtil.getTokenSessionByToken(tokenValueListByLoginId.get(0)).set(UserTokenUtil.SESSION_USER_KEY, user);
            }
            SessionContextUtil.setUser(user, tokenValueListByLoginId.get(0));
        } else {
            String token = StpUtil.createLoginSession(userId, new SaLoginModel());
            StpUtil.getTokenSessionByToken(token).set(UserTokenUtil.SESSION_USER_KEY, user);
           SessionContextUtil.setUser(user, token);
        }
    }
}
