package com.tech.afa.archangel.library.utils;

import java.sql.SQLException;

public class Timer {

    public static <T> Pair<T, Long> measure(TimedOperation<T> operation) throws SQLException {
        long start = System.nanoTime();
        T result = operation.execute();
        long finish = System.nanoTime();
        long executeTime = (finish - start) / 1_000_000;
        return new Pair<>(result, executeTime);
    }
}
