package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import tool.code.generator.Create;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

public class test111 {
    @Resource
    AfUserService afUserService;
    @Resource
    AfUserWithholdService afUserWithholdService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserAccountService afUserAccountService;
    public void main(String[] args) {
        //String mobile = "13656648524,17612158083,18237147025,15067961798,13370127054";
        String mobile = "18314896619";
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
                //生成订单
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByStatus();
                if(afBorrowCashDo!=null){
                    continue;
                }
                afBorrowCashDo.setAmount(BigDecimal.valueOf(500l));
                afBorrowCashDo.setStatus("TRANSED");
                afBorrowCashDo.setUserId(userId);
                afBorrowCashDo.setType("SEVEN");
                afBorrowCashDo.setBorrowNo("jk2017test");
                afBorrowCashDo.setGmtPlanRepayment(new Date());
                afBorrowCashDo.setArrivalAmount(BigDecimal.valueOf(500l));
                afBorrowCashDo.setGmtCreate(new Date());
                afBorrowCashDo.setGmtModified(new Date());
                afBorrowCashDo.setCardName("0");
                afBorrowCashDo.setCardNumber("0");
                afBorrowCashDo.setAddress("");
                afBorrowCashDo.setCity("");
                afBorrowCashDo.setCounty("");
                afBorrowCashDo.setProvince("");
                afBorrowCashDo.setPoundage(BigDecimal.valueOf(0.00d));
                afBorrowCashDo.setRateAmount(BigDecimal.valueOf(0.00d));
                afBorrowCashDo.setLatitude(BigDecimal.valueOf(0d));
                afBorrowCashDo.setLongitude(BigDecimal.valueOf(0d));
                afBorrowCashDo.setPoundageRate(BigDecimal.valueOf(0d));
                afBorrowCashDo.setBaseBankRate(BigDecimal.valueOf(0d));

                afBorrowCashService.addBorrowCash(afBorrowCashDo);
                afUserAccountService.updateUserAccountByUserId(userId,400);
            }
        }
    }
}
