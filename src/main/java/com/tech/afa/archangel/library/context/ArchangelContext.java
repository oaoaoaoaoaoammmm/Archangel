package com.tech.afa.archangel.library.context;

import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.table.Table;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArchangelContext {

    private final Map<String, SQLRequest> requests;

    private final Map<String, Table> tables;

    public ArchangelContext(Map<String, Table> tables) {
        this.requests = new ConcurrentHashMap<>();
        this.tables = new ConcurrentHashMap<>(tables);
        tables.forEach(((s, table) -> System.out.println(table.toString())));
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public SQLRequest getRequest(String id) {
        requests.forEach(((s, table) -> System.out.println(table.toString())));
        return requests.get(id);
    }

    public SQLRequest putRequest(String id, SQLRequest request) {
        requests.forEach(((s, table) -> System.out.println(table.toString())));
        return requests.put(id, request);
    }

    public SQLRequest removeRequest(String id) {
        requests.forEach(((s, table) -> System.out.println(table.toString())));
        return requests.remove(id);
    }
}
