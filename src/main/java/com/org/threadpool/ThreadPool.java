package com.org.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPool {

    private static ThreadPool threadPool = new ThreadPool(100);
    private ExecutorService pool;
    private ScheduledExecutorService timerTaskPool;

    public static ThreadPool getInstance(){
        return threadPool;
    }

    private ThreadPool( int poolSize){
        pool = Executors.newFixedThreadPool(poolSize);
        timerTaskPool = Executors.newScheduledThreadPool(1);
    }

    public ExecutorService getPool(){
        return this.pool;
    }

    public ScheduledExecutorService getTimerTaskPool() {
        return this.timerTaskPool;
    }

    public void shutDown(){
        this.pool.shutdownNow();
        this.timerTaskPool.shutdownNow();
    }
}
