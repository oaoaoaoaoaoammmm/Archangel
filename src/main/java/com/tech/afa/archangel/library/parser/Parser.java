package com.tech.afa.archangel.library.parser;

import com.tech.afa.archangel.library.model.SQLRequest;

public interface Parser {

    SQLRequest parse(String sql);
}
