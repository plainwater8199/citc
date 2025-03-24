package com.citc.nce.im.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexResponse;
import co.elastic.clients.elasticsearch.indices.IndexState;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.elasticsearch.tls.TrustHostnameVerifier;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * es 客户端的配置
 *
 * @author bydud
 * @since 2024/6/27 14:10
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "es.client")
@Data
public class EsClientUtil {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    //Verifying HTTPS with a CA certificate
    private String caCrtPath;

    //Verifying HTTPS with a certificate fingerprint
    private String fingerprint;

    private String host;
    private Integer port;

    private String username;
    private String password;
    private Boolean ssl = false;
    //用户退出是springboot程序关闭连接
    private RestClient restClient;
    //通过配置类缓存客户端，懒加载在第一次被调用时创建
    private ElasticsearchClient elasticsearchClient;

    public ElasticsearchClient getClient() {
        if (elasticsearchClient == null) {
            Assert.hasLength(host, "host can not be empty");
            Assert.hasLength(username, "username can not be empty");
            Assert.hasLength(password, "password can not be empty");
            if (Objects.isNull(port) || port == 0) {
                port = 9200;
            }

            String authFlag = null;
            //验证参数
            if (StringUtils.hasLength(caCrtPath)) {
                authFlag = "ca";
            }
            if (!StringUtils.hasLength(authFlag) && StringUtils.hasLength(fingerprint)) {
                authFlag = "fingerprint";
            }

            synchronized (this) {
                if (elasticsearchClient == null) {
                    setElasticsearchClient(
                            null == authFlag ? getElasticsearchClientHttp()
                                    : "ca".equals(authFlag) ? getElasticsearchClientCA()
                                    : getElasticsearchClientFingerprint());
                }
            }
        }
        return getElasticsearchClient();
    }


    @SneakyThrows
    public void reSetIndex(String indexName) {
        ElasticsearchClient esClient = getClient();
        //查询index数据
        IndexState indexState = backUpIndex(esClient, indexName);
        esClient.indices().delete(new DeleteIndexRequest.Builder().index(indexName).build());
        //使用之前的mappings 和 analyzer；
        esClient.indices().create(new CreateIndexRequest.Builder().
                index(indexName)
                .mappings(indexState.mappings())
                .settings(set -> set.index(i -> i.analysis(an -> an.analyzer(indexState.settings().index().analysis().analyzer()))))
                .build());

    }

    @SneakyThrows
    private static IndexState backUpIndex(ElasticsearchClient esClient, final String esIndex) {
        GetIndexResponse response = esClient.indices().get(index -> index.index(esIndex));
        return response.get(esIndex);

    }

    private String getProtocol() {
        return Boolean.TRUE.equals(ssl) ? HTTPS : HTTP;
    }

    private ElasticsearchClient getElasticsearchClientHttp() {
        RestClient restClient = RestClient
                .builder(new HttpHost(getHost(), getPort(), getProtocol()))
                .setHttpClientConfigCallback(hc -> hc
                        .setDefaultCredentialsProvider(getBasicUserNameProvider())
                )
                .build();
        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
        return new ElasticsearchClient(transport);
    }


    private ElasticsearchClient getElasticsearchClientCA() {

        try {
            File certFile = new File(getCaCrtPath());
            SSLContext sslContext = TransportUtils
                    .sslContextFromHttpCaCrt(certFile);
            RestClient restClient = RestClient
                    .builder(new HttpHost(getHost(), getPort(), HTTPS))
                    .setHttpClientConfigCallback(hc -> hc
                            .setHostnameVerifier(getX59HostVerifier())
                            .setSSLContext(sslContext)
                            .setDefaultCredentialsProvider(getBasicUserNameProvider())
                    )
                    .build();
            // Create the transport and the API client
            ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
            return new ElasticsearchClient(transport);
        } catch (Exception e) {
            log.error("getElasticsearchClientCA", e);
            throw new BizException("es 连接失败");
        }

    }


    //Verifying HTTPS with a certificate fingerprint
    private ElasticsearchClient getElasticsearchClientFingerprint() {
        SSLContext sslContext = TransportUtils
                .sslContextFromCaFingerprint(getFingerprint());
        RestClient restClient = RestClient
                .builder(new HttpHost(getHost(), getPort(), HTTPS))
                .setHttpClientConfigCallback(hc -> hc
                        .setHostnameVerifier(getX59HostVerifier())
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(getBasicUserNameProvider())
                )
                .build();

        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, getJSONMapper());
        return new ElasticsearchClient(transport);
    }

    private X509HostnameVerifier getX59HostVerifier() {
        return new TrustHostnameVerifier(getHost());
    }


    @PreDestroy
    public void close() {
        try {
            if (restClient != null) {
                restClient.close();
            }
        } catch (IOException e) {
            log.error("close elasticsearch client", e);
        }
    }

    private JsonpMapper getJSONMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return new JacksonJsonpMapper(objectMapper);
    }

    private @NotNull BasicCredentialsProvider getBasicUserNameProvider() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(getUsername(), getPassword())
        );
        return provider;
    }
}
