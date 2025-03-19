package com.tech.afa.archangel.library.interceptor;

import com.tech.afa.archangel.library.processor.Processor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class InterceptorImpl implements Interceptor {

    private final Processor processor;

    public void intercept(String sql) {
        processor.processing(sql);
    }
}
