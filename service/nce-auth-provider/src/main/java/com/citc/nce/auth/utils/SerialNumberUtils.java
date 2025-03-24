package com.citc.nce.auth.utils;

import com.citc.nce.auth.serialnumber.GlobalSerialNumberService;
import com.citc.nce.common.util.SpringUtils;

/**
 * @author jcrenc
 * @since 2024/5/23 15:37
 */
public class SerialNumberUtils {
    public static final int MSG_ORDER = 1;//预付费和后付费订单

    public static void applyGlobalUniqueSerialNumber(int type, String serialNumber) {
        GlobalSerialNumberService globalSerialNumberService = SpringUtils.getBean(GlobalSerialNumberService.class);
        globalSerialNumberService.applyGlobalUniqueSerialNumber(type, serialNumber);
    }
}
