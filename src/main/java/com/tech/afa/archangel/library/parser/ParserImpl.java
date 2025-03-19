package com.tech.afa.archangel.library.parser;

import com.tech.afa.archangel.library.model.SQLRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ParserImpl implements Parser {

    @Override
    public SQLRequest parse(String sql) {
        return new SQLRequest(sql);
    }
}
