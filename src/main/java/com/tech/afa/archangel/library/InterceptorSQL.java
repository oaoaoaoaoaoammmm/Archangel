package com.tech.afa.archangel.library;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InterceptorSQL implements Interceptor{

    public void intercept(String sql) {
        System.out.println(sql);
    }
}
