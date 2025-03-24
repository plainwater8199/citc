package com.citc.nce.im.materialSquare.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryContent;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryIntroduce;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.Objects;

@Data
public class MsSummarySnapshot {

    private final MsSummary summary;
    private final MsSummaryContent content;
    private final MsSummaryIntroduce introduce;


    public MsSummarySnapshot(MsSummary summary, MsSummaryContent content, MsSummaryIntroduce introduce) {
        Assert.notNull(summary, "summary is null");
        Assert.notNull(content, "content is null");
        this.summary = summary;
        this.content = content;
        this.introduce = introduce;
    }

    public String getTsSnapshotUuid() {
        if (Objects.isNull(summary)) return null;
        if (Objects.isNull(content) || StrUtil.isBlankIfStr(content.getMsJson())) return null;
        if (MsType.ROBOT.equals(summary.getMsType()) || MsType.NR_SSG.equals(summary.getMsType())) {
            MallCommonContent parse = JSON.parseObject(content.getMsJson(), MallCommonContent.class);
            if (Objects.isNull(parse)) return null;
            return parse.getSnapshotUuid();
        }
        return null;
    }

}
