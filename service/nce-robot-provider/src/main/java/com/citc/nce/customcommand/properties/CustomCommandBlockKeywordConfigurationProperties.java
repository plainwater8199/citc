package com.citc.nce.customcommand.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jcrenc
 * @since 2024/3/19 11:58
 */
@Configuration
@ConfigurationProperties(prefix = "custom-command")
@Getter
@Setter
@RefreshScope
public class CustomCommandBlockKeywordConfigurationProperties {
    private final List<String> DEFAULT_BLOCK_KEYWORDS = Collections.emptyList();
    private List<String> blockKeywords = DEFAULT_BLOCK_KEYWORDS;

    public void setBlockKeywords(List<String> blockKeywords) {
        if (CollectionUtils.isEmpty(blockKeywords)) {
            this.blockKeywords = DEFAULT_BLOCK_KEYWORDS;
        } else {
            this.blockKeywords = blockKeywords.stream().map(String::toLowerCase).distinct().collect(Collectors.toList());
        }
    }
}
