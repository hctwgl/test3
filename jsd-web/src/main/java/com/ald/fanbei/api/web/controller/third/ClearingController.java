package com.ald.fanbei.api.web.controller.third;

import com.ald.fanbei.api.biz.bo.ClearingResqBo;
import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.CuiShouUtils;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.BizThirdRespCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.jsd.mgr.enums.CollectionBorrowStatus;
import com.ald.jsd.mgr.enums.CommonReviewStatus;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author yinxiangyu 2018年10月26日 下午15:14:32
 * @类现描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/finance")
public class ClearingController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;
    @Resource
    JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    CuiShouUtils cuiShouUtils;
    @Resource
    TransactionTemplate transactionTemplate;

    @RequestMapping(value = {"/getUserInfo"},method = RequestMethod.POST)
    @ResponseBody
    public ClearingResqBo getUserInfo(HttpServletRequest request){
        ClearingResqBo resqBo=new ClearingResqBo(BizThirdRespCode.SUCCESS.getCode(),"请求成功");
        try {
            String data= ObjectUtils.toString(request.getParameter("data"));
            String sign = ObjectUtils.toString(request.getParameter("sign"));
            //解析参数
            JSONObject object=JSON.parseObject(data);
            String mobile= object.getString("mobile");
            if(!checkSign(data, sign)){
                resqBo.setCode(BizThirdRespCode.CLEARING_USER_IS_NULL.getCode());
                resqBo.setMsg(BizThirdRespCode.CLEARING_USER_IS_NULL.getDesc());
            }else {
                JsdUserDo jsdUserDo=jsdUserService.getUserInfo(mobile);
                if(jsdUserDo==null){
                    resqBo.setCode(BizThirdRespCode.CLEARING_USER_IS_NULL.getCode());
                    resqBo.setMsg(BizThirdRespCode.CLEARING_USER_IS_NULL.getDesc());
                    return resqBo;
                }
                Map<String,String>  userInfoMap=new HashMap<>();
                userInfoMap.put("userName",jsdUserDo.getRealName());
                userInfoMap.put("mobile",jsdUserDo.getMobile());
                userInfoMap.put("sex",jsdUserDo.getGender());
                userInfoMap.put("IdNumber",jsdUserDo.getIdNumber());
                userInfoMap.put("age",jsdUserDo.getBirthday());
                resqBo.setData(userInfoMap);
            }
            return resqBo;
        }catch (Exception e){
            logger.error("error message " + e);
            resqBo.setCode(BizThirdRespCode.SYSTEM_ERROR.getCode());
            resqBo.setMsg(BizThirdRespCode.SYSTEM_ERROR.getMsg());
            return resqBo;
        }
    }

    @RequestMapping(value = {"/getUserBorrowInfos"},method = RequestMethod.POST)
    @ResponseBody
    public ClearingResqBo getBorrowInfos(HttpServletRequest request) {
        ClearingResqBo resqBo = new ClearingResqBo(BizThirdRespCode.SUCCESS.getCode(), "请求成功");
        try {
            String data = ObjectUtils.toString(request.getParameter("data"));
            String sign = ObjectUtils.toString(request.getParameter("sign"));
            //解析参数
            JSONObject object = JSON.parseObject(data);
            String mobile = object.getString("mobile");
            if (!checkSign(data, sign)) {
                resqBo.setCode(BizThirdRespCode.CLEARING_USER_IS_NULL.getCode());
                resqBo.setMsg(BizThirdRespCode.CLEARING_USER_IS_NULL.getDesc());
            } else {
                JsdUserDo jsdUserDo = jsdUserService.getUserInfo(mobile);
                if (jsdUserDo == null) {
                    resqBo.setCode(BizThirdRespCode.CLEARING_USER_IS_NULL.getCode());
                    resqBo.setMsg(BizThirdRespCode.CLEARING_USER_IS_NULL.getDesc());
                    return resqBo;
                }
                List<JsdBorrowCashDo> borrowCash = jsdBorrowCashService.getBorrowCashsInfos(jsdUserDo.getRid());
                List borrowList = new ArrayList();
                for (JsdBorrowCashDo cash : borrowCash) {
                    JsdBorrowLegalOrderCashDo borrowLegalOrderCash = jsdBorrowLegalOrderCashService.getLegalOrderByBorrowId(cash.getRid());
                    Map<String, String> map = new HashMap<>();
                    BigDecimal legalOrderSumAmount=BigDecimal.ZERO;
                    BigDecimal legalOrderOverdueAmount=BigDecimal.ZERO;
                    BigDecimal legalOrderRepayAmount=BigDecimal.ZERO;
                    BigDecimal legalOrderNoReductionAmount=BigDecimal.ZERO;
                    if(borrowLegalOrderCash!=null){
                        legalOrderSumAmount = BigDecimalUtil.add(borrowLegalOrderCash.getAmount(), borrowLegalOrderCash.getOverdueAmount(), borrowLegalOrderCash.getSumRepaidOverdue(),
                                    borrowLegalOrderCash.getInterestAmount(), borrowLegalOrderCash.getSumRepaidInterest(),
                                    borrowLegalOrderCash.getPoundageAmount(), borrowLegalOrderCash.getSumRepaidPoundage());
                        legalOrderOverdueAmount = BigDecimalUtil.add(borrowLegalOrderCash.getOverdueAmount(),borrowLegalOrderCash.getSumRepaidOverdue());
                        legalOrderRepayAmount = BigDecimalUtil.add(borrowLegalOrderCash.getRepaidAmount());
                        legalOrderNoReductionAmount= BigDecimalUtil.add(borrowLegalOrderCash.getInterestAmount(), borrowLegalOrderCash.getSumRepaidInterest(),
                                                     borrowLegalOrderCash.getPoundageAmount(), borrowLegalOrderCash.getSumRepaidPoundage(),borrowLegalOrderCash.getAmount());
                    }
                    map.put("company", "绿游");
                    map.put("productType", "极速贷");
                    map.put("productName", "借吧");
                    map.put("planRepayTime", String.valueOf(cash.getGmtPlanRepayment()));
                    map.put("status", cash.getStatus());
                    map.put("orderNo", cash.getBorrowNo());
                    map.put("tradeTime", String.valueOf(cash.getGmtCreate()));
                    map.put("sumAmount", String.valueOf(BigDecimalUtil.add(cash.getAmount(),
                            cash.getOverdueAmount(), cash.getSumRepaidOverdue(),
                            cash.getInterestAmount(), cash.getSumRepaidInterest(),
                            cash.getPoundageAmount(), cash.getSumRepaidPoundage(),legalOrderSumAmount)));
                    map.put("repayAmount", String.valueOf(BigDecimalUtil.add(cash.getRepayAmount(),legalOrderRepayAmount)));
                    map.put("restAmount", String.valueOf(jsdBorrowLegalOrderCashService.calculateLegalRestAmount(cash, borrowLegalOrderCash)));
                    map.put("day",cash.getType());
                    map.put("noReductionAmount",String.valueOf(BigDecimalUtil.add(cash.getAmount(),
                            cash.getInterestAmount(), cash.getSumRepaidInterest(),
                            cash.getPoundageAmount(), cash.getSumRepaidPoundage(),legalOrderNoReductionAmount)));
                    map.put("companyId","JSD");
                    map.put("overdueAmount",String.valueOf(BigDecimalUtil.add(cash.getOverdueAmount(),cash.getSumRepaidOverdue(),legalOrderOverdueAmount)));
                    borrowList.add(map);
                }
                resqBo.setData(borrowList);
            }
            return resqBo;
        } catch (Exception e) {
            logger.error("error message " + e);
            resqBo.setCode(BizThirdRespCode.SYSTEM_ERROR.getCode());
            resqBo.setMsg(BizThirdRespCode.SYSTEM_ERROR.getDesc());
            return resqBo;
        }
    }

    @RequestMapping(value = {"/inAccount"},method = RequestMethod.POST)
    @ResponseBody
    public ClearingResqBo offlineRepay(HttpServletRequest request){
        ClearingResqBo resqBo = new ClearingResqBo(BizThirdRespCode.SUCCESS.getCode(), "请求成功");
        try {
            String data = ObjectUtils.toString(request.getParameter("data"));
            String sign = ObjectUtils.toString(request.getParameter("sign"));
            if (!checkSign(data, sign)) {
                resqBo.setCode(BizThirdRespCode.CLEARING_USER_IS_NULL.getCode());
                resqBo.setMsg(BizThirdRespCode.CLEARING_USER_IS_NULL.getDesc());
            } else {
                //解析参数
                JSONObject object = JSON.parseObject(data);
                String borrowNo = object.getString("borrowNo");
                String accountAmount = object.getString("accountAmount");
                String operator = object.getString("operator");
                String isBalance = object.getString("isBalance");
                String remark = object.getString("remark");
                JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByBorrowNo(borrowNo);
                JsdBorrowLegalOrderCashDo legalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
                if(new BigDecimal(accountAmount).compareTo(jsdBorrowLegalOrderCashService.calculateLegalRestAmount(borrowCashDo,legalOrderCashDo))>0){
                    resqBo.setCode(BizThirdRespCode.CLEARING_IN_ACCOUNT_AMOUNT_ERR.getCode());
                    resqBo.setMsg(BizThirdRespCode.CLEARING_IN_ACCOUNT_AMOUNT_ERR.getDesc());
                    return resqBo;
                }
                String dataId = String.valueOf(borrowCashDo.getRid() + borrowCashDo.getRenewalNum());
                jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo, legalOrderCashDo, accountAmount, null, borrowCashDo.getUserId(), JsdRepayType.SETTLE_SYSTEM, null, null, null, dataId, operator+"_"+remark);
                if(YesNoStatus.YES.getCode().equals(isBalance)){
                    String reviewRemark = "管理员【" + operator + "】操作强制结清，原因：" + remark;

                    JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowCashDo.getTradeNoXgxy());
                    JsdBorrowLegalOrderDo legalOrderDo = jsdBorrowLegalOrderService.getLastOrderByBorrowId(cashDo.getRid());
                    JsdCollectionBorrowDo collBorrowDo = jsdCollectionBorrowService.selectByBorrowId(cashDo.getRid());

                    if(CollectionBorrowStatus.WAIT_FINISH.name().equals(collBorrowDo.getStatus())) {
                        resqBo.setCode(BizThirdRespCode.CLEARING_LOAN_SETTLE_APPLY.getCode());
                        resqBo.setMsg(BizThirdRespCode.CLEARING_LOAN_SETTLE_APPLY.getDesc());
                        return resqBo;
                    }

                    JsdCollectionBorrowDo collBorrowDoForMod = this.buildCollectionBorrowDoForMod(collBorrowDo.getRid(),
                            CollectionBorrowStatus.MANUAL_FINISHED.name(), operator, CommonReviewStatus.PASS.name(), reviewRemark);

                    JsdBorrowCashDo cashDoForMod = new JsdBorrowCashDo();
                    cashDoForMod.setRid(cashDo.getRid());
                    cashDoForMod.setStatus(JsdBorrowCashStatus.FINISHED.name());
                    cashDoForMod.setRemark(reviewRemark);

                    transactionTemplate.execute(new TransactionCallback<Integer>() {
                        public Integer doInTransaction(TransactionStatus status) {
                            jsdBorrowCashService.updateById(cashDoForMod);
                            jsdCollectionBorrowService.updateById(collBorrowDoForMod);
                            cuiShouUtils.collectImport(legalOrderDo.getRid().toString());
                            jsdNoticeRecordService.dealBorrowNoticed(cashDo, XgxyBorrowNoticeBo.gen(cashDo.getTradeNoXgxy(), XgxyBorrowNotifyStatus.FINISHED.name(), "强制结清"));
                            return 1;
                        }
                    });
                }
            }
            return resqBo;
        }catch (Exception e){
            logger.error("error message " + e);
            resqBo.setCode(BizThirdRespCode.SYSTEM_ERROR.getCode());
            resqBo.setMsg(BizThirdRespCode.SYSTEM_ERROR.getDesc());
            return resqBo;
        }
    }

    private JsdCollectionBorrowDo buildCollectionBorrowDoForMod(Long id, String status, String reviewer, String reviewStatus, String reviewRemark) {
        JsdCollectionBorrowDo collBorrowDoForMod = new JsdCollectionBorrowDo();
        collBorrowDoForMod.setRid(id);
        collBorrowDoForMod.setStatus(status);
        collBorrowDoForMod.setReviewer(reviewer);
        collBorrowDoForMod.setReviewStatus(reviewStatus);
        collBorrowDoForMod.setReviewRemark(reviewRemark);
        return collBorrowDoForMod;
    }

    public boolean checkSign(String data, String sign) {
        String finalSign = Base64.encode(AesUtil.encrypt(data, getKey()));
        if(! finalSign.equalsIgnoreCase(sign)) {
            logger.error("Sign error, reqSign=" + sign + ", finalSign=" + finalSign);
            return false;
        }
        return true;
    }

    public String getKey() {
        return "111111111";
    }
}
