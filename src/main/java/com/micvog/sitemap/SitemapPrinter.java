package com.micvog.sitemap;

import com.micvog.model.URLInfo;
import java.util.Map;

public class SitemapPrinter {

    public String makeSitemap(final Map<String, URLInfo> urls){
        String sitemapXmlStartTags = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n";

        StringBuilder stringBuilder = new StringBuilder(sitemapXmlStartTags);
        //populate
        for (Map.Entry<String, URLInfo> url : urls.entrySet()){
            stringBuilder.append("\t<url>\n");
            stringBuilder.append("\t\t<loc>");
            stringBuilder.append(url.getKey());
            stringBuilder.append("</loc>\n");
            stringBuilder.append("\t</url>\n");
        }

        stringBuilder.append("</urlset>\n");
        return stringBuilder.toString();
    }

}
