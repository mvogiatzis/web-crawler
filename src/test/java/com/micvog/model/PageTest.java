package com.micvog.model;

import org.apache.http.entity.BasicHttpEntity;
import org.junit.Test;

import static org.junit.Assert.*;

public class PageTest {

    @Test
    public void isHTML_Should_Return_False_When_ContentTypeIs_Image() throws Exception {
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContentType("image");
        Page page = Page.builder()
                .entity(basicHttpEntity)
                .build();
        assertFalse(page.isHTML());
    }

    @Test
    public void isHTML_Should_Return_True_When_ContentTypeIs_TextHtml() throws Exception {
        final BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
        basicHttpEntity.setContentType("text/HTmL; charset=UTF-8");
        Page page = Page.builder()
                .entity(basicHttpEntity)
                .build();
        assertTrue(page.isHTML());
    }

}