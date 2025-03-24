package com.citc.nce.robot.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo implements Serializable {

    private String messageId;

    private String status;

    private String sender;

    private String code;

    private String message;

    private String oldMessageId;

}
