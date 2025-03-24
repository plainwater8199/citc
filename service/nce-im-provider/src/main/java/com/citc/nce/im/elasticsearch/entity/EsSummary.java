package com.citc.nce.im.elasticsearch.entity;

import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author bydud
 * @since 2024/6/27 15:47
 */
@NoArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
@ToString
public class EsSummary extends MsSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msTypeAlias;
    private Long likeNum;
    private Long viewNum;

    public EsSummary(MsSummary summary) {
        super();
        BeanUtils.copyProperties(summary, this);
        this.msTypeAlias = getMsTypeAlias();
    }

    public String getMsTypeAlias() {
        if (StringUtils.hasLength(msTypeAlias)) return msTypeAlias;
        if (Objects.nonNull(getMsType())) {
            return getMsType().getAlias();
        }
        return "";
    }
}