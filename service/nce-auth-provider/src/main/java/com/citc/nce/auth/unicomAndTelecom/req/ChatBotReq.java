package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * 联通电信chatBot信息
 */
@Data
@Accessors(chain = true)
public class ChatBotReq {
  //编辑的时候才有
  private String accessTagNo;
  private String cspId;
  private String cspEcNo;
  private String chatbotId;
  private String serviceName;
  private String serviceIcon;
  private String serviceDescription;
  private String SMSNumber;
  private String autograph;
  private List category;
  private String provider;
  private Integer showProvider;
  private String TCPage;
  private String emailAddress;
  private String serviceWebsite;
  private String callBackNumber;
  private String address;
  private Double longitude;
  private Double latitude;
  private List<String> ipWhiteList;

}
