package com.tech.afa.archangel.library.context;

import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.schemaloader.SchemaLoader;
import com.tech.afa.archangel.library.utils.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ArchangelContext {

    private final SchemaRefresher schemaRefresher;

    private final Map<String, Table> tables;

    private final Map<String, Pair<SQLRequest, RequestStatistics>> requests;

    public ArchangelContext(long delay, SchemaLoader schemaLoader) {
        this.schemaRefresher = new SchemaRefresher(delay, schemaLoader);
        this.requests = new ConcurrentHashMap<>();
        this.tables = new ConcurrentHashMap<>();
        this.schemaRefresher.startRefreshSchema(false);
    }

    public Table getTable(@NonNull String name) {
        return tables.get(name);
    }

    public SQLRequest getRequest(@NonNull String id) {
        Pair<SQLRequest, RequestStatistics> pair = requests.get(id);
        return pair != null ? pair.first() : null;
    }

    public RequestStatistics getStatistics(@NonNull String id) {
        Pair<SQLRequest, RequestStatistics> pair = requests.get(id);
        return pair != null ? pair.second() : null;
    }

    public Pair<SQLRequest, RequestStatistics> getPair(@NonNull String id) {
        return requests.get(id);
    }

    public Pair<SQLRequest, RequestStatistics> put(@NonNull String id, @NonNull SQLRequest request, @NonNull RequestStatistics requestStatistics) {
        //if (!requests.containsKey(id)) {
        Pair<SQLRequest, RequestStatistics> pair = new Pair<>(request, requestStatistics);
        requests.put(id, pair);
        return pair;
        //}
    }

    public void forceRefreshSchema() {
        this.schemaRefresher.startRefreshSchema(true);
    }

    @RequiredArgsConstructor
    private class SchemaRefresher {
        private final long delay;
        private final SchemaLoader schemaLoader;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        private void startRefreshSchema(boolean skipSchedulingNewTask) {
            Map<String, Table> updatedTables = schemaLoader.loadSchema();
            tables.forEach((k, v) -> updatedTables.computeIfPresent(k, (key, table) -> {
                table.setStatistics(v.getStatistics());
                return table;
            }));
            tables.putAll(updatedTables);
            StringBuilder sb = new StringBuilder();
            tables.forEach(((s, table) -> sb.append(table.toString())));
            log.info("Storage structure: \n{}", sb.toString());
            if (!skipSchedulingNewTask) {
                executor.schedule(() -> this.startRefreshSchema(false), delay, TimeUnit.SECONDS);
            }
        }
    }
}
