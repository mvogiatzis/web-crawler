package com.micvog;

import com.micvog.crawler.impl.DomainCrawler;
import com.micvog.scheduler.impl.JobSchedulerImpl;
import com.micvog.sitemap.SitemapBuilder;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static final String WEBSITE_PROPERTY = "website";

    public static void main(String[] args) throws MalformedURLException, InterruptedException {

        final String domain = System.getProperty(WEBSITE_PROPERTY, "https://en.wikipedia.org/wiki/Dodo");

        URL domainToBeCrawled = new URL(domain);
        DomainCrawler domainCrawler = new DomainCrawler(domainToBeCrawled.getHost());
        JobSchedulerImpl.INSTANCE.schedule(domainToBeCrawled);
        Thread thread = new Thread(domainCrawler);
        //start crawling
        thread.start();

        printSitemapEveryXSeconds(5);
    }

    private static void printSitemapEveryXSeconds(final int interval) {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.info("==================== SITEMAP ====================");
                log.info(SitemapBuilder.INSTANCE.build());
                log.info("====================         ===================");
            }
        }, 0, interval, TimeUnit.SECONDS);
    }

}
