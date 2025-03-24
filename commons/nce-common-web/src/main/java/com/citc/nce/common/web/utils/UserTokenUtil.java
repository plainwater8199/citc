package com.citc.nce.common.web.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.citc.nce.common.Constants;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 将token转换成用户进行保存
 */
@Slf4j
public class UserTokenUtil {
    /**
     * session中用户信息存储的key
     */
    public static final String SESSION_USER_KEY = "authUser";


    private UserTokenUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void saveUser(HttpServletRequest request) {

        String token = request.getHeader(Constants.HttpHeader.TOKEN);
        if (!Strings.isNullOrEmpty(token)) {
            SaSession tokenSessionByToken = StpUtil.getTokenSessionByToken(token);
            Object o = tokenSessionByToken.get(SESSION_USER_KEY);
            if (Objects.nonNull(o)) {
                SessionContextUtil.setUser((BaseUser) o, token);
            }
        }
    }

    public static void refreshTokenUser(HttpServletRequest request, BaseUser baseUser) {
        String token = request.getHeader(Constants.HttpHeader.TOKEN);
        if (!Strings.isNullOrEmpty(token)) {
            SaSession tokenSessionByToken = StpUtil.getTokenSessionByToken(token);
            tokenSessionByToken.set(SESSION_USER_KEY, baseUser);
        }
    }
}
