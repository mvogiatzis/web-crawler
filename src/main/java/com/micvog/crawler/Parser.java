package com.micvog.crawler;

import com.micvog.model.Page;
import com.micvog.model.ParsedContent;

import java.io.IOException;
import java.util.Optional;

public interface Parser {

    Optional<ParsedContent> parse(Page page) throws IOException, IllegalStateException;

}
