package com.citc.nce.filecenter.configure;

import com.citc.nce.filecenter.enums.UploadFileType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.citc.nce.filecenter.enums.UploadFileType.*;

/**
 * 上传文件大小限制，可从nacos动态加载（忽略错误配置）
 *
 * @author jcrenc
 * @since 2024/7/16 9:51
 */
@Data
@ConfigurationProperties(prefix = "upload-file")
@Component
@RefreshScope
public class FileUploadLimitConfigure {
    private static final long _5M = 5 * 1024 * 1024L;
    private static final long _10M = 10 * 1024 * 1024L;
    private static final long _100M = 100 * 1024 * 1024L;

    private static final Map<UploadFileType, Long> DEFAULT_CONFIG;

    static {
        Map<UploadFileType, Long> map = new HashMap<>();
        map.put(MATERIAL_IMAGE, _5M);
        map.put(MATERIAL_AUDIO, _100M);
        map.put(MATERIAL_VIDEO, _5M);
        map.put(CUSTOM_LOG, _10M);
        map.put(BLACK_LIST, _10M);
        map.put(H5_FORM, _10M);
        DEFAULT_CONFIG = Collections.unmodifiableMap(map);
    }

    private Map<UploadFileType, Long> limitSize = DEFAULT_CONFIG;


    public void setLimitSize(Map<UploadFileType, Long> limitSize) {
        if (limitSize != null) {
            Map<UploadFileType, Long> validLimitSize = new HashMap<>();
            for (Map.Entry<UploadFileType, Long> entry : limitSize.entrySet()) {
                if (entry.getKey() != null) {
                    validLimitSize.put(entry.getKey(), entry.getValue());
                }
            }
            this.limitSize = Collections.unmodifiableMap(validLimitSize);
        }
    }
}
