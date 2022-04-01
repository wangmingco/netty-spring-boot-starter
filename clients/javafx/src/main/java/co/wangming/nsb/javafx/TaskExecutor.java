package co.wangming.nsb.javafx;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskExecutor {

    private static ThreadPoolExecutor executor = null;

    private static AtomicBoolean isStart = new AtomicBoolean(false);

    public static void rebuild(int threads, int cycle) {
        executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.SECONDS, new ArrayBlockingQueue(cycle + 10));
    }

    public static void shutdownNow() {
        if (executor != null) {
            executor.shutdownNow();
        }

        stop();
    }

    public static boolean isStart() {
        return isStart.get();
    }

    public static void start() {
        isStart.set(true);
    }

    public static void stop() {
        isStart.set(false);
    }

    public static void submit(Runnable task) {
        executor.submit(task);
    }

    public static String threadExecutorInfo() {

        StringBuffer stringBuffer = new StringBuffer();
        long waitedCount = executor.getTaskCount() - executor.getCompletedTaskCount();
        long activeCount = executor.getActiveCount();
        long taskCount = executor.getTaskCount();

        stringBuffer.append("已完成任务:" + executor.getCompletedTaskCount());
        stringBuffer.append(". 未完成任务:"+ waitedCount);
        stringBuffer.append(". 任务总数:"+ taskCount);
        stringBuffer.append(". 运行中线程数:"+ activeCount);
        stringBuffer.append(". 线程总数:"+ executor.getPoolSize());
        stringBuffer.append(". 核心线程数:"+ executor.getCorePoolSize());
        stringBuffer.append(". 最大同时存在:"+ executor.getLargestPoolSize());
        stringBuffer.append(". 最大分配:"+ executor.getMaximumPoolSize());

        return stringBuffer.toString();
    }
}
