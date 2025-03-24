package com.citc.nce.im.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.citc.nce.im.elasticsearch.entity.EsSummary;
import com.citc.nce.im.elasticsearch.tls.TrustHostnameVerifier;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bydud
 * @since 2024/6/27 9:26
 */
public class Test {

    static String host = "124.70.104.202";
    static int port = 9200;
    static String es_index = "citc_ms_summary";
    static String fingerprint = "000f5b37d040ebffab09aedb2206aa63de580f57b5e99da6be25bcc102f1dd07";
    static String elasticPwd = "UPXTihm4rhnLOGqTkRIf";

    public static void main(String[] args) throws IOException {
        ElasticsearchClient esClient = getElasticsearchClient();
        queryByLambda(esClient);
    }

    private static void removeBatch(ElasticsearchClient esClient) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();

        for (String mssId : Arrays.asList("123456d", "1234567")) {
            br.operations(op -> op.delete(idx -> idx
                            .index(es_index)
                            .id(mssId.toString())
                    )
            );
        }
        BulkResponse result = esClient.bulk(br.build());
        if (result.errors()) {
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    System.out.println("item.error().reason() = " + item.error().reason());
                }
            }
        }
    }

    private static void update(ElasticsearchClient esClient) throws IOException {

        Map<String, Long> updateFields = new HashMap<>();
        updateFields.put("likeNum", 100L);
        updateFields.put("viewNum", 200L);

        // 构建更新请求
        UpdateRequest<EsSummary, Map<String, Long>> updateRequest = UpdateRequest.of(u -> u
                .index(es_index)  // 请替换为实际索引名
                .id("123456")    // 请替换为实际文档ID
                .doc(updateFields)
        );

        // 执行更新请求
        UpdateResponse<EsSummary> update = esClient.update(updateRequest, EsSummary.class);
        // 输出结果
        System.out.println("Document updated: " + update.result());
    }

    private static void reSetInex(ElasticsearchClient esClient) throws IOException {
        IndexState indexState = backUpIndex(esClient, es_index);

        esClient.indices().delete(new DeleteIndexRequest.Builder().index(es_index).build());

        esClient.indices().create(new CreateIndexRequest.Builder().
                index(es_index)
                .mappings(indexState.mappings())
                .settings(set -> set.index(i -> i.analysis(an -> an.analyzer(indexState.settings().index().analysis().analyzer()))))
                .build());
    }

    private static IndexState backUpIndex(ElasticsearchClient esClient, final String esIndex) throws IOException {
        GetIndexResponse response = esClient.indices().get(index -> index.index(esIndex));
        return response.get(esIndex);

    }

    private static void queryByLambda(ElasticsearchClient esClient) throws IOException {
        String scriptSource = "double customScore = 0; if (doc.containsKey('likeNum') && doc['likeNum'].size() != 0) { customScore += doc['likeNum'].value * params.likeNum; } if (doc.containsKey('viewNum') && doc['viewNum'].size() != 0) { customScore += doc['viewNum'].value * params.viewNum; } return _score * params.BM25score + customScore * params.customScore;";

        // 定义评分脚本的参数
        Map<String, JsonData> params = new HashMap<>();
        params.put("BM25score", JsonData.of(0.8));
        params.put("customScore", JsonData.of(0.2));
        params.put("likeNum", JsonData.of(0.7));
        params.put("viewNum", JsonData.of(0.3));

        // 构建脚本查询
        Script script = Script.of(s -> s
                .inline(i -> i
                        .source(scriptSource)
                        .params(params)
                )
        );

        // 构建查询
        SearchResponse<EsSummary> response = esClient.search(s -> s
                        .index(es_index)
                        .query(q -> q
                                .functionScore(fs -> fs
                                        .query(subQuery -> subQuery
                                                .bool(bool -> {
                                                    bool.must(blMust -> blMust.match(mq -> mq
                                                            .field("queryStr")
                                                            .query("5G消息小猫咪")
                                                    ));
                                                    bool.filter(new ArrayList<>());
                                                    return bool;
                                                })


                                        )
                                        .functions(fun -> fun
                                                .scriptScore(ss -> ss
                                                        .script(script)
                                                )
                                        )
                                        .boostMode(FunctionBoostMode.Replace)
                                )
                        ),
                EsSummary.class
        );

        // 处理搜索结果
        for (Hit<EsSummary> hit : response.hits().hits()) {
            System.out.println(hit.source());
        }
    }


    @SneakyThrows
    private static Map<String, IndexState> getIndexState(ElasticsearchClient esClient) {
        return esClient.indices().get(index -> index.index(es_index)).result();
    }


    private static ElasticsearchClient getElasticsearchClientCA() {

        try {
            File certFile = new File("C:\\http_ca.crt");
            SSLContext sslContext = TransportUtils
                    .sslContextFromHttpCaCrt(certFile);
            RestClient restClient = RestClient
                    .builder(new HttpHost(host, port, "https"))
                    .setHttpClientConfigCallback(hc -> hc
                            .setHostnameVerifier(new TrustHostnameVerifier(host))
                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(getBasicCredentialsProvider())
                    )
                    .build();

// Create the transport and the API client
            ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
            return new ElasticsearchClient(transport);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private static @NotNull BasicCredentialsProvider getBasicCredentialsProvider() {
        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials("elastic", elasticPwd)
        );
        return credsProv;
    }

    private ElasticsearchClient getElasticsearchClientHttp() {
        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setDefaultCredentialsProvider(getBasicCredentialsProvider())
                )
                .build();
        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
        return new ElasticsearchClient(transport);
    }

    //Verifying HTTPS with a certificate fingerprint
    private static ElasticsearchClient getElasticsearchClient() {
        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(fingerprint);
        RestClient restClient = RestClient
                .builder(new HttpHost(host, port, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setHostnameVerifier(new TrustHostnameVerifier(host))
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(getBasicCredentialsProvider())
                )
                .build();


        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
        return new ElasticsearchClient(transport);
    }

    private static JsonpMapper getJSONMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return new JacksonJsonpMapper(objectMapper);
    }

    public static SSLContext getSSLContextTrustAll() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        // 首先实现一个X509TrustManager接口，用于信任所有证书
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // 不进行任何校验
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // 不进行任何校验
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        return sslContext;
    }

}

