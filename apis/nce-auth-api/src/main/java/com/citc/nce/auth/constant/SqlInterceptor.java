package com.citc.nce.auth.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 11:43
 * @Version 1.0
 * @Description:
 */
public class SqlInterceptor {
    public static final List<String> sqlList = Arrays.asList("com.citc.nce.auth.user.dao.UserDao.selectPage_mpCount", "com.citc.nce.auth.user.dao.UserDao.selectPage");
}
