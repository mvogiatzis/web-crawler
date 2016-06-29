package com.micvog.crawler.impl;

import com.micvog.crawler.Crawler;
import com.micvog.crawler.Fetcher;
import com.micvog.model.Page;
import com.micvog.model.ParsedContent;
import com.micvog.scheduler.JobScheduler;
import com.micvog.sitemap.SitemapBuilder;
import com.micvog.sitemap.SitemapPrinter;
import com.micvog.store.impl.InMemoryKeyValueStore;
import lombok.ToString;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DomainCrawlerTest {

    private DomainCrawler domainCrawler;

    @Mock
    private PageFetcher fetcher;

    @Mock
    private JobScheduler jobScheduler;

    @Mock
    private HtmlParser htmlParser;

    private SitemapBuilder sitemapBuilder;

    @Before
    public void setUp() {
        initMocks(this);
        sitemapBuilder = new SitemapBuilder(new InMemoryKeyValueStore(), new SitemapPrinter());
        domainCrawler = new DomainCrawler("example.com", fetcher, htmlParser, jobScheduler,
                sitemapBuilder);
    }

    @Test
    public void shouldCrawl_ShouldReturn_True_ForSameDomainURLs() throws MalformedURLException {
        assertTrue(domainCrawler.shouldCrawl(new URL("http://example.com/page.html")));
        assertTrue(domainCrawler.shouldCrawl(new URL("http://example.com")));
        assertTrue(domainCrawler.shouldCrawl(new URL("https://example.com")));
    }

    @Test
    public void shouldCrawl_Should_Return_False_ForDifferentDomain() throws MalformedURLException {
        assertFalse(domainCrawler.shouldCrawl(new URL("http://adifferentdomain.com/page.html")));
    }

    @Test
    public void crawl_Should_Add_Content_ToTheSitemapBuilder() throws IOException, URISyntaxException {
        final URL domainToCrawl = new URL("https://example.com/");
        final URL about = new URL("https://example.com/about.html");
        final URL contact = new URL("https://example.com/contact.html");
        final URL image = new URL("https://example.com/image.png");
        Page expectedPageFetched = Page
                .builder()
                .uri(domainToCrawl.toURI().toString())
                .statusCode(200)
                .build();
        when(fetcher.fetch(domainToCrawl)).thenReturn(Optional.of(expectedPageFetched));
        ParsedContent expectedParsedContent = ParsedContent.builder()
                .links(Arrays.asList(about, contact))
                .media(Arrays.asList(image))
                .imports(Arrays.asList())
                .build();
        when(htmlParser.parse(expectedPageFetched)).thenReturn(Optional.of(expectedParsedContent));

        //act
        domainCrawler.crawl(domainToCrawl);

        assertTrue(sitemapBuilder.contains(domainToCrawl));
        assertTrue(sitemapBuilder.contains(about));
        assertTrue(sitemapBuilder.contains(contact));
        assertTrue(sitemapBuilder.contains(image));
        //verify job scheduler receives next tasks
        verify(jobScheduler).schedule(about);
        verify(jobScheduler).schedule(contact);
        verify(jobScheduler).finished(expectedPageFetched);
        verifyNoMoreInteractions(jobScheduler);
    }

    @Test
    public void crawl_Should_Not_Add_Any_Content_ToSitemapBuilder_ApartFromCurrURL_When_ParseExceptionOccurs() throws IOException {
        final URL domainToCrawl = new URL("https://example.com/");
        Page faultyPage = Page.builder().statusCode(404).build();
        when(fetcher.fetch(domainToCrawl)).thenReturn(Optional.of(faultyPage));
        when(htmlParser.parse(faultyPage)).thenThrow(IOException.class);

        domainCrawler.crawl(domainToCrawl);

        verify(jobScheduler).finished(faultyPage);
        verifyNoMoreInteractions(jobScheduler);
        assertTrue(sitemapBuilder.contains(domainToCrawl));
    }


}