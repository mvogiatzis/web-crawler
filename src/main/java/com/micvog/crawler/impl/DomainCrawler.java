package com.micvog.crawler.impl;

import com.micvog.crawler.Crawler;
import com.micvog.crawler.Fetcher;
import com.micvog.model.Page;
import com.micvog.model.ParsedContent;
import com.micvog.scheduler.JobScheduler;
import com.micvog.scheduler.impl.JobSchedulerImpl;
import com.micvog.sitemap.SitemapBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * A web crawler responsible for visiting all pages within a domain
 */
@AllArgsConstructor
@Slf4j
public class DomainCrawler implements Crawler, Runnable {

    private String host;
    private Fetcher fetcher;
    private HtmlParser htmlParser;
    private JobScheduler jobScheduler;
    private SitemapBuilder sitemapBuilder;

    public DomainCrawler(String host) {
        this.htmlParser = new HtmlParser();
        this.fetcher = new PageFetcher();
        this.jobScheduler = JobSchedulerImpl.INSTANCE;
        this.sitemapBuilder = SitemapBuilder.INSTANCE;
        this.host = host;
    }

    /**
     * Continuously crawling for new URLs and updates. The process is as follows:
     * 1. Get new URL task from the job scheduler, or else sleep for a while.
     * 2. Fetch content of URL
     * 3. Parse the HTML if successful and populate the sitemap builder with any links found
     * 4. Notify the job scheduler about new URLs that need crawling (of the same domain)
     * @throws InterruptedException
     */
    public void crawl() throws InterruptedException {

        while (true) {
            final URL url = jobScheduler.getNextUrl();
            if (url != null) {
                crawl(url);
            } else { // URL not available
                Thread.sleep(2000L);
            }
        }

    }

    public void crawl(URL url){
        final Optional<Page> pageOpt = fetcher.fetch(url);
        pageOpt.ifPresent(page -> {
            final Optional<ParsedContent> parsedContentOpt = parseHTML(page);

            sitemapBuilder.add(url);
            parsedContentOpt.ifPresent(parsedContent -> {
                sitemapBuilder.add(parsedContent);
                //sending next tasks to job scheduler
                parsedContent.getLinks().stream()
                        .filter(this::shouldCrawl)
                        .forEach(schedulableUrl -> jobScheduler.schedule(schedulableUrl));
            });

        });
        jobScheduler.finished(pageOpt.orElse(errorPage(url)));
    }


    /**
     * Determines whether the given URL should be crawled according to the requirements of this crawler.
     * DomainCrawler only crawls same host URLs.
     *
     * @param url Potential URL to be crawled
     * @return True if of the same domain, false otherwise
     */
    public boolean shouldCrawl(final URL url) {
        return url.getHost().equalsIgnoreCase(host);
    }

    @Override
    public void run() {
        try {
            crawl();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    private Optional<ParsedContent> parseHTML(final Page page) {
        try {
            return htmlParser.parse(page);
        } catch (IOException | IllegalStateException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Page errorPage(final URL url) {
        return Page.builder().statusCode(500).uri(url.toString()).build();
    }



}
