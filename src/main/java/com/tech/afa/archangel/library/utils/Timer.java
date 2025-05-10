package com.tech.afa.archangel.library.utils;

import java.sql.SQLException;

public class Timer {

    private static final int MILLION = 1_000_000;

    public static <T> Pair<T, Long> measure(TimedOperation<T> operation) throws SQLException {
        long start = System.nanoTime();
        T result = operation.execute();
        long finish = System.nanoTime();
        long executeTime = (finish - start) / MILLION;
        return new Pair<>(result, executeTime);
    }
}
