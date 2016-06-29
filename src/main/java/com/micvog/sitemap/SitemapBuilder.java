package com.micvog.sitemap;

import com.micvog.model.ParsedContent;
import com.micvog.store.KeyValueStore;
import com.micvog.store.impl.InMemoryKeyValueStore;
import lombok.AllArgsConstructor;

import java.net.URL;
import java.util.List;

@AllArgsConstructor
public class SitemapBuilder {

    public static final SitemapBuilder INSTANCE = new SitemapBuilder();

    private KeyValueStore keyValueStore;
    private SitemapPrinter sitemapPrinter;

    private SitemapBuilder(){
        keyValueStore = new InMemoryKeyValueStore();
        sitemapPrinter = new SitemapPrinter();
    }

    public void add(URL url){
        if (url != null)
            keyValueStore.store(url);
    }

    public boolean contains(URL url){
        return keyValueStore.containsKey(url);
    }

    public void add(final ParsedContent parsedContent) {
        distinctAdd(parsedContent.getImports());
        distinctAdd(parsedContent.getLinks());
        distinctAdd(parsedContent.getMedia());
    }

    /**
     * Builds a sitemap by calling the printer function
     * @return
     */
    public String build(){
        return sitemapPrinter.makeSitemap(keyValueStore.getAll());
    }

    private void distinctAdd(final List<URL> imports) {
        imports.stream().distinct().forEach(this::add);
    }
}
