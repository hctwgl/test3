package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.service.AfContractPdfCreateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
public class ContractPdfThreadPool implements InitializingBean {

    protected static final Logger logger = LoggerFactory.getLogger(ContractPdfThreadPool.class);
    @Resource
    private AfContractPdfCreateService afContractPdfCreateService;
    private int nThreads = Runtime.getRuntime().availableProcessors() * 2;
    private int maxThreads = Runtime.getRuntime().availableProcessors() * 3;
    private ExecutorService service;// 线程池
    private int blockLength = 50; // 队列长度 。每个线程处理50条记录，保证ArrayBlockingQueue正常够用
    public ContractPdfThreadPool() {
        service = new ThreadPoolExecutor(nThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(blockLength), new ThreadPoolExecutor.DiscardPolicy());
        logger.info("contractPdfThreadPool nThreads num is " + nThreads);
    }
    public void protocolCashLoanPdf(Long borrowId,BigDecimal borrowAmount,Long userId ){
        ProtocolCashLoanTask protocolCashLoanTask = new ProtocolCashLoanTask(borrowId,borrowAmount,userId);
        service.execute(protocolCashLoanTask);
    }
    public void protocolRenewalPdf(long userId,Long borrowId,Long renewalId,int renewalDay ,BigDecimal renewalAmount){
        ProtocolRenewalTask protocolRenewalTask = new ProtocolRenewalTask(userId,renewalId,renewalAmount,renewalDay,borrowId);
        service.execute(protocolRenewalTask);
    }
    public void protocolInstalmentPdf(Long userId,Integer nper,BigDecimal amount,Long borrowId){
        ProtocolInstalmentTask protocolInstalmentTask = new ProtocolInstalmentTask(userId,nper,amount,borrowId);
        service.execute(protocolInstalmentTask);
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
    /*class ContractTask implements Runnable {
        private Map<String, String> map;

        public ContractTask(Map<String, String> type) {
            map = type;
        }

        @Override
        public void run() {
            contractCreate(map);
        }
    }

    private void contractCreate(Map<String, String> map) {
        String contractType = map.get("contractType");
        Long borrowId = NumberUtil.objToLongDefault(map.get("borrowId"), 0l);
        Long userId = NumberUtil.objToLongDefault(map.get("userId"), 0l);
        try {
            if ("1".equals(contractType)) {//借款协议
                BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(map.get("borrowAmount"), BigDecimal.ZERO);
                afContractPdfCreateService.protocolCashLoan(borrowId, borrowAmount, userId);
            } else if ("2".equals(contractType)) {//分期服务协议
                Integer nper = NumberUtil.objToIntDefault(map.get("nper"), 0);
                BigDecimal amount = NumberUtil.objToBigDecimalDefault(map.get("amount"), BigDecimal.ZERO);
                afContractPdfCreateService.protocolInstalment(userId, nper, amount, borrowId);
            } else if ("3".equals(contractType)) {//续借协议
                Long renewalId = NumberUtil.objToLongDefault(map.get("renewalId"), 0l);
                Integer renewalDay = NumberUtil.objToIntDefault(map.get("renewalDay"), 0);
                BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(map.get("amount"), BigDecimal.ZERO);
                afContractPdfCreateService.protocolRenewal(userId, borrowId, renewalId, renewalDay, renewalAmount);
            }
        } catch (Exception e) {
            logger.error("contractPdfThreadPool nThreads error => {}", e.getMessage());
        }
    }*/

    @Override
    public void afterPropertiesSet() throws Exception {
    }

}
