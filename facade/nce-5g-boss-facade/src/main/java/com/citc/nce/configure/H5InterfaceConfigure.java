package com.citc.nce.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "h5")
public class H5InterfaceConfigure {
    private String uris = "";
    private String apis = "";
}
