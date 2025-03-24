package com.citc.nce.common.core.pojo;

import lombok.*;

/**
 * @author jcrenc
 * @since 2025/1/14 14:20
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationContext {
    private String conversationId;
    private String contributionId;
}
