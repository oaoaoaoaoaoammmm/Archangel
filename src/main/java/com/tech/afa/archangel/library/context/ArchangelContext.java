package com.tech.afa.archangel.library.context;

import com.tech.afa.archangel.library.model.request.SQLRequest;
import com.tech.afa.archangel.library.model.stats.RequestStatistics;
import com.tech.afa.archangel.library.model.table.Table;
import com.tech.afa.archangel.library.utils.Pair;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArchangelContext {

    private final Map<String, Table> tables;

    private final Map<String, Pair<SQLRequest, RequestStatistics>> requests;

    public ArchangelContext(@NonNull Map<String, Table> tables) {
        this.requests = new ConcurrentHashMap<>();
        this.tables = new ConcurrentHashMap<>(tables);
        tables.forEach(((s, table) -> System.out.println(table.toString())));
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

    /*
    public void put(@NonNull String id, @NonNull SQLRequest request) {
        Statistics statistics = getStatistics(id);
        if (statistics == null) statistics = new Statistics();
        requests.put(id, new Pair<>(request, statistics));
    }
     */

    public Pair<SQLRequest, RequestStatistics> put(@NonNull String id, @NonNull SQLRequest request, @NonNull RequestStatistics requestStatistics) {
        //if (!requests.containsKey(id)) {
            Pair<SQLRequest, RequestStatistics> pair = new Pair<>(request, requestStatistics);
            requests.put(id, pair);
            return pair;
        //}
    }
}
