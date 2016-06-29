package com.micvog.crawler.impl;

import com.micvog.model.Page;
import com.micvog.model.ParsedContent;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class HtmlParserTest {

    private String someHtml = "<html><head><title>First parse</title></head>" +
            "<body><p>Parsed HTML into a doc.</p>" +
            "<a href=\"http://www.google.com\">Click here for Google</a>" +
            "<img src=\"http://awebsite.com/photo.png\" />" +
            "<link rel=\"pingback\" href=\"https://micvog.com/xmlrpc.php\">" +
            "</body></html>;";

    @Test
    public void parse_Should_Extract_Links_OutOfAnHtml_Page() throws Exception {
        final Optional<ParsedContent> parsedContent = parse(someHtml, 200);
        //asset parsed content exists
        assertTrue(parsedContent.isPresent());
        //assert links exist
        final List<URL> links = parsedContent.get().getLinks();
        assertTrue(links.size() == 1);
        assertEquals(links.get(0), new URL("http://www.google.com"));
        assertEquals(parsedContent.get().getMedia().get(0), new URL("http://awebsite.com/photo.png"));
        assertEquals(parsedContent.get().getImports().get(0), new URL("https://micvog.com/xmlrpc.php"));
    }

    @Test
    public void parse_Should_DoNothing_IfPageIsNot2xx() throws Exception {
        final Optional<ParsedContent> parsedContent = parse(someHtml, 404);
        assertFalse(parsedContent.isPresent());
    }

    //missing feature
    @Test
    public void parse_ShouldNotExtractLinks_When_RelativePathsExist() throws IOException {
        String html = "<html><head><title>First parse</title></head>" +
                "<body><p>Parsed HTML into a doc.</p>" +
                "<a href=\"/help/articles/how-do-i-set-up-a-webpage.html\">Click here for Google</a>" +
                "</body></html>;";
        final Optional<ParsedContent> parsedContent = parse(html, 200);
        assertTrue(parsedContent.isPresent());
        assertTrue(parsedContent.get().getLinks().isEmpty());
    }

    @Test
    public void parse_ShouldWork_When_InvalidHtmlIsFound() throws IOException {
        String invalidHtml = "<head><title>Some Document";
        final Optional<ParsedContent> parse = parse(invalidHtml, 200);
        assertTrue(parse.isPresent());
        assertNoContent(parse.get());
    }

    private void assertNoContent(final ParsedContent parsedContent) {
        assertTrue(parsedContent.getLinks().isEmpty());
        assertTrue(parsedContent.getImports().isEmpty());
        assertTrue(parsedContent.getMedia().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void parse_Should_Throw_Exception_When_Entity_Empty() throws IOException {
        parse(null, 200);
    }

    private Optional<ParsedContent> parse(String html, final int statusCode) throws IOException {
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        if (html != null) basicHttpEntity.setContent(new ByteArrayInputStream(html.getBytes()));
        basicHttpEntity.setContentType("text/html");
        final Page page = Page.builder().entity(basicHttpEntity).statusCode(statusCode).build();
        return new HtmlParser().parse(page);
    }

}