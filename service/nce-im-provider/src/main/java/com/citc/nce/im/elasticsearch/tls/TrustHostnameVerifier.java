package com.citc.nce.im.elasticsearch.tls;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.AbstractVerifier;

import javax.net.ssl.SSLException;


/**
 * 自定义信任的host主机
 */
@Slf4j
public class TrustHostnameVerifier extends AbstractVerifier {
    private final String host;

    public TrustHostnameVerifier(String url) {
        host = url;
    }

    @Override
    public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
        if (!host.equals(this.host)) {
            throw new SSLException("Hostname verification failed");
        }
    }
}
