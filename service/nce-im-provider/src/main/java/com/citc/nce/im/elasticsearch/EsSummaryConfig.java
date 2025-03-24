package com.citc.nce.im.elasticsearch;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.json.JsonData;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.elasticsearch.entity.CustomScore;
import com.citc.nce.robot.api.materialSquare.emums.MsEsOrder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bydud
 * @since 2024/6/27 14:51
 */

@Slf4j
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "es.summary")
@Setter
public class EsSummaryConfig {
    private String indexName;
    private CustomScore customScore;
    /*是否解释查询，开启之后查询结果会返回得分的详细信息，但是十分耗性能，只能在调试时打开*/
    private boolean queryExplain = false;

    //控制变量
    private int haseCode;
    private Map<String, JsonData> paramsMap;

    public String getIndexName() {
        Assert.hasLength(indexName, "indexName is empty");
        return indexName;
    }

    /**
     * 或者自定义评分脚本
     *
     * @return String Script
     */
    public Script getScoreScript(MsEsOrder orderType) {
        // 构建脚本查询
        if (MsEsOrder.ALL.equals(orderType)) {
            return Script.of(s -> s
                    .inline(i -> i
                            .source(customScore.getScriptAll())
                            .params(getScoreScriptParams())
                    )
            );
        }
        if (MsEsOrder.likeNum.equals(orderType)) {
            return Script.of(s -> s
                    .inline(i -> i
                            .source(customScore.getScriptLike())
                    )
            );
        }

        if (MsEsOrder.viewNum.equals(orderType)) {
            return Script.of(s -> s
                    .inline(i -> i
                            .source(customScore.getScriptView())
                    )
            );
        }

        throw new BizException("不支持排序类型：" + orderType.getAlias());
    }

    private Map<String, JsonData> getScoreScriptParams() {
        if (customScore.hashCode() != haseCode || paramsMap == null) {
            Map<String, JsonData> params = new HashMap<>();
            params.put("BM25score", JsonData.of(customScore.getBM25score()));
            params.put("customScore", JsonData.of(customScore.getCustomScore()));
            params.put("likeNum", JsonData.of(customScore.getLikeNum()));
            params.put("viewNum", JsonData.of(customScore.getViewNum()));
            this.paramsMap = params;
            this.haseCode = customScore.hashCode();
            return params;
        }
        return this.paramsMap;
    }

    public boolean enableQueryExplain() {
        return queryExplain;
    }

}
