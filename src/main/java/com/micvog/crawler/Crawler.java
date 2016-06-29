package com.micvog.crawler;

import java.net.URL;

/**
 * Interface for crawling a given url
 */
public interface Crawler {

    void crawl() throws InterruptedException;

    boolean shouldCrawl(URL url);

}
