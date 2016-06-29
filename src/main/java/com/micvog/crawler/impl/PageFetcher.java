package com.micvog.crawler.impl;

import com.micvog.crawler.Fetcher;
import com.micvog.http.HttpClientFactory;
import com.micvog.model.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Slf4j
public class PageFetcher implements Fetcher {

    private HttpClient httpClient;

    public PageFetcher() {
        this.httpClient = HttpClientFactory.create();
    }

    public PageFetcher(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Optional<Page> fetch(final URL url) {

        try {
            final HttpResponse httpResponse = httpClient.execute(httpGet(url));
            return Optional.of(Page.builder()
                    .statusCode(httpResponse.getStatusLine().getStatusCode())
                    .entity(httpResponse.getEntity())
                    .uri(url.toURI().toString())
                    .build());
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            System.out.println("Error " + e.getMessage());
            return Optional.empty();
        }
    }

    private HttpUriRequest httpGet(final URL url) throws URISyntaxException {
        final HttpGet httpGet = new HttpGet(url.toURI());
        return httpGet;
    }

}
