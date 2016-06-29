package com.micvog.crawler;

import com.micvog.model.Page;
import java.net.URL;
import java.util.Optional;

public interface Fetcher {

    Optional<Page> fetch(URL url);
}
