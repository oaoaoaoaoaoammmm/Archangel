package com.tech.afa.archangel.library.model;

public record SQLRequestView(String id, String sql, long executeTime) {

    public SQLRequestView(String id, String sql) {
        this(id, sql, 0);
    }
}
