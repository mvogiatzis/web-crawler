package com.micvog.scheduler.impl;

import com.micvog.model.Page;
import com.micvog.scheduler.JobScheduler;
import com.micvog.store.KeyValueStore;
import com.micvog.store.impl.InMemoryKeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.*;

public class JobSchedulerImplTest {

    private JobScheduler jobScheduler;

    private KeyValueStore keyValueStore;
    private Queue<URL> queue;

    @Before
    public void setUp(){
        keyValueStore = new InMemoryKeyValueStore();
        queue = new LinkedBlockingQueue<>(1000);
        jobScheduler = new JobSchedulerImpl(keyValueStore, queue);
    }

    @Test
    public void schedule_ShouldInsertTasks_Into_The_Queue_When_Not_In_KeyStore() throws Exception {
        final URL url = new URL("http://www.google.com");
        jobScheduler.schedule(url);
        assertEquals(queue.peek(), url);
    }

    @Test
    public void schedule_ShouldNot_InsertTasks_Into_The_Queue_When_Not_In_KeyStore() throws Exception {
        final URL url = new URL("http://www.google.com");
        keyValueStore.store(url);
        jobScheduler.schedule(url);
        assertNull(queue.peek());
    }

    @Test
    public void getNextUrl_Should_Return_The_Next_URL_ToProcess() throws Exception {
        final URL url1 = new URL("http://www.google.com");
        final URL url2 = new URL("http://www.someotherwebsite.com");
        jobScheduler.schedule(url1);
        jobScheduler.schedule(url2);
        assertEquals(url1, jobScheduler.getNextUrl());
    }

    @Test
    public void getNextUrl_Should_Return_Null_When_QueueIsEmpty(){
        assertTrue(queue.isEmpty());
        assertNull(jobScheduler.getNextUrl());
    }

    @Test
    public void finished_Should_RequeueURL_If_Failed_To_Be_Processed() throws MalformedURLException, URISyntaxException {
        final URL url1 = new URL("http://www.google.com");
        Page page = Page.builder()
                .statusCode(404)
                .uri(url1.toURI().toString())
                .build();
        jobScheduler.finished(page);
        assertEquals(url1, queue.peek());
    }

    @Test
    public void finished_Should_Not_RequeueURL_If_Already_Processed() throws MalformedURLException, URISyntaxException {
        final URL url1 = new URL("http://www.google.com");
        Page page = Page.builder()
                .statusCode(404)
                .uri(url1.toURI().toString())
                .build();
        keyValueStore.store(url1);
        jobScheduler.finished(page);
        assertNull(queue.peek());
    }

    @Test
    public void finished_Should_NotRequeueURL_When_Malformed() throws MalformedURLException, URISyntaxException {
        Page page = Page.builder()
                .statusCode(404)
                .uri("malformed")
                .build();
        jobScheduler.finished(page);
        assertNull(queue.peek());
    }

}