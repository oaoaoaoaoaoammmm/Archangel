package com.tech.afa.archangel.library.worker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkerStarter {

    public static <T, S, R extends Worker<T, S>> void startWork(T obj, S obj2, List<R> workers) {
        for (Worker<T, S> worker : workers) {
            if (worker.shouldWork(obj)) {
                WorkerSignal signalType = worker.work(obj, obj2);
                if (signalType == WorkerSignal.STOP) {
                    return;
                }
                if (signalType == WorkerSignal.FINALLY) {
                    Worker<T, S> finalWorker = workers.getLast();
                    if (finalWorker.shouldWork(obj)) {
                        finalWorker.work(obj, obj2);
                        return;
                    }
                }
            }
        }
    }
}
