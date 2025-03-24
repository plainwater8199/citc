package com.citc.nce.misc.guide.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author jcrenc
 * @since 2024/11/6 17:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGuideProgressResp {
    private Map<String, Progress> progresses;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Progress {
        private int totalSteps;
        private int currentStep;
        private boolean completed;
        private LocalDateTime completedAt;
    }
}
