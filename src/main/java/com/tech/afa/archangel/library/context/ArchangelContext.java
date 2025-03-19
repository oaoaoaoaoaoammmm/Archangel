package com.tech.afa.archangel.library.context;

import com.tech.afa.archangel.library.model.SQLRequest;
import com.tech.afa.archangel.library.model.Table;

import java.util.HashMap;
import java.util.Map;

public class ArchangelContext {

    private final Map<Integer, SQLRequest> cache;

    private final Map<String, Table> tablesCache;

    public ArchangelContext(Map<String, Table> tables) {
        this.cache = new HashMap<>();
        this.tablesCache = tables;

        tablesCache.forEach(((s, table) -> System.out.println(table.toString())));
    }
}
