package com.micvog.crawler.impl;

import com.micvog.crawler.Parser;
import com.micvog.model.Page;
import com.micvog.model.ParsedContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class HtmlParser implements Parser {

    /**
     * Parses the content of the given page looking for any URLs that can be found.
     * For the sake of simplicity we ignore redirects and only consider 2xx as valid parsable URLs
     *
     * @param page
     * @return
     */
    @Override
    public Optional<ParsedContent> parse(final Page page) throws IOException, IllegalStateException {

        if (page.has2xxStatusCode() && page.isHTML()) {
                final Document doc = Jsoup.parse(EntityUtils.toString(page.getEntity()));
                Elements crawlableLinks = doc.select("a[href]"); // a with href
                Elements media = doc.select("[src]");
                Elements imports = doc.select("link[href]");
                return Optional.of(ParsedContent.builder()
                        .links(toUrl(crawlableLinks, "abs:href"))
                        .media(toUrl(media, "abs:src"))
                        .imports(toUrl(imports, "abs:href"))
                        .build());
        }
        return Optional.empty();
    }

    private URL toUrl(final String elemStr){
        try {
            return new URL(elemStr);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private List<URL> toUrl(final Elements elements, String attr) {
        if (elements == null) return Collections.emptyList();
        return elements.stream()
                .map(elem -> toUrl(elem.attr(attr)))
                .filter(url -> url != null)
                .collect(Collectors.toList());
    }

}
