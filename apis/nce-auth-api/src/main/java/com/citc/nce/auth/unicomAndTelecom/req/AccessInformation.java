package com.citc.nce.auth.unicomAndTelecom.req;

import lombok.Data;

@Data
public class AccessInformation {
    private String AppID;

    private String secretKey;

    private String token;


}
