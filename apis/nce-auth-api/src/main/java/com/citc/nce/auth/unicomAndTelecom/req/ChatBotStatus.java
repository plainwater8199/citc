package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;

@Data
public class ChatBotStatus {

  private String accessTagNo;
  //上下线标志，1:上线，2:下线（后续可能扩充此泛型类型），3:测试
  private Integer type;
  private String cspId;

}
