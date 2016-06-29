package com.micvog.store;

import com.micvog.model.URLInfo;

import java.net.URL;
import java.util.Map;

public interface KeyValueStore {

    boolean containsKey(URL url);

    URLInfo store(URL url);

    /**
     * Should not be invoked in real key value systems
     * @return
     */
    Map<String,URLInfo> getAll();
}
