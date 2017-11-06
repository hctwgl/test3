package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/userWithhold")
public class UserWithholdController extends BaseController {
    BigDecimal showAmount;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    AfUserCouponService afUserCouponService;
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
        BigDecimal repaymentAmount = BigDecimal.ZERO;
        Long borrowId = NumberUtil.objToLongDefault(
                        ObjectUtils.toString(request.getParameter(
                                "borrowId")), 0l);
        //将该笔订单锁住
        if(afBorrowCashService.updateBorrowCashLock(borrowId)==0){
            logger.info("withhold for borrowcash fail for lock,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg","订单正在还款中");
            return returnjson;
        }
        Long cardId1 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId1")),0l);
        Long cardId2 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId2")),0l);
        Long cardId3 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId3")),0l);
        Long cardId4 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId4")),0l);
        Long cardId5 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId5")),0l);

        AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService
                .getLastRepaymentBorrowCashByBorrowId(borrowId);
        if (borrowId == null || borrowId == 0) {
            logger.info("withhold for borrowcash fail for BORROW_CASH_NOT_EXIST_ERROR,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
            return returnjson;
        }
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
                .getRenewalDetailByBorrowId(borrowId);
        if (lastAfRenewalDetailDo != null
                && AfRenewalDetailStatus.PROCESS.getCode().equals(
                lastAfRenewalDetailDo.getStatus())) {
            logger.info("withhold for borrowcash fail for HAVE_A_PROCESS_RENEWAL_DETAIL,userId:"+userId + ",borrowId:"+borrowId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
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

        if (!yiBaoUtility.checkCanNext(userId, 0)) {
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING_ERROR);
        }

        AfBorrowCashDo afBorrowCashDo = afBorrowCashService
                .getBorrowCashByrid(borrowId);
        if (afBorrowCashDo != null) {
            BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(),
                            afBorrowCashDo.getSumOverdue(),
                            afBorrowCashDo.getOverdueAmount(),
                            afBorrowCashDo.getRateAmount(),
                            afBorrowCashDo.getSumRate());

            BigDecimal temAmount = BigDecimalUtil.subtract(allAmount,
                    afBorrowCashDo.getRepayAmount());
            repaymentAmount = temAmount;
        }

        Map<String, Object> map = null;
        Map<Object, Long> cardMap = new HashMap<>();
        cardMap.put(1,cardId1);
        cardMap.put(2,cardId2);
        cardMap.put(3,cardId3);
        cardMap.put(4,cardId4);
        cardMap.put(5,cardId5);
        UpsCollectRespBo upsResult = null;
        JSONObject returnjson = new JSONObject();
        for(int j=0;j<cardMap.size();j++){
            Long cardId = cardMap.get(j);
            if(cardId!=0l){
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
                if (null == card) {
                    continue;
                }
                map = afRepaymentBorrowCashService.createRepayment(BigDecimal.ZERO,
                        repaymentAmount, repaymentAmount, null, BigDecimal.ZERO,
                        borrowId, cardId, userId, request.getRemoteAddr(), userDto);

                // 代收
                if (map.get("resp") != null
                        && map.get("resp") instanceof UpsCollectRespBo) {
                    upsResult = (UpsCollectRespBo) map.get("resp");
                }

                if (upsResult != null && upsResult.isSuccess()) {
                    returnjson.put("outTradeNo", upsResult.getOrderNo());
                    returnjson.put("tradeNo", upsResult.getTradeNo());
                    returnjson.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
                    returnjson.put("refId", map.get("refId"));
                    returnjson.put("type", map.get("type"));
                    returnjson.put("success",true);
                    break;
                }

            }
        }
//      //借款账单解锁
        afBorrowCashService.updateBorrowCashUnLock(borrowId);
        return returnjson;
    }

    /**
     * 借款代扣
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/borrowBill", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject CreatRepaymenBill(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long userId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("userId")),null);

        String billIds1 = afBorrowBillService.getBillIdsByUserId(userId);
        if(StringUtil.isBlank(billIds1)){
            logger.info("用户无分期账单，userId="+userId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_BILL_UPDATE_ERROR);
            return returnjson;
        }
        String billIds = "";
        //遍历账单，加锁
        String[] billStr = billIds1.split(",");
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
            logger.info("用户无未还分期账单，userId="+userId);
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.BORROW_BILL_UPDATE_ERROR);
            return returnjson;
        }
        AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(billIds);
        BigDecimal repaymentAmount = billDo.getBillAmount();
        BigDecimal actualAmount = repaymentAmount;
        Long cardId1 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId1")),0l);
        Long cardId2 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId2")),0l);
        Long cardId3 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId3")),0l);
        Long cardId4 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId4")),0l);
        Long cardId5 = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("cardId5")),0l);
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (afUserAccountDo == null) {
            JSONObject returnjson = new JSONObject();
            returnjson.put("success",false);
            returnjson.put("msg",FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
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
        }

        Map<String,Object> map;
        Map<Object, Long> cardMap = new HashMap<>();
        cardMap.put(1,cardId1);
        cardMap.put(2,cardId2);
        cardMap.put(3,cardId3);
        cardMap.put(4,cardId4);
        cardMap.put(5,cardId5);
        UpsCollectRespBo upsResult = null;
        JSONObject returnjson = new JSONObject();
        for(int j=0;j<cardMap.size();j++) {
            Long cardId = cardMap.get(j);
            if(cardId.longValue()>0){//银行卡支付
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
                if(null == card){
                    continue;
                }
                map = afRepaymentService.createRepayment(BigDecimal.ZERO,repaymentAmount, actualAmount,null, BigDecimal.ZERO, billIds,
                        cardId,userId,billDo,request.getRemoteAddr(),afUserAccountDo);
                // 代收
                if (map.get("resp") != null
                        && map.get("resp") instanceof UpsCollectRespBo) {
                    upsResult = (UpsCollectRespBo) map.get("resp");
                }

                if (upsResult != null && upsResult.isSuccess()) {
                    returnjson.put("outTradeNo", upsResult.getOrderNo());
                    returnjson.put("tradeNo", upsResult.getTradeNo());
                    returnjson.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
                    returnjson.put("refId", map.get("refId"));
                    returnjson.put("type", map.get("type"));
                    break;
                }

            }
        }
        //分期账单解锁
        afBorrowBillService.updateBorrowBillUnLockByIds(billIds);


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
