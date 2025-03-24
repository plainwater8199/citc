package com.citc.nce.authcenter.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.citc.nce.common.core.pojo.BaseUser;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class TokenUtils {

    private TokenUtils() {
        throw new IllegalStateException("Utility class");
    }
    public static final String SESSION_USER_KEY = "authUser";
    public static void updateTempStorePermContextUser(String userId,boolean tempStorePerm){
        List<String> tokens = StpUtil.getTokenValueListByLoginId(userId);
        if (!CollectionUtils.isEmpty(tokens)) {
            for(String token : tokens){
                SaSession tokenSessionByToken = StpUtil.getTokenSessionByToken(token);
                Object o = tokenSessionByToken.get(SESSION_USER_KEY);
                if (Objects.nonNull(o)) {
                    BaseUser baseUser = (BaseUser) o;
                    baseUser.setTempStorePerm(tempStorePerm);
                    tokenSessionByToken.set(SESSION_USER_KEY,baseUser);
                }
            }
        }
    }
}
