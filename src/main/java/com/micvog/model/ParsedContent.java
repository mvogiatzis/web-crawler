package com.micvog.model;

import lombok.Builder;
import lombok.Data;
import java.net.URL;
import java.util.List;

@Data
@Builder
public class ParsedContent {

    private List<URL> links;
    private List<URL> media;
    private List<URL> imports;

}
