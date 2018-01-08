package com.ald.fanbei.web.test;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tool.code.generator.Create;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class test111 {
    @Resource
    AfUserService afUserService;
    @Resource
    AfUserWithholdService afUserWithholdService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfBorrowDao afBorrowDao;
    @RequestMapping(value = "/creatBorrow", method = RequestMethod.POST)
    @ResponseBody
    public String CreatBorrow() {
        //String mobile = "13656648524,17612158083,18237147025,15067961798,13370127054";
        String mobile = "18314896619,17612158083";
        String[] mobileStr = mobile.split(",");
        for(int i=0;i<mobileStr.length;i++){
            String mobile1 = mobileStr[i];
            AfUserDo afUserDo = afUserService.getUserByUserName(mobile1);
            if(afUserDo!=null){
                Long userId = afUserDo.getRid();
                AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userId);
                if(afUserWithholdDo==null){
                    afUserWithholdDo.setUserId(userId);
                    afUserWithholdDo.setIsWithhold(1);
                    afUserWithholdDo.setUsebalance(1);
                    afUserWithholdDo.setCardId1(3111270l);
                    afUserWithholdDo.setLastopenTime(new Date());
                    afUserWithholdService.saveRecord(afUserWithholdDo);
                }
                //生成借款订单
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByStatus(userId);
                if(afBorrowCashDo!=null){
                    continue;
                }
                afBorrowCashDo = new AfBorrowCashDo();
                BigDecimal amount = NumberUtil.objToBigDecimalDefault(
                        ObjectUtils.toString("500"), BigDecimal.ZERO);
                BigDecimal arrivalAmount = NumberUtil.objToBigDecimalDefault(
                        ObjectUtils.toString("500"), BigDecimal.ZERO);

                afBorrowCashDo.setAmount(amount);
                afBorrowCashDo.setStatus("TRANSED");
                afBorrowCashDo.setUserId(userId);
                afBorrowCashDo.setType("SEVEN");
                afBorrowCashDo.setBorrowNo("jk2017test");
                afBorrowCashDo.setGmtPlanRepayment(new Date());
                afBorrowCashDo.setArrivalAmount(arrivalAmount);
                afBorrowCashDo.setGmtCreate(new Date());
                afBorrowCashDo.setGmtModified(new Date());
                afBorrowCashDo.setCardName("0");
                afBorrowCashDo.setCardNumber("0");
                afBorrowCashDo.setAddress("");
                afBorrowCashDo.setCity("");
                afBorrowCashDo.setCounty("");
                afBorrowCashDo.setProvince("");
                afBorrowCashDo.setPoundage(BigDecimal.ZERO);
                afBorrowCashDo.setRateAmount(BigDecimal.ZERO);
                afBorrowCashDo.setLatitude(BigDecimal.ZERO);
                afBorrowCashDo.setLongitude(BigDecimal.ZERO);
                afBorrowCashDo.setPoundageRate(BigDecimal.ZERO);
                afBorrowCashDo.setBaseBankRate(BigDecimal.ZERO);

                afBorrowCashService.addBorrowCash(afBorrowCashDo);
                afBorrowCashService.updateAfBorrowCashPlanTime(userId);
                afUserAccountService.updateUserAccountByUserId(userId,400);

                //生成分期账单
                /*AfBorrowBillDo bill = new AfBorrowBillDo();
                bill.setUserId(userId);
                bill.setBorrowId(borrow.getRid());
                bill.setBorrowNo(borrow.getBorrowNo());
                bill.setName(borrow.getName());
                bill.setGmtBorrow(borrow.getGmtCreate());
                Map<String, Integer> timeMap = getCurrentYearAndMonth(now);
                bill.setBillYear(timeMap.get(Constants.DEFAULT_YEAR));
                bill.setBillMonth(timeMap.get(Constants.DEFAULT_MONTH));
                bill.setNper(borrow.getNper());
                bill.setBillNper(i);
                if (i <= freeNper) {
                    bill.setInterestAmount(BigDecimal.ZERO);
                    bill.setIsFreeInterest(YesNoStatus.YES.getCode());
                    bill.setPoundageAmount(BigDecimal.ZERO);
                } else {
                    bill.setInterestAmount(interestAmount);
                    bill.setIsFreeInterest(YesNoStatus.NO.getCode());
                    bill.setPoundageAmount(poundageAmount);
                }
                if (i == 1) {
                    bill.setPrincipleAmount(firstPrincipleAmount);
                } else {
                    bill.setPrincipleAmount(principleAmount);
                }
                bill.setBillAmount(BigDecimalUtil.add(bill.getInterestAmount(), bill.getPoundageAmount(), bill.getPrincipleAmount()));
                bill.setStatus(BorrowBillStatus.NO.getCode());
                bill.setType(BorrowType.CONSUME.getCode());
                afBorrowDao.addBorrowBillInfo(bill);*/
            }
        }
        return "";
    }
}
