package com.micvog.sitemap;

import com.micvog.model.URLInfo;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class SitemapPrinterTest {


    String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
            "\t<url>\n" +
            "\t\t<loc>http://s3.images.com/image1.gif</loc>\n" +
            "\t</url>\n" +
            "\t<url>\n" +
            "\t\t<loc>http://s3.images.com/image1.png</loc>\n" +
            "\t</url>\n" +
            "\t<url>\n" +
            "\t\t<loc>http://www.google.com/</loc>\n" +
            "\t</url>\n" +
            "</urlset>\n";

    public static String defaultXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
            "</urlset>\n";

    @Test
    public void makeSitemap() throws Exception {
        SitemapPrinter sitemapPrinter = new SitemapPrinter();
        Map<String, URLInfo> urls = new TreeMap<>();
        urls.put("http://s3.images.com/image1.png", new URLInfo());
        urls.put("http://www.google.com/", new URLInfo());
        urls.put("http://s3.images.com/image1.gif", new URLInfo());
        final String sitemap = sitemapPrinter.makeSitemap(urls);
        assertEquals(expected, sitemap);
    }

    @Test
    public void makeSitemap_ShouldReturn_Default_XML_When_Map_Empty(){
        SitemapPrinter sitemapPrinter = new SitemapPrinter();
        final String sitemap = sitemapPrinter.makeSitemap(new HashMap<>());
        assertEquals(defaultXML, sitemap);
    }

}