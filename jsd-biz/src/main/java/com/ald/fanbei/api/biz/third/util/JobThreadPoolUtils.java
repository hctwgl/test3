package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.service.JsdLegalContractPdfCreateService;
import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author weiqingeng
 * @describe 线程池
 * @date 2018/5/11 13:19
 **/
@Component("jobThreadPoolUtils")
public class JobThreadPoolUtils {

    private static final Logger logger = LoggerFactory.getLogger(JobThreadPoolUtils.class);
    //单例获取线程池
    private ExecutorService threadPool;
    @Resource
    private JsdLegalContractPdfCreateService jsdLegalContractPdfCreateService;

    //创建16条线程
    private JobThreadPoolUtils() {
        if (threadPool == null) {
            threadPool = Executors.newFixedThreadPool(16);
        }
    }
    /**
     * 赊销生成pdf
     * @param tradeNoXgxy
     */
    public void platformServiceSellProtocol(String tradeNoXgxy){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    jsdLegalContractPdfCreateService.platformServiceSellProtocol(tradeNoXgxy);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("JobThreadPoolUtils error =>{}",e);
                } catch (DocumentException e) {
                    e.printStackTrace();
                    logger.error("JobThreadPoolUtils error =>{}",e);
                }
            }
        });
    }


    /**
     * 砍头生成pdf
     * @param tradeNoXgxy
     */
    public void platformServiceBeheadProtocol(String tradeNoXgxy){
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    jsdLegalContractPdfCreateService.platformServiceBeheadProtocol(tradeNoXgxy);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("JobThreadPoolUtils error =>{}",e);
                } catch (DocumentException e) {
                    e.printStackTrace();
                    logger.error("JobThreadPoolUtils error =>{}",e);
                }
            }
        });
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
