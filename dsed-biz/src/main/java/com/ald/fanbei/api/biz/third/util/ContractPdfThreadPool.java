package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.service.AfContractPdfCreateService;
import com.ald.fanbei.api.biz.service.AfLegalContractPdfCreateService;
import com.ald.fanbei.api.biz.service.AfLegalContractPdfCreateServiceV2;
import com.ald.fanbei.api.biz.service.AfWhiteLoanContractPdfCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @类描述:
 * @auther gsq 2017年11月16日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("contractPdfThreadPool")
public class ContractPdfThreadPool{

    protected static final Logger logger = LoggerFactory.getLogger(ContractPdfThreadPool.class);
    @Resource
    private AfContractPdfCreateService afContractPdfCreateService;
    @Resource
    private AfLegalContractPdfCreateService afLegalContractPdfCreateService;
    @Resource
    private AfLegalContractPdfCreateServiceV2 afLegalContractPdfCreateServiceV2;
    @Resource
    private AfWhiteLoanContractPdfCreateService afWhiteLoanContractPdfCreateService;
    private int nThreads = Runtime.getRuntime().availableProcessors() ;
    private int maxThreads = Runtime.getRuntime().availableProcessors() * 2;
    private ExecutorService service;// 线程池
    private int blockLength = 50; // 队列长度 。每个线程处理50条记录，保证ArrayBlockingQueue正常够用
    public ContractPdfThreadPool() {
        service = new ThreadPoolExecutor(nThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(blockLength), new ThreadPoolExecutor.DiscardPolicy());
        logger.info("contractPdfThreadPool nThreads num is " + nThreads);
    }
    public void protocolCashLoanPdf(Long borrowId,BigDecimal borrowAmount,Long userId ){
//        ProtocolCashLoanTask protocolCashLoanTask = new ProtocolCashLoanTask(borrowId,borrowAmount,userId);
//        service.execute(protocolCashLoanTask);
    }
    public void protocolRenewalPdf(long userId,Long borrowId,Long renewalId,int renewalDay ,BigDecimal renewalAmount){
//        ProtocolRenewalTask protocolRenewalTask = new ProtocolRenewalTask(userId,renewalId,renewalAmount,renewalDay,borrowId);
//        service.execute(protocolRenewalTask);
    }
    public void protocolInstalmentPdf(Long userId,Integer nper,BigDecimal amount,Long borrowId){
//        ProtocolInstalmentTask protocolInstalmentTask = new ProtocolInstalmentTask(userId,nper,amount,borrowId);
//        service.execute(protocolInstalmentTask);
    }
    public void PlatformServiceProtocolPdf(Long platformBorrowId,String platformType,BigDecimal platformPoundage,Long userId){
        PlatformServiceProtocolTask platformServiceProtocolTask = new PlatformServiceProtocolTask(platformBorrowId,platformType,platformPoundage,userId);
        service.execute(platformServiceProtocolTask);
    }
    public void whiteLoanPlatformServiceProtocol(Long id,Long userId){
        whiteLoanPlatformServiceProtocolTask whiteLoanPlatformServiceProtocolTask = new whiteLoanPlatformServiceProtocolTask(id,userId);
        service.execute(whiteLoanPlatformServiceProtocolTask);
    }

    public void createGoodsInstalmentProtocolPdf(Long borrowId,String type,Long userId){
        GoodsInstalmentProtocolTask goodsInstalmentProtocolTask = new GoodsInstalmentProtocolTask(borrowId,type,userId);
        service.execute(goodsInstalmentProtocolTask);
    }

    public void LeaseProtocolPdf(Map<String,Object> data,Long userId ,Long orderId){
        LeaseProtocolPdf leaseProtocolPdf = new LeaseProtocolPdf(data, userId,orderId);
        service.execute(leaseProtocolPdf);
    }

    class ProtocolCashLoanTask implements Runnable {
        private Long borrowId;
        private BigDecimal borrowAmount;
        private Long userId;
        public ProtocolCashLoanTask(Long bId,BigDecimal bAmount,long uId ) {
            borrowId = bId;
            borrowAmount = bAmount;
            userId = uId;
        }
        @Override
        public void run() {
            afContractPdfCreateService.protocolCashLoan(borrowId, borrowAmount, userId);
        }
    }

    class ProtocolInstalmentTask implements Runnable {
        private Long borrowId;
        private BigDecimal amount;
        private Long userId;
        private Integer nper;
        public ProtocolInstalmentTask(Long uId,Integer staging,BigDecimal bAmount,Long bId) {
            borrowId = bId;
            amount = bAmount;
            userId = uId;
            nper = staging;
        }
        @Override
        public void run() {
            afContractPdfCreateService.protocolInstalment(userId, nper, amount, borrowId);
        }
    }

    class PlatformServiceProtocolTask implements Runnable {
        private Long borrowId;
        private String type;
        private BigDecimal poundage;
        private Long userId;
        public PlatformServiceProtocolTask(Long platformBorrowId,String platformType,BigDecimal platformPoundage,Long uId) {
            borrowId = platformBorrowId;
            type = platformType;
            poundage = platformPoundage;
            userId = uId;
        }
        @Override
        public void run() {
            afLegalContractPdfCreateServiceV2.platformServiceProtocol(borrowId, type, poundage, userId);
        }
    }

    class GoodsInstalmentProtocolTask implements Runnable {
        private Long borrowId;
        private String type;
        private Long userId;
        public GoodsInstalmentProtocolTask(Long platformBorrowId,String borrowType,Long uId) {
            borrowId = platformBorrowId;
            type = borrowType;
            userId = uId;
        }
        @Override
        public void run() {
            afLegalContractPdfCreateServiceV2.goodsInstalmentProtocol(borrowId, type, userId);
        }
    }

    class whiteLoanPlatformServiceProtocolTask implements Runnable {
        private Long loanId;
        private Long userId;
        public whiteLoanPlatformServiceProtocolTask(Long id,Long uId) {
            loanId = id;
            userId = uId;
        }
        @Override
        public void run() {
            afWhiteLoanContractPdfCreateService.whiteLoanPlatformServiceProtocol(loanId,userId);
        }
    }

    class ProtocolRenewalTask implements Runnable {
        private Long renewalId;
        private BigDecimal renewalAmount;
        private Long userId;
        private Integer renewalDay;
        private Long borrowId;
        public ProtocolRenewalTask(Long uId,Long rId,BigDecimal rAmount,Integer rDay,Long bId) {
            renewalId = rId;
            renewalAmount = rAmount;
            userId = uId;
            renewalDay = rDay;
            borrowId = bId;
        }
        @Override
        public void run() {
            afContractPdfCreateService.protocolRenewal(userId, borrowId, renewalId, renewalDay, renewalAmount);
        }
    }

    class LeaseProtocolPdf implements Runnable {
        private Map<String,Object> data;
        private Long userId;
        private Long orderId;
        public LeaseProtocolPdf(Map<String,Object> idata,Long uId,Long oId) {
            data = idata;
            userId=uId;
            orderId=oId;
        }
        @Override
        public void run() {
            try {
                afLegalContractPdfCreateServiceV2.leaseProtocolPdf(data, userId,orderId);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
