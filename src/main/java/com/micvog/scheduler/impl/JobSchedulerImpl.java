package com.micvog.scheduler.impl;

import com.micvog.model.Page;
import com.micvog.scheduler.JobScheduler;
import com.micvog.store.KeyValueStore;
import com.micvog.store.impl.InMemoryKeyValueStore;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Job Scheduler is responsible for queueing up URLs that need to be crawled and serving the crawlers
 * with available tasks.
 *
 * It uses a key value store for storage of the known / already crawled ones.
 */
@Slf4j
@AllArgsConstructor
public class JobSchedulerImpl implements JobScheduler {

    public final static JobSchedulerImpl INSTANCE = new JobSchedulerImpl();

    private KeyValueStore keyValueStore;
    private Queue<URL> jobs;

    //singleton
    private JobSchedulerImpl() {
        keyValueStore = new InMemoryKeyValueStore();
        jobs = new LinkedBlockingQueue<>(1000);
    }

    @Override
    public void schedule(final URL url) {
        if (url != null && !keyValueStore.containsKey(url)){
//            System.out.println("Scheduling " + url.toString());
            jobs.offer(url);
        }
    }

    @Override
    public URL getNextUrl() {
        final URL poll = jobs.poll();
        return poll;
    }

    @Override
    public void finished(final Page page) {
        if (!page.has2xxStatusCode()){
            try {
                schedule(new URL(page.getUri()));
            } catch (MalformedURLException e) {
                log.warn("Malformed URL: " + e.getMessage());
            }
        }
    }
}
