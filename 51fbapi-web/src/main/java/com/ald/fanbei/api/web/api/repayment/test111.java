package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.util.NumberUtil;
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

            }
        }
        return "";
    }
}
