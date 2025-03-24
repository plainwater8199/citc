package com.citc.nce.im.elasticsearch.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * EsSummary 自定义评分配置
 *
 * @author bydud
 * @since 2024/7/1 9:45
 */
@Getter
@Setter
public class CustomScore implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scriptAll;
    private String scriptLike;
    private String scriptView;
    private Double BM25score;
    private Double customScore;
    private Double likeNum;
    private Double viewNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomScore that = (CustomScore) o;
        return Objects.equals(scriptAll, that.scriptAll) && Objects.equals(scriptLike, that.scriptLike) && Objects.equals(scriptView, that.scriptView) && Objects.equals(BM25score, that.BM25score) && Objects.equals(customScore, that.customScore) && Objects.equals(likeNum, that.likeNum) && Objects.equals(viewNum, that.viewNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scriptAll, scriptLike, scriptView, BM25score, customScore, likeNum, viewNum);
    }
}
