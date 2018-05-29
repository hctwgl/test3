package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.AfSupplierOrderSettlementService;
import com.ald.fanbei.api.common.enums.SupplierOrderSettlementStatus;
import com.ald.fanbei.api.common.enums.SupplierSettlementOrderPayStatus;
import com.ald.fanbei.api.dal.dao.AfSupplierOrderSettlementDao;
import com.ald.fanbei.api.dal.domain.AfSupplierOrderSettlementDo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
/**
 * @类描述：自营商城 订单结算
 * @author weiqingeng 2017年12月12日下午10:22:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSupplierOrderSettlementService")
public class AfSupplierOrderSettlementServiceImpl implements AfSupplierOrderSettlementService {
    private static final Logger logger = LoggerFactory.getLogger(AfSupplierOrderSettlementServiceImpl.class);
    @Resource
    AfSupplierOrderSettlementDao afSupplierOrderSettlementDao;
    @Resource
    TransactionTemplate transactionTemplate;


    /**
     * 处理 支付中心 回调
     * @param afSupDo
     * @param tradeState
     */
    @Override
    public void dealPayCallback(final AfSupplierOrderSettlementDo afSupDo, final String tradeState) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus status) {

                logger.error("dealPayCallback, tradeState=" + tradeState);
                if ("00".equals(tradeState)) {// 打款 成功，更新结算单状态 tradeState: 00 成功 01部分成功 02已签约 10 失败 20 处理中 30未知
                    afSupDo.setStatus(SupplierOrderSettlementStatus.SETTLEMENT_SUCCESS.getStatus());
                    afSupplierOrderSettlementDao.updateBatchOrderSettlementStatus(afSupDo);//修改结算订单为已结算
                    afSupDo.setStatus(SupplierSettlementOrderPayStatus.PAY_SUCCESS.getStatus());
                    afSupplierOrderSettlementDao.updateSettlementOrderPayStatus(afSupDo);

                    //处理轧差的结算单
                    String offsetNo = afSupplierOrderSettlementDao.selectSettlementNoById(afSupDo.getRid());//查找当前结算单是否包含轧差的结算单
                    if(StringUtils.isNotBlank(offsetNo)){
                        String[] offsetNoArray = offsetNo.split(",");
                        for(int i = 0; i < offsetNoArray.length; i++){
                            if(StringUtils.isNotBlank(offsetNoArray[i])){
                                logger.error("dealPayCallback, offsetNo=" + offsetNoArray[i]);
                                //更新结算单的状态
                                afSupDo.setSettlementNo(offsetNoArray[i]);
                                afSupDo.setStatus(SupplierSettlementOrderPayStatus.PAY_SUCCESS.getStatus());
                                afSupplierOrderSettlementDao.updateSettlementOrderPayStatusBySettleNo(afSupDo);

                                //更新结算订单的状态
                                Long rid = afSupplierOrderSettlementDao.selectSettlementNoBySettleNo(offsetNoArray[i]);
                                if(null != rid){
                                    afSupDo.setRid(rid);
                                    afSupDo.setStatus(SupplierOrderSettlementStatus.SETTLEMENT_SUCCESS.getStatus());
                                    afSupplierOrderSettlementDao.updateBatchOrderSettlementStatus(afSupDo);//修改结算订单为已结算
                                }
                            }
                        }
                    }
                }else{//记录错误日志
                    logger.error("dealPayCallback, tradeState=" + tradeState);
                    afSupDo.setStatus(SupplierOrderSettlementStatus.SETTLEMENT_FAILED.getStatus());
                    afSupplierOrderSettlementDao.updateBatchOrderSettlementStatus(afSupDo);//修改结算订单为结算失败
                    afSupDo.setStatus(SupplierSettlementOrderPayStatus.PAY_FAILED.getStatus());
                    afSupplierOrderSettlementDao.updateSettlementOrderPayStatus(afSupDo);
                }
                return null;
            }
        });

    }

}
