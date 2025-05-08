package com.tech.afa.archangel.library.model.stats;

import lombok.AllArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@AllArgsConstructor
public class RequestStatistics {
    private final AtomicLong requestCount;
    private final AtomicLong minExecuteTimeMs;
    private final AtomicLong maxExecuteTimeMs;
    private final AtomicLong avgExecuteTimeMs;

    public RequestStatistics() {
        this.requestCount = new AtomicLong(0);
        this.minExecuteTimeMs = new AtomicLong(Long.MAX_VALUE);
        this.maxExecuteTimeMs = new AtomicLong(Long.MIN_VALUE);
        this.avgExecuteTimeMs = new AtomicLong(1);
    }

    public RequestStatistics reset(long executeTime) {
        this.minExecuteTimeMs.set(executeTime);
        this.maxExecuteTimeMs.set(executeTime);
        this.avgExecuteTimeMs.set(executeTime);
        return this;
    }

    public long getRequestCount() {
        return this.requestCount.get();
    }

    public long getMinExecuteTimeMs() {
        return this.minExecuteTimeMs.get();
    }

    public long getMaxExecuteTimeMs() {
        return this.maxExecuteTimeMs.get();
    }

    public long getAvgExecuteTimeMs() {
        return this.avgExecuteTimeMs.get();
    }

    public long incrementRequestCountAndGet() {
        return this.requestCount.incrementAndGet();
    }

    public void setMinExecuteTimeMs(long minExecuteTimeMs) {
        this.minExecuteTimeMs.set(minExecuteTimeMs);
    }

    public void setMaxExecuteTimeMs(long maxExecuteTimeMs) {
        this.maxExecuteTimeMs.set(maxExecuteTimeMs);
    }

    public void setAvgExecuteTimeMs(long avgExecuteTimeMs) {
        this.avgExecuteTimeMs.set(avgExecuteTimeMs);
    }

    @Override
    public String toString() {
        return String.format(
                """
                {
                        Request Count: %d
                        Min Execute Time: %d ms
                        Max Execute Time: %d ms
                        Avg Execute Time: %d ms
                    }""",
            requestCount.get(), minExecuteTimeMs.get(), maxExecuteTimeMs.get(), avgExecuteTimeMs.get()
        );
    }

    /*
class ResourceStats {
    AtomicLong rowsAffected;       // Количество затронутых строк
    AtomicLong resultSetSize;      // Размер возвращаемого результата
    AtomicLong fetchSize;          // Размер выборки
    double cpuUsage;               // Использование CPU
    long memoryUsage;              // Использование памяти (bytes)
}
class ErrorStats {
    int errorCount;                     // Количество ошибок
    String lastError;                   // Последнее сообщение об ошибке
    Map<String, Integer> errorTypes;    // Типы ошибок и их частота
}
class ExecutionPlanStats {
    String executionPlan;    // План выполнения запроса
    int indexUsageCount;     // Использование индексов
    boolean fullScanUsed;    // Был ли full scan
}
class TimingStats {
    long parsingTime;       // Время разбора запроса (мс)
    long compilationTime;   // Время компиляции (мс)
    long executionTime;     // Чистое время выполнения (мс)
    long networkTime;       // Время передачи данных (мс)
}
class ContextStats {
    String databaseType;    // Тип БД (MySQL, PostgreSQL и т.д.)
    String connectionId;    // Идентификатор соединения
    String userName;        // Имя пользователя БД
    Timestamp timestamp;    // Время выполнения
}
     */
}
