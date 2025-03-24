package com.citc.nce.filecenter.converter;

import com.citc.nce.filecenter.enums.UploadFileType;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author jcrenc
 * @since 2024/7/16 10:54
 */
@Component
@ConfigurationPropertiesBinding
public class FileUploadTypeConverter implements Converter<String, UploadFileType> {
    @Override
    public UploadFileType convert(String source) {
        if (!StringUtils.hasText(source))
            return null;
        return Arrays.stream(UploadFileType.values())
                .filter(type -> Objects.equals(type.name(), source.toUpperCase()))
                .findAny()
                .orElse(null);
    }
}
