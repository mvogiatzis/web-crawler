package com.micvog.model;

import lombok.Builder;
import lombok.Data;
import org.apache.http.HttpEntity;

@Data
@Builder
public class Page {

    private HttpEntity entity;
    private String uri;
    private Integer statusCode;

    public boolean isHTML() {
        return entity != null && entity.getContentType().getValue().toLowerCase().contains("text/html");
    }

    public boolean has2xxStatusCode() {
        return statusCode != null && (statusCode >= 200 && statusCode <= 299);
    }

}
