package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 预发布 （第一次点击发布按钮）
 * @author yy
 * @date 2024-03-25 20:55:34
 */
@Data
public class PrepareForReleaseProcessResp {
    /**
     * 配置最后更新时间
     */
    Date descLastUpdateTime;
    /**
     * 场景关联名字最后修改时间
     */
    Date lastChangeSceneAccountTime;

    private List<Long> templateIds;
    /**
     * 需要审核的新模板和修改过内容的模板
     */

    int needAuditForUpdatedTemplate;
    private String processDes;//设计图
}
