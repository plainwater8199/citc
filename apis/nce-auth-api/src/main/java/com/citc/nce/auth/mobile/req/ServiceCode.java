package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务代码
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceCode extends BaseRequest {

   //产品订单号
   private String prodordSkuNum;
   //产品订购关系ID，计费编码
   private String prodistSkuNum;
   //集团客户联系人
   private String contactName;
   //集团客户联系人手机号码
   private String contactPhone;
   //产品类型：01-5G消息
   private String prodType;
   //服务代码类型
   private String serviceCodeType;
   //是否为CSP平台 1-是  0-否
   private String cspFlag;
   //CSP平台名称，CSP平台Flag等于1时必填
   private String cspName;
}
