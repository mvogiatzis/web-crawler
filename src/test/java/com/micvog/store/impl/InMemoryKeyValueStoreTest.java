package com.micvog.store.impl;

import com.micvog.store.KeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InMemoryKeyValueStoreTest {

    private KeyValueStore keyValueStore;
    private URL SAMPLE_URL_1 = new URL("http://example.com/page.html");
    private URL SAMPLE_URL_2 = new URL("http://example2.com/page2.html");

    @Before
    public void setUp(){
        keyValueStore = new InMemoryKeyValueStore();
    }

    public InMemoryKeyValueStoreTest() throws MalformedURLException {
    }

    @Test
    public void containsKey_ShouldReturnTrue_When_SampleUrlExists() throws Exception {
        checkStoredURLExists(SAMPLE_URL_1);
    }

    private void checkStoredURLExists(final URL sampleUrl) {
        keyValueStore.store(sampleUrl);
        assertTrue(keyValueStore.containsKey(sampleUrl));
    }

    @Test
    public void containsKey_ShouldReturnFalse_When_SampleUrlIsMissing() {
        assertFalse(keyValueStore.containsKey(SAMPLE_URL_2));
    }

    @Test
    public void containsKey_Should_Not_Contain_Nulls(){
        assertFalse(keyValueStore.containsKey(null));
    }


    @Test
    public void store_Should_Put_URL_Into_Storage() throws Exception {
        checkStoredURLExists(SAMPLE_URL_1);
    }

}