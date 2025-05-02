package com.tech.afa.archangel.library.interceptor;

public interface Interceptor {

    void intercept(String sql);

    void intercept(String sql, String nativeSql);

    void intercept(String sql, long executeTime);

    void intercept(String sql, String nativeSql, long executeTime);
}
