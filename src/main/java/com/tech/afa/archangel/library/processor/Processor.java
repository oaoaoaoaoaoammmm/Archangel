package com.tech.afa.archangel.library.processor;

import com.tech.afa.archangel.library.model.SQLRequestView;

public interface Processor {

    void processing(SQLRequestView sql);
}
