package com.citc.nce.misc.constant;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/7/18 16:04
 * @Version 1.0
 * @Description:
 */
public interface MsgCode {

    /*企业用户*/
    PushMsg BUSINESS_USER_PASS = new PushMsg("BUSINESS_USER_PASS");
    PushMsg BUSINESS_USER_NOT_PASS = new PushMsg("BUSINESS_USER_NOT_PASS");

    /*实名用户*/
    PushMsg REAL_NAME_USER_PASS = new PushMsg("REAL_NAME_USER_PASS");
    PushMsg REAL_NAME_USER_NOT_PASS = new PushMsg("REAL_NAME_USER_NOT_PASS");

    /*入驻用户*/
    PushMsg CHECK_IN_USER_PASS = new PushMsg("CHECK_IN_USER_PASS");
    PushMsg CHECK_IN_USER_NOT_PASS = new PushMsg("CHECK_IN_USER_NOT_PASS");

    /*能力提供商*/
    PushMsg ABILITY_SUPPLIER_PASS = new PushMsg("ABILITY_SUPPLIER_PASS");
    PushMsg ABILITY_SUPPLIER_NOT_PASS = new PushMsg("ABILITY_SUPPLIER_NOT_PASS");

    /*解决供应商*/
    PushMsg SOLUTION_PROVIDER_PASS = new PushMsg("SOLUTION_PROVIDER_PASS");
    PushMsg SOLUTION_PROVIDER_NOT_PASS = new PushMsg("SOLUTION_PROVIDER_NOT_PASS");

    /*解决方案审核*/
    PushMsg SOLUTION_AUDIT_PASS = new PushMsg("SOLUTION_AUDIT_PASS");
    PushMsg SOLUTION_AUDIT_NOT_PASS = new PushMsg("SOLUTION_AUDIT_NOT_PASS");
    PushMsg SOLUTION_DELETED = new PushMsg("SOLUTION_DELETED");

    /*产品订购成功*/
    PushMsg PRODUCT_ORDER_SUCCESSFULLY = new PushMsg("PRODUCT_ORDER_SUCCESSFULLY");
    PushMsg API_TIMES_WARNING = new PushMsg("API_TIMES_WARNING");

    /*API审核*/
    PushMsg API_AUDIT_PASS = new PushMsg("API_AUDIT_PASS");
    PushMsg API_AUDIT_NOT_PASS = new PushMsg("API_AUDIT_NOT_PASS");
    PushMsg API_UPDATE = new PushMsg("API_UPDATE");

    /*API禁用*/
    PushMsg API_DISABLED = new PushMsg("API_DISABLED");
    PushMsg API_CANCEL_DISABLED = new PushMsg("API_CANCEL_DISABLED");

    /*API删除*/
    PushMsg API_DELETED = new PushMsg("API_DELETED");

    /*API上下架*/
    PushMsg API_SHELF = new PushMsg("API_SHELF");
    PushMsg API_LOWER_SHELF = new PushMsg("API_LOWER_SHELF");

    /*关闭订单*/
    PushMsg MERCHANT_ORDER_CLOSED = new PushMsg("MERCHANT_ORDER_CLOSED");
    PushMsg BUYER_ORDER_CLOSED = new PushMsg("BUYER_ORDER_CLOSED");
}
