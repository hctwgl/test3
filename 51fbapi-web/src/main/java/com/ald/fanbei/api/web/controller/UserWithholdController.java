package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @类描述:代扣任务请求
 *
 * @auther hqj 2017年11月1日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/userWithhold")
public class UserWithholdController extends BaseController {
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfWithholdLogService afWithholdLogService;
    @Resource
    AfUserWithholdService afUserWithholdService;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    /**
     * 借款代扣
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/borrowCash", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject CreatRepaymenCash(HttpServletRequest request, HttpServletResponse response) throws Exception{

        Long userId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("userId")),null);
        //最低代扣金额
        BigDecimal lowCashPrice = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(request.getParameter("lowCashPrice")),BigDecimal.ZERO);
        BigDecimal amountRate = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(request.getParameter("amountRate")),BigDecimal.ZERO);
        int isAmount = NumberUtil.objToIntDefault(ObjectUtils.toString(request.getParameter("isAmount")),1);
        String borrowStatus = ObjectUtils.toString(request.getParameter("borrowStatus"), null);
		String bankPayType = ObjectUtils.toString(request.getParameter("payType"), null);
        BigDecimal repaymentAmount = BigDecimal.ZERO;

        Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("borrowId")), 0l);

        if (userId == null || borrowId == 0) {
            logger.info("withhold for borrowcash fail for params is null,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","params is null");
            return returnjson;
        }
        logger.info("withhold for borrowbill userId:"+userId+",borrowId:"+borrowId+",lowCashPrice:"+lowCashPrice);

        AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
        if (rbCashDo != null && StringUtils.equals(rbCashDo.getStatus(),
                AfBorrowCashRepmentStatus.PROCESS.getCode())) {
            logger.info("withhold for borrowcash fail for BORROW_CASH_REPAY_PROCESS_ERROR,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
            return returnjson;
        }
        // 判断是否存在续期处理中的记录,防止续期和还款交叉,导致最后记录更新失败
        AfRenewalDetailDo lastAfRenewalDetailDo = afRenewalDetailService
                .getRenewalDetailHoursByBorrowId(borrowId);
        if (lastAfRenewalDetailDo != null) {
            logger.info("withhold for borrowcash fail for HAVE_A_PROCESS_RENEWAL_DETAIL,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
            return returnjson;
        }
        //等待第三方调试
        /*if (!yiBaoUtility.checkCanNext(userId, 0)) {
            logger.info("withhold for borrowcash fail for HAVE_A_REPAYMENT_PROCESSING_ERROR,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
            return returnjson;
        }*/
        AfUserWithholdDo afUserWithholdDo= afUserWithholdService.getByUserId(userId);
        if(afUserWithholdDo==null){
            //用户又关闭了代扣，但是目前逻辑是在代扣时间段内用户不能操作代扣相关功能
            logger.info("withhold for borrowcash fail for afUserWithholdDo is null,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","afUserWithholdDo is null");
            return returnjson;
        }
        AfUserAccountDo userDto = afUserAccountService
                .getUserAccountByUserId(userId);
        if (userDto == null) {
            logger.info("withhold for borrowcash fail for USER_ACCOUNT_NOT_EXIST_ERROR,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return returnjson;
        }

        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
        try{
            if (afBorrowCashDo != null) {
                BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(),
                        afBorrowCashDo.getSumOverdue(),
                        afBorrowCashDo.getOverdueAmount(),
                        afBorrowCashDo.getRateAmount(),
                        afBorrowCashDo.getSumRate());
                //判断是否搭售二期，加入手续费
                if(afBorrowLegalOrderService.isV2BorrowCash(borrowId)){
                    allAmount = BigDecimalUtil.add(allAmount,afBorrowCashDo.getPoundage(),afBorrowCashDo.getSumRenewalPoundage());
                }
                BigDecimal temAmount = BigDecimalUtil.subtract(allAmount,
                        afBorrowCashDo.getRepayAmount());
                repaymentAmount = temAmount;
                if(StringUtils.isNotBlank(borrowStatus)){
                    if("overdue".equals(borrowStatus)){
                        if(isAmount==1){
                            //amountRate = BigDecimalUtil.add(amountRate,BigDecimal.ONE);
                            repaymentAmount = BigDecimalUtil.subtract(repaymentAmount,afBorrowCashDo.getOverdueAmount());
                        }
                        logger.info("withhold for borrowcashOverdue,userId:"+userId + ",borrowId:"+borrowId);
                        if(repaymentAmount.compareTo(temAmount)>0){
                            repaymentAmount = temAmount;
                        /*logger.info("withhold for borrowcashOverdue fail for repaymentAmount>temAmount,userId:"+userId + ",borrowId:"+borrowId);
                        JSONObject returnjson = new JSONObject();
                        returnjson.put("success",false);
                        returnjson.put("msg","afBorrowCashDo repaymentAmount>temAmount");
                        return returnjson;*/
                        }
                    }
                }
                //如果订单已结清或者需还金额为0
                if(!"TRANSED".equals(afBorrowCashDo.getStatus())||repaymentAmount.compareTo(BigDecimal.ZERO)<=0){
                    //当前无账单
                    logger.info("withhold for borrowcash fail for afBorrowCashDo is not TRANSED or repaymentAmount is 0,userId:"+userId + ",borrowId:"+borrowId);
                    JSONObject returnjson = new JSONObject();
                    returnjson.put("success",false);
                    returnjson.put("msg","afBorrowCashDo is not TRANSED");
                    return returnjson;
                }
            }else{
                //当前无账单
                logger.info("withhold for borrowcash fail for afBorrowCashDo is null,userId:"+userId + ",borrowId:"+borrowId);
                JSONObject returnjson = new JSONObject();
                returnjson.put("success",false);
                returnjson.put("msg","afBorrowCashDo is null");
                return returnjson;
            }
        }catch (Exception e){
            logger.error("withhold for borrowcash error" + e);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","afBorrowCashDo error");
            return returnjson;
        }
        //将该笔订单锁住(除此之外还需要将原先用户还款的数据锁住)
        if(afBorrowCashService.updateBorrowCashLock(borrowId)==0){
            logger.info("withhold for borrowcash fail for lock,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","订单正在还款中");
            return returnjson;
        }

        BigDecimal actualAmount = repaymentAmount;
        BigDecimal userAmount = BigDecimal.ZERO;
        //是否开启余额支付
        int useBalance = afUserWithholdDo.getUsebalance();
        if(useBalance==1){
            //账户余额
            BigDecimal userAccount = userDto.getRebateAmount();
            //不再使用余额
            userAccount = BigDecimal.ZERO;
            // 余额处理
            if (userAccount.compareTo(BigDecimal.ZERO) > 0
                    && repaymentAmount.compareTo(userAccount) > 0) {
                actualAmount = BigDecimalUtil.subtract(repaymentAmount,
                        userDto.getRebateAmount());
                userAmount = userDto.getRebateAmount();
            } else if (userAccount.compareTo(BigDecimal.ZERO) > 0
                    && repaymentAmount.compareTo(userAccount) <= 0) {
                userAmount = repaymentAmount;
                actualAmount = BigDecimal.ZERO;
            }
        }

        Map<String, Object> map = null;
        Map<Object, Long> cardMap = new HashMap<>();
        JSONObject returnjson = new JSONObject();
        //返回的还款id
        Long refId = 0l;
        AfWithholdLogDo afWithholdLogDo = new AfWithholdLogDo();
        afWithholdLogDo.setAmount(repaymentAmount);
        afWithholdLogDo.setBorrowcashId(borrowId);
        afWithholdLogDo.setBorrowType(1);
        afWithholdLogDo.setGmtCreate(new Date());
        afWithholdLogDo.setUserId(userId);
        try{
            //判断是否需要银行卡还款,如果不需要，直接使用余额还款
            if(useBalance==1&&actualAmount.compareTo(BigDecimal.ZERO)==0){
                Long cardId = -2l;
                try{
                    map = afRepaymentBorrowCashService.createRepayment(BigDecimal.ZERO,
                            repaymentAmount, actualAmount, null, userAmount,
                            borrowId, cardId, userId, "sysJob", userDto,bankPayType,"");
                }catch (Exception e) {
                    logger.info("withholdCashJob error", e);
                }

                if(map!=null){
                    returnjson.put("refId", map.get("refId"));
                    returnjson.put("success",true);
                    //returnjson.put("type", map.get("type"));
                    refId = NumberUtil.objToLongDefault(ObjectUtils.toString(map.get("refId")),0l);
                    afWithholdLogDo.setStatus(1);
                }else{
                    returnjson.put("success",false);
                    afWithholdLogDo.setStatus(0);
                }
                afWithholdLogDo.setRefId(refId);
                afWithholdLogDo.setRemark(map==null?"":map.toString());
                //插入代扣日志
                try{
                    afWithholdLogService.saveRecord(afWithholdLogDo);
                }catch (Exception e){
                    logger.error("withhold for add logs error:" + e);
                }
                return returnjson;
            }else if(actualAmount.compareTo(lowCashPrice)<0){
                logger.info("withhold for borrowcash fail for actualAmount less than lowCashPrice,userId:"+userId + ",borrowId:"+borrowId);
                returnjson.put("success",false);
                returnjson.put("msg","发起扣款金额小于门槛金额");
                return returnjson;
            }else if(actualAmount.compareTo(lowCashPrice)>=0){
                Long cardId1 = afUserWithholdDo.getCardId1();
                Long cardId2 = afUserWithholdDo.getCardId2();
                Long cardId3 = afUserWithholdDo.getCardId3();
                Long cardId4 = afUserWithholdDo.getCardId4();
                Long cardId5 = afUserWithholdDo.getCardId5();
                cardMap.put(1,cardId1);
                cardMap.put(2,cardId2);
                cardMap.put(3,cardId3);
                cardMap.put(4,cardId4);
                cardMap.put(5,cardId5);
            }
            UpsCollectRespBo upsResult = null;
            for(int j=1;j<=cardMap.size();j++){
                Long cardId = cardMap.get(j);
                if(cardId!=null&&cardId!=0l){
                    AfUserBankcardDo card = afUserBankcardService.getUserBankcardByIdAndStatus(cardId);
                    if (null == card) {
                        logger.info("withhold for borrowcash fail for card is null,userId:"+userId + ",borrowId:"+borrowId);
                        continue;
                    }
                    try{
                        map = afRepaymentBorrowCashService.createRepayment(BigDecimal.ZERO,
                                repaymentAmount, actualAmount, null, userAmount,
                                borrowId, cardId, userId, "sysJob", userDto,bankPayType,"");

                    }catch (Exception e) {
                        logger.info("withholdCashJob error", e);
                    }
                    if(map!=null){
                        // 代收
                        if (map.get("resp") != null
                                && map.get("resp") instanceof UpsCollectRespBo) {
                            upsResult = (UpsCollectRespBo) map.get("resp");
                        }
                        returnjson.put("refId", map.get("refId"));
                        refId = NumberUtil.objToLongDefault(ObjectUtils.toString(map.get("refId")),0l);
                    }
                    afWithholdLogDo.setCardNumber(card.getCardNumber());
                    afWithholdLogDo.setCardId(cardId);
                    afWithholdLogDo.setRefId(refId);
                    if (upsResult != null && upsResult.isSuccess()) {
                        returnjson.put("success",true);
                        returnjson.put("outTradeNo", upsResult.getOrderNo());
                        returnjson.put("tradeNo", upsResult.getTradeNo());
                        returnjson.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
                        afWithholdLogDo.setStatus(1);
                        afWithholdLogDo.setRemark(map.toString());
                        //插入代扣日志
                        try{
                            afWithholdLogService.saveRecord(afWithholdLogDo);
                        }catch (Exception e){
                            logger.error("withhold for add logs error:" + e);
                        }
                        break;
                    }else{
                        afWithholdLogDo.setStatus(0);
                        returnjson.put("success",true);
                        afWithholdLogDo.setRemark(map==null?"":map.toString());
                        //插入代扣日志
                        try{
                            afWithholdLogService.saveRecord(afWithholdLogDo);
                        }catch (Exception e){
                            logger.error("withhold for add logs error:" + e);
                        }
                    }

                }
            }
        }catch(Exception e){
            logger.info("withholdCashJob error", e);
        }finally {
            //更新还款表的款款方式
            //afRepaymentBorrowCashService.updateRepaymentBorrowCashName(refId);
            //借款账单解锁
            afBorrowCashService.updateBorrowCashUnLock(borrowId);
        }
        return returnjson;
    }

    /**
     * 分期代扣
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/borrowBill", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject CreatRepaymenBill(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long userId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("userId")),null);
        //最低代扣金额
        BigDecimal lowBillPrice = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(request.getParameter("lowBillPrice")),BigDecimal.ZERO);
        String bankPayType = ObjectUtils.toString(request.getParameter("payType"), null);
        if (userId == null) {
            logger.info("withhold for borrowbill fail for params is null");
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","params is null");
            return returnjson;
        }
        logger.info("withhold for borrowbill userId:"+userId+",lowBillPrice:"+lowBillPrice);
        AfUserWithholdDo afUserWithholdDo= afUserWithholdService.getByUserId(userId);
        if(afUserWithholdDo==null){
            //用户又关闭了代扣，但是目前逻辑是在代扣时间段内用户不能操作代扣相关功能
            logger.info("withhold for borrowbill fail for afUserWithholdDo is null,userId:"+userId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","afUserWithholdDo is null");
            return returnjson;
        }
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (afUserAccountDo == null) {
            logger.info("withhold for borrowbill fail for USER_ACCOUNT_NOT_EXIST_ERROR,userId:"+userId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return returnjson;
        }
        String billIds = afBorrowBillService.getBillIdsByUserId(userId);
        if(StringUtil.isBlank(billIds)){
            logger.info("withhold for borrowbill fail for billsIds1 is null,userId:"+userId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_BILL_UPDATE_ERROR);
            return returnjson;
        }

        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });
        List<AfBorrowBillDo> borrowBillList = afBorrowBillService.getBorrowBillByIds(billIdList);
        if (constainsRepayingBill(borrowBillList)) {
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_BILL_IS_REPAYING);
            return returnjson;
        }

        JSONObject returnjson = new JSONObject();
        Long refId = 0l;
        try{
            //遍历账单，加锁
            String[] billStr = billIds.split(",");
            billIds = "";
            for(int i=0;i<billStr.length;i++){
                String billId = billStr[i];
                if(afBorrowBillService.updateBorrowBillLockById(billId)>0){
                    if(billIds.equals("")){
                        billIds = billId;
                    }else{
                        billIds = billIds + "," + billId;
                    }
                }
            }
            if(StringUtil.isBlank(billIds)){
                logger.info("withhold for borrowbill fail for billIds is null,userId:"+userId+"billIds:"+billIds);
                returnjson.put("success",false);
                returnjson.put("msg","withhold for borrowbill fail for billIds is null");
                return returnjson;
            }

            AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(billIds);
            BigDecimal repaymentAmount = billDo.getBillAmount();
            BigDecimal actualAmount = repaymentAmount;
            BigDecimal userAmount = BigDecimal.ZERO;
            //AfUserWithholdDo afUserWithholdDo= afUserWithholdService.getByUserId(userId);
            //是否开启余额支付
            int useBalance = afUserWithholdDo.getUsebalance();
            if(useBalance==1){
                //账户余额
                BigDecimal userAccount = afUserAccountDo.getRebateAmount();
                //不再使用余额
                userAccount = BigDecimal.ZERO;
                // 余额处理
                if (userAccount.compareTo(BigDecimal.ZERO) > 0
                        && repaymentAmount.compareTo(userAccount) > 0) {
                    actualAmount = BigDecimalUtil.subtract(repaymentAmount,
                            afUserAccountDo.getRebateAmount());
                    userAmount = afUserAccountDo.getRebateAmount();
                } else if (userAccount.compareTo(BigDecimal.ZERO) > 0
                        && repaymentAmount.compareTo(userAccount) <= 0) {
                    userAmount = repaymentAmount;
                    actualAmount = BigDecimal.ZERO;
                }
            }

            Map<String, Object> map = null;
            Map<Object, Long> cardMap = new HashMap<>();

            //插入代扣日志表
            AfWithholdLogDo afWithholdLogDo = new AfWithholdLogDo();
            afWithholdLogDo.setAmount(repaymentAmount);
            afWithholdLogDo.setBorrowbillId(billIds);
            afWithholdLogDo.setBorrowType(2);
            afWithholdLogDo.setGmtCreate(new Date());
            afWithholdLogDo.setUserId(userId);
            //判断是否额度支付
            if(useBalance==1&&actualAmount.compareTo(BigDecimal.ZERO)==0){
                Long cardId = -2l;
                try{
                    map = afRepaymentService.createRepaymentByBankOrRebate(BigDecimal.ZERO,repaymentAmount, actualAmount,null, userAmount, billIds,
                            cardId,userId,billDo,"sysJob",afUserAccountDo,bankPayType);
                }catch (Exception e) {
                    logger.info("withholdCashJob error", e);
                }
                if(map!=null){
                    returnjson.put("refId", map.get("refId"));
                    returnjson.put("success",true);
                    //returnjson.put("type", map.get("type"));
                    refId = NumberUtil.objToLongDefault(ObjectUtils.toString(map.get("refId")),0l);
                    afWithholdLogDo.setStatus(1);
                }else{
                    returnjson.put("success",false);
                    afWithholdLogDo.setStatus(0);
                }
                afWithholdLogDo.setRefId(refId);
                afWithholdLogDo.setRemark(map==null?"":map.toString());
                //插入代扣日志
                try{
                    afWithholdLogService.saveRecord(afWithholdLogDo);
                }catch (Exception e){
                    logger.error("withhold for add logs error:" + e);
                }
                return returnjson;
            }else if(actualAmount.compareTo(lowBillPrice)<0){
                logger.info("withhold for borrowbill fail for actualAmount less than lowBillPrice,userId:"+userId + ",billIds:"+billIds);
                returnjson.put("success",false);
                returnjson.put("msg","发起扣款金额小于门槛金额");
                return returnjson;
            }else if(actualAmount.compareTo(lowBillPrice)>=0){
                Long cardId1 = afUserWithholdDo.getCardId1();
                Long cardId2 = afUserWithholdDo.getCardId2();
                Long cardId3 = afUserWithholdDo.getCardId3();
                Long cardId4 = afUserWithholdDo.getCardId4();
                Long cardId5 = afUserWithholdDo.getCardId5();
                cardMap.put(1,cardId1);
                cardMap.put(2,cardId2);
                cardMap.put(3,cardId3);
                cardMap.put(4,cardId4);
                cardMap.put(5,cardId5);
            }
            UpsCollectRespBo upsResult = null;
            for(int j=1;j<=cardMap.size();j++) {
                Long cardId = cardMap.get(j);
                if(cardId!=null&&cardId!=0){//银行卡支付
                    AfUserBankcardDo card = afUserBankcardService.getUserBankcardByIdAndStatus(cardId);
                    if(null == card){
                        continue;
                    }
                    try{
                        map = afRepaymentService.createRepaymentByBankOrRebate(BigDecimal.ZERO,repaymentAmount, actualAmount,null, userAmount, billIds,
                                cardId,userId,billDo,"sysJob",afUserAccountDo,bankPayType);
                    }catch (Exception e) {
                        logger.info("withholdCashJob error", e);
                    }
                    if(map!=null){
                        // 代收
                        if (map.get("resp") != null
                                && map.get("resp") instanceof UpsCollectRespBo) {
                            upsResult = (UpsCollectRespBo) map.get("resp");
                        }
                        returnjson.put("refId", map.get("refId"));
                        refId = NumberUtil.objToLongDefault(ObjectUtils.toString(map.get("refId")),0l);
                    }
                    afWithholdLogDo.setCardId(cardId);
                    afWithholdLogDo.setCardNumber(card.getCardNumber());
                    //returnjson.put("type", map.get("type"));
                    afWithholdLogDo.setRefId(refId);
                    if (upsResult != null && upsResult.isSuccess()) {
                        returnjson.put("outTradeNo", upsResult.getOrderNo());
                        returnjson.put("tradeNo", upsResult.getTradeNo());
                        returnjson.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
                        afWithholdLogDo.setStatus(1);
                        returnjson.put("success",true);
                        afWithholdLogDo.setRemark(map.toString());
                        //插入代扣日志
                        try{
                            afWithholdLogService.saveRecord(afWithholdLogDo);
                        }catch (Exception e){
                            logger.error("withhold for add logs error:" + e);
                        }
                        break;
                    }else{
                        afWithholdLogDo.setStatus(0);
                        returnjson.put("success",true);
                        afWithholdLogDo.setRemark(map==null?"":map.toString());
                        //插入代扣日志
                        try{
                            afWithholdLogService.saveRecord(afWithholdLogDo);
                        }catch (Exception e){
                            logger.error("withhold for add logs error:" + e);
                        }
                    }

                }
            }
        }catch (Exception e) {
            logger.error("borrowbill repayment fail" + e);
            //throw new FanbeiException("sys exception",FanbeiExceptionCode.SYSTEM_ERROR);
        }
        finally {
            //如果有其中一笔账单解锁
            if(StringUtil.isNotEmpty(billIds)){
                //更新还款表的款款方式
                //afRepaymentService.updateRepaymentName(refId);
                //分期账单解锁
                afBorrowBillService.updateBorrowBillUnLockByIds(billIds);
            }
        }
        return returnjson;
    }

    /**
     * 查看是否存在还款中的账单
     *
     * @param borrowBillList
     * @return
     */
    private boolean constainsRepayingBill(List<AfBorrowBillDo> borrowBillList) {
        if (CollectionUtil.isEmpty(borrowBillList)) {
            return false;
        }
        boolean constainRepaying = false;
        for (AfBorrowBillDo borrowBillInfo : borrowBillList) {
            if (BorrowBillStatus.DEALING.getCode().equals(borrowBillInfo.getStatus())) {
                constainRepaying = true;
                break;
            }
        }
        return constainRepaying;
    }
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}
