package com.micvog.scheduler.impl;

import com.micvog.model.ParsedContent;
import com.micvog.sitemap.SitemapBuilder;
import com.micvog.sitemap.SitemapPrinter;
import com.micvog.sitemap.SitemapPrinterTest;
import com.micvog.store.impl.InMemoryKeyValueStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SitemapBuilderTest {

    private SitemapBuilder sitemapBuilder;

    @Before
    public void setUp(){
        //to not use the singleton in tests
        sitemapBuilder = new SitemapBuilder(new InMemoryKeyValueStore(), new SitemapPrinter());
    }

    @Test
    public void add_Should_Store_Given_URL() throws Exception {
        URL url = new URL("http://www.google.com/");
        sitemapBuilder.add(url);
        assertTrue(sitemapBuilder.contains(url));
    }

    @Test
    public void add_Should_Not_Store_Null_URL(){
        URL url = null;
        sitemapBuilder.add(url);
        assertFalse(sitemapBuilder.contains(url));
    }

    @Test
    public void addParsedContent_Should_Store_AllRelevantContent() throws Exception {
        URL link1 = new URL("http://www.google.com/");
        URL link2 = new URL("http://www.link2.com/");
        URL image1 = new URL("http://www.google.com/image1.png");
        URL import1 = new URL("http://www.google.com/basic.css");
        URL import2 = new URL("http://www.google.com/basic.css");

        ParsedContent parsedContent = ParsedContent.builder()
                .links(Arrays.asList(link1, link2))
                .imports(Arrays.asList(import1, import2))
                .media(Arrays.asList(image1))
                .build();
        sitemapBuilder.add(parsedContent);
        assertTrue(sitemapBuilder.contains(link1));
        assertTrue(sitemapBuilder.contains(link2));
        assertTrue(sitemapBuilder.contains(image1));
        assertTrue(sitemapBuilder.contains(import1));
        assertTrue(sitemapBuilder.contains(import2));
    }

    @Test
    public void build_Should_ReturnCorrectResult_With_Empty_KeyValueStore(){
        assertEquals(SitemapPrinterTest.defaultXML, sitemapBuilder.build());
    }

}