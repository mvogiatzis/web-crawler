package com.micvog.crawler.impl;

import com.micvog.crawler.Fetcher;
import com.micvog.crawler.Parser;
import com.micvog.model.Page;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PageFetcherTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse mockHttpResponse;

    @Mock
    private StatusLine mockStatusLine;

    private Fetcher fetcher;

    private URL url;

    @Before
    public void setUp() throws MalformedURLException {
        initMocks(this);
        fetcher = new PageFetcher(mockHttpClient);
        url = new URL("http://www.google.com/");
    }

    @Test
    public void fetch_Should_GetContentWhen_HttpResponse_IsValid() throws IOException, URISyntaxException {

        //mock request
        final BasicHttpEntity anEntity = new BasicHttpEntity();
        when(mockHttpClient.execute(any())).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(anEntity);
        when(mockHttpResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockStatusLine.getStatusCode()).thenReturn(new Integer(200));

        //do fetch
        final Optional<Page> fetch = fetcher.fetch(url);
        assertTrue(fetch.isPresent());
        assertEquals(anEntity, fetch.get().getEntity());
        assertEquals(new Integer(200), fetch.get().getStatusCode());
    }

    @Test
    public void fetch_Should_ThrowException_When_HttpGetFails() throws IOException {
        when(mockHttpClient.execute(any())).thenThrow(IOException.class);
        final Optional<Page> fetch = fetcher.fetch(url);
        assertFalse(fetch.isPresent());
    }

//    //real world fetch
//    @Test
//    public void fetch() throws Exception {
//        Fetcher fetcher = new PageFetcher();
//        Parser parser = new HtmlParser();
//        final Optional<Page> fetch = fetcher.fetch(new URL("http://www.rodiaki.gr/"));
//
//        System.out.println();
//        fetch.ifPresent(page -> {
//            try {
//                parser.parse(page);
//                final String s = EntityUtils.toString(page.getEntity());
//                System.out.println(s);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }

}