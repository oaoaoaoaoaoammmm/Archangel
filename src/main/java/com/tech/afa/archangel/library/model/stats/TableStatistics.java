package com.tech.afa.archangel.library.model.stats;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class TableStatistics {
    private final String tableName;
    private final AtomicLong countTableUsage;
    private final Set<String> usableFieldsName;
    private final Set<String> usableIndexesName;

    public TableStatistics(String tableName) {
        this.tableName = tableName;
        this.countTableUsage = new AtomicLong(0);
        this.usableFieldsName = new HashSet<>();
        this.usableIndexesName = new HashSet<>();
    }

    public long incrementRequestCountAndGet() {
        return this.countTableUsage.incrementAndGet();
    }

    public boolean addUsableField(String fieldName) {
        return this.usableFieldsName.add(fieldName);
    }

    public boolean addUsableIndex(String indexName) {
        return this.usableIndexesName.add(indexName);
    }

    public boolean hasUsableField(String fieldName) {
        return this.usableFieldsName.contains(fieldName);
    }

    public boolean hasUsableIndex(String indexName) {
        return this.usableIndexesName.contains(indexName);
    }

    @Override
    public String toString() {
        return String.format(
            """
            {
                Table Name: %s
                Table Usage Count: %d
                Table Usable Fields: %s
                Table Usable Indexes: %s
            }""",
            this.tableName,
            countTableUsage.get(),
            usableFieldsName.toString(),
            usableIndexesName.toString()
        );
    }
}
