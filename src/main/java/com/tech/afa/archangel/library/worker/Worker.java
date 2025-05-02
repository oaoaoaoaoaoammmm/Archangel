package com.tech.afa.archangel.library.worker;

public interface Worker<T, S> {

    WorkerSignal work(T t, S s);

    boolean shouldWork(T t);
}
