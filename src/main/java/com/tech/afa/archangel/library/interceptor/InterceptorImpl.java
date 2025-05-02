package com.tech.afa.archangel.library.interceptor;

import com.tech.afa.archangel.library.model.SQLRequestView;
import com.tech.afa.archangel.library.processor.Processor;
import com.tech.afa.archangel.library.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InterceptorImpl implements Interceptor {

    private final Processor processor;

    private final IdGenerator idGenerator;

    @Override
    public void intercept(String sql) {
        String id = idGenerator.getId(sql);
        processor.processing(new SQLRequestView(id, sql, 0));
    }

    @Override
    public void intercept(String sql, String nativeSql) {
        String id = idGenerator.getId(sql);
        processor.processing(new SQLRequestView(id, nativeSql, 0));
    }

    @Override
    public void intercept(String sql, long executeTime) {
        String id = idGenerator.getId(sql);
        processor.processing(new SQLRequestView(id, sql, executeTime));
    }

    public void intercept(String sql, String nativeSql, long executeTime) {
        String id = idGenerator.getId(sql);
        processor.processing(new SQLRequestView(id, nativeSql, executeTime));
    }
}
