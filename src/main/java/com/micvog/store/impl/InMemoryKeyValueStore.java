package com.micvog.store.impl;

import com.micvog.model.URLInfo;
import com.micvog.store.KeyValueStore;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple implementation of a concurrent in-memory key value store. Does not permit storing null URLs.
 *
 */
public class InMemoryKeyValueStore implements KeyValueStore {

    private final Map<String, URLInfo> keyStore;

    public InMemoryKeyValueStore() {
        this.keyStore = new ConcurrentHashMap<>(1000);
    }

    public boolean containsKey(final URL url) {
        if (url == null)
            return false;

        return keyStore.containsKey(url.toString().toLowerCase());
    }

    /**
     * Stores the given URL into the keystore. Idempotent operations have no impact, just replacing a URL that already exists
     * @param url
     * @return Returns the most recent URL stored
     */
    public URLInfo store(final URL url) {
        return url == null ? null : keyStore.put(url.toString().toLowerCase(), new URLInfo());
    }

    /**
     * Retrieves all urls stored in the key store
     * @return
     */
    @Override
    public Map<String, URLInfo> getAll() {
        return this.keyStore;
    }

}
