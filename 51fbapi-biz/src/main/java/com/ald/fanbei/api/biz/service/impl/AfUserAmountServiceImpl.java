package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.enums.AfUserAmountDetailType;
import com.ald.fanbei.api.common.enums.AfUserAmountProcessStatus;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.dal.dao.AfUserAmountDao;
import com.ald.fanbei.api.dal.dao.AfUserAmountDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserAmountLogDao;
import com.ald.fanbei.api.dal.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author honghzengpei 2017/11/22 14:29
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAmountService")
public class AfUserAmountServiceImpl implements AfUserAmountService {

    @Resource
    AfUserAmountDao afUserAmountDao;
    @Resource
    AfUserAmountLogDao afUserAmountLogDao;
    @Resource
    AfUserAmountDetailDao afUserAmountDetailDao;
    @Resource
    AfBorrowBillService afBorrowBillService;

    @Override
    public void addUseAmountDetail(AfRepaymentDo afRepaymentDo) {
        BigDecimal total = afRepaymentDo.getRepaymentAmount();  //总金额
        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(afRepaymentDo.getBillIds().split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });
        List<AfBorrowBillDo> afBorrowBillDoList = afBorrowBillService.getBorrowBillByIds(billIdList);
        BigDecimal principleAmount = BigDecimal.ZERO;        //本金
        BigDecimal poundageAmount = BigDecimal.ZERO;        //手续费
        BigDecimal overdueInterestAmount = BigDecimal.ZERO;       //逾期利息

        BigDecimal yohuijuang = BigDecimal.ZERO;   //优惠卷
        BigDecimal yuer = BigDecimal.ZERO;
        BigDecimal zifu = BigDecimal.ZERO;

        zifu = afRepaymentDo.getActualAmount();
        yohuijuang = afRepaymentDo.getCouponAmount();
        yuer = afRepaymentDo.getRebateAmount().add(afRepaymentDo.getJfbAmount());

        for(AfBorrowBillDo afBorrowBillDo : afBorrowBillDoList){
            principleAmount =principleAmount.add(afBorrowBillDo.getPrincipleAmount());
            overdueInterestAmount =overdueInterestAmount.add(afBorrowBillDo.getOverdueInterestAmount().add(afBorrowBillDo.getOverduePoundageAmount()));
            poundageAmount = poundageAmount.add(afBorrowBillDo.getPoundageAmount().add(afBorrowBillDo.getInterestAmount()));
        }

        AfUserAmountDo afUserAmountDo = new AfUserAmountDo();
        afUserAmountDo.setAmount(afRepaymentDo.getRepaymentAmount());
        afUserAmountDo.setBizOrderNo(afRepaymentDo.getRepayNo());
        afUserAmountDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountDo.setUserId(afRepaymentDo.getUserId());
        afUserAmountDo.setStatus(AfUserAmountProcessStatus.NEW.getCode());
        afUserAmountDo.setRemark("");
        afUserAmountDao.addUserAmount(afUserAmountDo);
        addUserAmountLog(afRepaymentDo,AfUserAmountProcessStatus.NEW);

        if(BigDecimal.ZERO.compareTo(principleAmount) !=0){
            afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),principleAmount,1, AfUserAmountDetailType.BENJIN));
        }
        if(BigDecimal.ZERO.compareTo(poundageAmount) !=0){
            afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),poundageAmount,1,AfUserAmountDetailType.FENQISHOUXUFEI));
        }
        if(BigDecimal.ZERO.compareTo(overdueInterestAmount) !=0){
            afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),overdueInterestAmount,1,AfUserAmountDetailType.YUQILIXI));
        }

        if(BigDecimal.ZERO.compareTo(yohuijuang) !=0){
            afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( yohuijuang),1,AfUserAmountDetailType.YOUHUIJUANGDIKOU));
        }
        if(BigDecimal.ZERO.compareTo(yuer) !=0){
            afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( yuer),1,AfUserAmountDetailType.ZHANGHUYUERDIKOU));
        }
        //if(BigDecimal.ZERO.compareTo(zifu) !=0){
        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( zifu),1,AfUserAmountDetailType.SHIJIZHIFU));
        //}
    }

    @Override
    public void addUserAmountLog(AfRepaymentDo afRepaymentDo, AfUserAmountProcessStatus afUserAmountProcessStatus) {
        AfUserAmountLogDo afUserAmountLogDo = new AfUserAmountLogDo();
        afUserAmountLogDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountLogDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountLogDo.setStatus(afUserAmountProcessStatus.getCode());
        afUserAmountLogDao.addUserAmountLog(afUserAmountLogDo);
    }

    @Override
    public void updateUserAmount(AfUserAmountProcessStatus afUserAmountProcessStatus, AfRepaymentDo afRepaymentDo) {
        addUserAmountLog(afRepaymentDo,afUserAmountProcessStatus);
        AfUserAmountDo afUserAmountDo = new AfUserAmountDo();
        afUserAmountDo.setStatus(afUserAmountProcessStatus.getCode());
        afUserAmountDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountDao.updateUserAmountStatus(afUserAmountDo);
    }

    private AfUserAmountDetailDo buildAmountDetail(long userAmountId, BigDecimal amount, int count, AfUserAmountDetailType type){
        AfUserAmountDetailDo afUserAmountDetailDo = new AfUserAmountDetailDo();
        afUserAmountDetailDo.setUserAmountId(userAmountId);
        afUserAmountDetailDo.setAmount(amount);
        afUserAmountDetailDo.setCount(count);
        afUserAmountDetailDo.setType(type.getCode());
        afUserAmountDetailDo.setTitle(type.getName());
        afUserAmountDetailDo.setRemark("");
        return afUserAmountDetailDo;
    }
}
