package com.micvog.scheduler;

import com.micvog.model.Page;

import java.net.URL;

public interface JobScheduler {

    void schedule(URL url);

    URL getNextUrl();

    void finished(Page page);
}
