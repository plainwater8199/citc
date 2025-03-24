package com.citc.nce.filecenter.configure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "file-upload-config")
@RefreshScope
public class FileUploadConfig {
    private Map<String, FileLimitInfo> sceneMap;


    @Data
    public static class FileLimitInfo {
        // 文件大小(KB)
        private Long size;
        // 场景支持的文件类型
        private String format;
        // 缩略图大小限制(KB)（没有传-1）
        private Long thumbnailSize;
        // 时长(秒)（没有传-1）
        private Long duration;
        // 访问权限（暂未使用） 0:自己访问(默认), 1:所有登录用户, 2:全公网, 3:相同角色登录用户
        private Long accessPrivilege;
    }
}
