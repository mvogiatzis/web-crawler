package com.micvog.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class HttpClientFactory {

    /**
     * Creates an HttpClient from pre-configured settings.
     * In a production system such settings would not be constants (i.e. in properties files)
     * <p>
     * If parallel processing and multiple http connections are required then a pooling http client connection manager
     * is require (https://hc.apache.org/httpcomponents-client-ga/httpclient/examples/org/apache/http/examples/client/ClientConfiguration.java)
     *
     * @return
     */
    public static HttpClient create() {

        //ignore ssl certificates
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, (x509Certificates, s) -> true);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    builder.build());

            return HttpClientBuilder
                    .create()
                    .setSSLSocketFactory(sslsf)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error(e.getMessage());
            return HttpClientBuilder.create().build();
        }

    }

}
