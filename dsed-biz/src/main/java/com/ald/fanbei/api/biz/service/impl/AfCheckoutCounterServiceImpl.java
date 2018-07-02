package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.RiskCreditBo;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfCheckoutCounterDao;
import com.ald.fanbei.api.dal.domain.AfCheckoutCounterDo;
import com.ald.fanbei.api.biz.service.AfCheckoutCounterService;


/**
 * 收银台相关配置表ServiceImpl
 *
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-16 09:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afCheckoutCounterService")
public class AfCheckoutCounterServiceImpl extends ParentServiceImpl<AfCheckoutCounterDo, Long> implements AfCheckoutCounterService {

    private static final Logger logger = LoggerFactory.getLogger(AfCheckoutCounterServiceImpl.class);

    @Resource
    private AfCheckoutCounterDao afCheckoutCounterDao;
    @Resource
    private RiskUtil riskUtil;
    @Override
    public BaseDao<AfCheckoutCounterDo, Long> getDao() {
        return afCheckoutCounterDao;
    }

    public  AfCheckoutCounterDo getByType(String type,String secType){
        return afCheckoutCounterDao.getByType(type,secType);
    }

    public RiskCreditBo getRiskCreditSummary(Long userId){
        RiskCreditBo riskCreditBo=new RiskCreditBo();
        riskCreditBo.setLoanPayOffCount(afCheckoutCounterDao.countFinishBorrowCash(userId));
        riskCreditBo.setLoanOverdueDay(afCheckoutCounterDao.sumOverdueDayBorrowCash(userId));
        riskCreditBo.setConsumeBorrowAmount(afCheckoutCounterDao.sumBorrowAmount(userId));
        riskCreditBo.setConsumePayOffCount(afCheckoutCounterDao.countBorrow(userId));
        riskCreditBo.setConsumeOverdueDay(afCheckoutCounterDao.sumOverdueDayBorrow(userId));
        return riskCreditBo;
    }

    @Override
    public boolean getRiskCreditStatus(RiskCreditBo riskCredit) {

//         if(){
//
//         }
//        return riskUtil.creditPayment(riskCredit);
        return false;
    }
}