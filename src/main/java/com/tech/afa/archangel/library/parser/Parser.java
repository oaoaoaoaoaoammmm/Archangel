package com.tech.afa.archangel.library.parser;

import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.model.request.SQLRequest;
import net.sf.jsqlparser.JSQLParserException;

public interface Parser {

    SQLRequest parse(SQLRequestView sql) throws JSQLParserException;
}
