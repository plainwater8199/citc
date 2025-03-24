package com.citc.nce.auth.utils;

import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.Data;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/5 15:55
 * @Version 1.0
 * @Description:
 */
@Data
public class CheckLength {
    private Integer len;
    private String str;

    public boolean checkLengthOverLimit(String str, Integer len) {
        if ((StringUtil.isNotBlank(str) && len > str.length()) || (StringUtil.isBlank(str) && len >= 0))
            return false;
        return true;
    }
}
