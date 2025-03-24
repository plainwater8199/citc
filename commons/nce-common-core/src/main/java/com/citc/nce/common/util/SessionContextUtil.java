package com.citc.nce.common.util;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.thread.ThreadTaskUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;


/**
 * SessionContextUtil : session上下文工具
 * userThreadLocal：保存用户的数据方便本地即时获取，获取userId可以直接通过getUser()方法获取。
 * tokenThreadLocal：保存token的数据方便本地即时获取
 */
public class SessionContextUtil {
    private SessionContextUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String HEADER_NAME = "user";

    public static ThreadLocal<BaseUser> userThreadLocal = ThreadTaskUtils.newThreadLocal();

    public static ThreadLocal<String> tokenThreadLocal = ThreadTaskUtils.newThreadLocal();

    public static BaseUser getUser() {
        return userThreadLocal.get();
    }

    /**
     * 检查是否登录
     *
     * @return
     */
    public static BaseUser getLoginUser() {
        BaseUser baseUser = getUser();
        if (Objects.isNull(baseUser)) {
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return baseUser;
    }

    /**
     * 同时移除
     */
    public static void remove() {
        userThreadLocal.remove();
        tokenThreadLocal.remove();
    }

    public static void setUser(BaseUser baseUser, String token) {
        userThreadLocal.set(baseUser);
        tokenThreadLocal.set(token);
    }

    public static String getToken() {
        return tokenThreadLocal.get();
    }

    /**
     * 验证是csp登录 如果不是csp则报错
     *
     * @return cspId
     */
    public static String verifyCspLogin() {
        BaseUser loginUser = getLoginUser();
        String cspId = loginUser.getCspId();
        if (loginUser.getIsCustomer() || !StringUtils.hasLength(cspId)) {
            throw new BizException("请使用csp账号登录");
        }
        return cspId;
    }

    public static boolean isCspTrumpetUse() {
        BaseUser loginUser = getLoginUser();
        return Objects.nonNull(loginUser.getCtuId());
    }


    /**
     * 判断传入的人是否是csp 如果不是抛出异常
     */
    public static void sameCsp(String creator) {
        if (!SessionContextUtil.getLoginUser().getCspId().equals(creator)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
    }

    /**
     * 判断传入的人是否是自己 如果不是抛出异常
     */
    public static void sameCus(String creator) {
        if (!SessionContextUtil.getLoginUser().getUserId().equals(creator)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
    }

    /**
     * 判断传入的人是否是自己 如果不是抛出异常
     */
    public static String getUserId() {
        return getLoginUser().getUserId();
    }
}
