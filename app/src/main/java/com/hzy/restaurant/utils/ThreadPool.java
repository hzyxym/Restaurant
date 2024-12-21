package com.hzy.restaurant.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * ThreadPool线程池管理类
 * <p>
 * 关于线程池ThreadPoolExecutor的说明：
 * <p>
 * 1.ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)
 * corePoolSize: 核心线程数，能够同时执行的任务数量
 * maximumPoolSize：除去缓冲队列中等待的任务，最大能容纳的任务数(其实是包括了核心线程池数量)
 * keepAliveTime：超出workQueue的等待任务的存活时间，就是指maximumPoolSize里面的等待任务的存活时间
 * unit：时间单位
 * workQueue:阻塞等待线程的队列，一般使用new LinkedBlockingQueue<Runnable>()这个，如果不指定容量，
 * 会一直往里边添加，没有限制,workQueue永远不会满；
 * threadFactory：创建线程的工厂，使用系统默认的类
 * handler：当任务数超过maximumPoolSize时，对任务的处理策略，默认策略是拒绝添加
 * <p>
 * 执行流程：当线程数小于corePoolSize时，每添加一个任务，则立即开启线程执行
 * 当corePoolSize满的时候，后面添加的任务将放入缓冲队列workQueue等待；
 * 当workQueue也满的时候，看是否超过maximumPoolSize线程数，如果超过，默认拒绝执行
 * 举例说明：
 * 假如：corePoolSize=2，maximumPoolSize=3，workQueue容量为8;
 * 最开始，执行的任务A，B，此时corePoolSize已用完，再次执行任务C，则
 * C将被放入缓冲队列workQueue中等待着，如果后来又添加了7个任务，此时workQueue已满，
 * 则后面再来的任务将会和maximumPoolSize比较，由于maximumPoolSize为3，所以只能容纳1个了，
 * 因为有2个在corePoolSize中运行了，所以后面来的任务默认都会被拒绝。
 */

public class ThreadPool {
    private int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private int maxNumPoolSize = corePoolSize + 1;
    private long keepAliveTime = 1;
    private TimeUnit unit = TimeUnit.MINUTES;
    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadPool() {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maxNumPoolSize,
                keepAliveTime,
                unit,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private static volatile ThreadPool pool;

    public static ThreadPool getInstance() {
        if (pool == null) {
            synchronized (ThreadPool.class) {
                pool = new ThreadPool();
            }
        }
        return pool;
    }

    public void execute(Runnable command) {
        threadPoolExecutor.execute(command);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolExecutor.submit(task);
    }

    public Future<?> submit(Runnable task) {
        return threadPoolExecutor.submit(task);
    }

    public void clear() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdownNow();
            pool = null;
        }
    }
}