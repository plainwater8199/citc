package com.citc.nce.im.elasticsearch;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.EsPageResult;
import com.citc.nce.common.core.pojo.TagInfo;
import com.citc.nce.im.elasticsearch.entity.EsSummary;
import com.citc.nce.im.materialSquare.mapper.MsSummaryMapper;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsEsPageQuery;
import com.citc.nce.robot.api.materialSquare.vo.summary.MsEsPageResult;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作品库es操作对象
 *
 * @author bydud
 * @since 2024/6/27 14:09
 */
@Slf4j
@Component
@AllArgsConstructor
public class EsSummaryService {
    private EsClientUtil esClientUtil;
    private EsSummaryConfig esSummaryConfig;
    private MsSummaryMapper summaryMapper;

    @PostConstruct
    public void verifyIndex() {
        ElasticsearchClient esClient = esClientUtil.getClient();
        //校验索引是否存在
        indexExist(esClient);
    }

    private void indexExist(ElasticsearchClient client) {
        try {
            String indexName = esSummaryConfig.getIndexName();
            // 检查索引是否存在
            boolean indexExists = client.indices().exists(e -> e.index(indexName)).value();
            log.info("索引: {} 存在: {}", indexName, indexExists);
            if (!indexExists) {
                // 创建索引
                ClassPathResource resource = new ClassPathResource("index_summary.json");
                CreateIndexRequest.Builder index = new CreateIndexRequest.Builder().index(indexName);
                index.withJson(resource.getInputStream());
                CreateIndexResponse createResponse = client.indices().create(index.build());
                if (!createResponse.acknowledged())
                    throw new BizException("索引:" + indexName + " 创建失败.");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private MsSummary getViewNumByMssId(Long mssId) {
        return summaryMapper.selectOne(new LambdaQueryWrapperX<MsSummary>()
                .eq(MsSummary::getMssId, mssId)
                .select(MsSummary::getLikesCount, MsSummary::getViewCount));
    }

    /**
     * 通过删库删除所有数据
     */
    public void removeAll() {
        esClientUtil.reSetIndex(esSummaryConfig.getIndexName());
    }

    /**
     * 保存(更新)数据的方法。注意是全量更新，null也会插入elasticSearch
     */
    @SneakyThrows
    public void saveOrUpdate(MsSummary msSummary) {
        EsSummary product = convertEsSummary(msSummary);
        IndexResponse response = esClientUtil.getClient().index(i -> i
                .index(esSummaryConfig.getIndexName())
                .id(product.getMssId().toString())
                .document(product)
        );
        log.info("Indexed with version {}", response.version());
    }

    /**
     * 保存(更新)数据的方法。注意是全量更新，null也会插入elasticSearch
     */
    @SneakyThrows
    public void saveOrUpdate(List<MsSummary> list) {
        List<EsSummary> summaryList = list.stream().map(this::convertEsSummary).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(list)) return;

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (EsSummary product : summaryList) {
            br.operations(op -> op.index(idx -> idx
                            .index(esSummaryConfig.getIndexName())
                            .id(product.getMssId().toString())
                            .document(product)
                    )
            );
        }

        BulkResponse result = esClientUtil.getClient().bulk(br.build());

        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
            throw new BizException("Bulk had errors");
        }
    }


    /**
     * 修改es文档中的收藏量
     *
     * @param mssId   文档id
     * @param likeNum 收藏量
     */
    public void updateLike(Long mssId, Long likeNum) {
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("likeNum", likeNum);
        updateFields.put("likesCount", new BigDecimal(likeNum));
        // 构建更新请求
        UpdateResponse<EsSummary> update = updateFields(mssId, updateFields);
        // 输出结果
        if (Result.Updated.equals(update.result())) return;
        throw new BizException("ES 修改收藏失败");
    }

    /**
     * 修改es文档中的流量量
     *
     * @param mssId   es文档id
     * @param viewNum 浏览量
     */
    public void updateView(Long mssId, Long viewNum) {
        Map<String, Object> updateFields = new HashMap<>();
        updateFields.put("viewNum", viewNum);
        updateFields.put("viewCount", new BigDecimal(viewNum));
        // 构建更新请求
        UpdateResponse<EsSummary> update = updateFields(mssId, updateFields);
        // 输出结果
        if (Result.Updated.equals(update.result())) return;
        throw new BizException("ES 修改浏览量失败");
    }

    /**
     * 局部更新字段
     *
     * @param mssId        需要修改的文档
     * @param updateFields 字段和值得列表
     * @return UpdateResponse 修改结果   （一般的查询update.result()）
     */
    @SneakyThrows
    public UpdateResponse<EsSummary> updateFields(Long mssId, Map<String, ? extends Object> updateFields) {
        UpdateRequest<EsSummary, Map<String, ? extends Object>> updateRequest = UpdateRequest.of(u -> u
                .index(esSummaryConfig.getIndexName())  // 请替换为实际索引名
                .id(mssId.toString())    // 请替换为实际文档ID
                .doc(updateFields)
        );
        // 执行更新请求
        return esClientUtil.getClient().update(updateRequest, EsSummary.class);
    }

    /**
     * id删除es文东
     *
     * @param mssId 作品库id
     */
    @SneakyThrows
    public void removeByMssId(Long mssId) {
        esClientUtil.getClient().delete(new DeleteRequest.Builder()
                .index(esSummaryConfig.getIndexName())
                .id(mssId.toString())
                .build());
    }

    /**
     * 同个id删除es文东
     *
     * @param mssIds 作品库id
     */
    @SneakyThrows
    public void removeByMssIds(List<Long> mssIds) {
        if (CollectionUtils.isEmpty(mssIds)) return;

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Long mssId : new HashSet<>(mssIds)) {
            br.operations(op -> op.delete(idx -> idx
                            .index(esSummaryConfig.getIndexName())
                            .id(mssId.toString())
                    )
            );
        }
        BulkResponse result = esClientUtil.getClient().bulk(br.build());
        // Log errors, if any
        if (result.errors()) {
            log.error("Bulk had errors");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
            throw new BizException("Bulk had errors");
        }
    }


    private EsSummary convertEsSummary(MsSummary summary) {
        EsSummary entity = new EsSummary(summary);
        MsSummary msSummary = getViewNumByMssId(summary.getMssId());
        entity.setLikeNum(0L);
        entity.setViewNum(0L);
        if (Objects.nonNull(msSummary)) {
            entity.setLikeNum(msSummary.getLikesCount().longValue());
            entity.setViewNum(msSummary.getViewCount().longValue());
        }
        return entity;
    }


    public EsPageResult<MsEsPageResult> page(MsEsPageQuery queryParam) {
        // 构建查询
        log.debug("es query param: {}", queryParam);
        Script scoreScript = esSummaryConfig.getScoreScript(queryParam.getOrderType());
        int pageSize = queryParam.getPageSize();
        int pageNo = queryParam.getPageNo();
        SearchRequest citcMsSummaryTest = new SearchRequest.Builder()
                .size(pageSize)
                .from(pageSize * (pageNo - 1))
                .index(esSummaryConfig.getIndexName())
                .query(q -> q
                        .functionScore(fs -> fs
                                .query(query -> query.bool(
                                        bool -> {
                                            bool = buildTypeQuery(bool, queryParam.getMsType());
                                            bool = buildTagsQuery(bool, Collections.singletonList(queryParam.getMsTag()));
                                            bool = buildQueryStrQuery(bool, queryParam.getQueryStr());
                                            bool = filterSuggestions(bool, queryParam.getSuggestions());
                                            return bool;
                                        })
                                )
                                .functions(FunctionScore.of(fsBuilder -> fsBuilder.scriptScore(ss -> ss.script(scoreScript))))
                                //忽略基础得分，使用自定义得分脚本的分数替代
                                .boostMode(FunctionBoostMode.Replace)
                        )
                )
                .aggregations("tags_aggregation", ag -> ag
                        .terms(t -> t
                                .field("tags")
                        )
                )
                .explain(esSummaryConfig.enableQueryExplain())
                .build();
        //执行搜索
        SearchResponse<EsSummary> response = null;
        try {
            response = esClientUtil.getClient().search(citcMsSummaryTest, EsSummary.class);
        } catch (IOException e) {
            log.error("es搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索服务不可用，请稍后再试。");
        }
        //转换结果
        List<MsEsPageResult> hits = response.hits().hits().stream()
                .map(hit -> {
                    if (log.isDebugEnabled() && hit.explanation() != null) {
                        log.debug("ES query explain, id: {}, explanation: {}", hit.id(), hit.explanation());
                    }
                    return hit;
                })
                .map(this::converMsEsPageResult)
                .collect(Collectors.toList());
        //拿到总数
        Long total = Optional.ofNullable(response.hits().total()).map(TotalHits::value).orElse(0L);
        //构建返回对象
        EsPageResult<MsEsPageResult> result = new EsPageResult<>(hits, total);
        List<TagInfo> tagInfos = queryAllTags(queryParam);
        result.setTags(tagInfos);
        return result;
    }

    private List<TagInfo> queryAllTags(MsEsPageQuery queryParam) {
        List<TagInfo> tagInfos = new ArrayList<>();
        Script scoreScript = esSummaryConfig.getScoreScript(queryParam.getOrderType());
        int pageSize = queryParam.getPageSize();
        int pageNo = queryParam.getPageNo();
        SearchRequest citcMsSummaryTest = new SearchRequest.Builder()
                .size(pageSize)
                .from(pageSize * (pageNo - 1))
                .index(esSummaryConfig.getIndexName())
                .query(q -> q
                        .functionScore(fs -> fs
                                .query(query -> query.bool(
                                        bool -> {
                                            bool = buildTypeQuery(bool, queryParam.getMsType());
                                            bool = buildQueryStrQuery(bool, queryParam.getQueryStr());
                                            bool = filterSuggestions(bool, queryParam.getSuggestions());
                                            return bool;
                                        })
                                )
                                .functions(FunctionScore.of(fsBuilder -> fsBuilder.scriptScore(ss -> ss.script(scoreScript))))
                                //忽略基础得分，使用自定义得分脚本的分数替代
                                .boostMode(FunctionBoostMode.Replace)
                        )
                )
                .aggregations("tags_aggregation", ag -> ag
                        .terms(t -> t
                                .field("tags")
                        )
                )
                .explain(esSummaryConfig.enableQueryExplain())
                .build();
        //执行搜索
        try {
            SearchResponse<EsSummary> response = esClientUtil.getClient().search(citcMsSummaryTest, EsSummary.class);
            //拿到标签聚合结果
            Aggregate tagsAggregation = response.aggregations().get("tags_aggregation");
            if (tagsAggregation != null) {
                tagInfos = tagsAggregation
                        .sterms()
                        .buckets()
                        .array().stream()
                        .map(bucket -> new TagInfo(bucket.key().stringValue(), bucket.docCount()))
                        .collect(Collectors.toList());

            }
        } catch (IOException e) {
            log.error("es搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索服务不可用，请稍后再试。");
        }
        return tagInfos;
    }

    private static BoolQuery.Builder buildTypeQuery(BoolQuery.Builder boolBuilder, MsType type) {
        if (type == null)
            return boolBuilder;
        return boolBuilder.must(m -> m.match(MatchQuery.of(mq -> mq.field("msType").query(type.getCode()))));
    }

    private static BoolQuery.Builder buildTagsQuery(BoolQuery.Builder boolBuilder, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return boolBuilder;
        }
        //必须匹配所有tag
        for (String tag : tags) {
            if (tag == null)
                continue;
            boolBuilder.must(m -> m.match(MatchQuery.of(mq -> mq.field("tags").query(tag))));
        }
        return boolBuilder;
    }

    private static BoolQuery.Builder buildQueryStrQuery(BoolQuery.Builder boolBuilder, String queryStr) {
        if (queryStr == null || queryStr.isEmpty()) {
            return boolBuilder;
        }
        String value = String.format("*%s*", queryStr);
        return boolBuilder.must(s ->
                s.bool(b -> {
                    b.should(sh -> sh.multiMatch(m -> m
                                            .fields("name", "msDesc")
                                            .query(queryStr)
                                            .type(TextQueryType.MostFields)
                                    )
                            )
                            .should(sh -> sh.wildcard(w -> w
                                    .field("name")
                                    .value(value)
                            ))
                            .should(sh -> sh.wildcard(w -> w
                                    .field("msDesc")
                                    .value(value)
                            ))
                            .minimumShouldMatch("1");
                    return b;
                }));
    }

    private static BoolQuery.Builder filterSuggestions(BoolQuery.Builder boolBuilder, List<MsEsPageResult> suggestions) {
        if (CollectionUtils.isEmpty(suggestions)) return boolBuilder;
        List<FieldValue> terms = suggestions.stream()
                .map(MsEsPageResult::getMssId)
                .map(FieldValue::of)
                .collect(Collectors.toList());

        return boolBuilder.filter(filter ->
                filter.bool(fb ->
                        fb.mustNot(mustNot ->
                                mustNot.terms(TermsQuery.of(tq -> tq.field("mssId").terms(tqf -> tqf.value(terms))))
                        )
                )
        );
    }

    private MsEsPageResult converMsEsPageResult(Hit<EsSummary> esSummaryHit) {
        EsSummary source = esSummaryHit.source();
        if (Objects.isNull(source)) return null;
        MsEsPageResult entity = new MsEsPageResult();
        BeanUtils.copyProperties(source, entity);
        return entity;
    }

}
