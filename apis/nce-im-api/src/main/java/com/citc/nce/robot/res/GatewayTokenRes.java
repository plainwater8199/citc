package com.citc.nce.robot.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayTokenRes implements Serializable {

    private boolean success;

    private String message;

    private int expires;

    private String token;


}
