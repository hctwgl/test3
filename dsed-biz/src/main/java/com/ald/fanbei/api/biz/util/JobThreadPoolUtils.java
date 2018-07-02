package com.ald.fanbei.api.biz.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author weiqingeng
 * @describe 线程池
 * @date 2018/5/11 13:19
 **/
@Component("jobThreadPoolUtils")
public class JobThreadPoolUtils {
    //单例获取线程池
    private ExecutorService threadPool;

    //创建16条线程
    private JobThreadPoolUtils() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(16);
        }
    }


    /**
     * 异步处理业务逻辑
     */
    public void asynProcessBusiness(Runnable process) {
        threadPool.execute(process);
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }
}
