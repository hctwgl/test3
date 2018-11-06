package com.ald.fanbei.api.web.controller.third;

import com.ald.fanbei.api.biz.bo.ClearingResqBo;
import com.ald.fanbei.api.biz.bo.FinanceSystemRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CuiShouUtils;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.exception.BizThirdRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.JsdCashDto;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yinxiangyu 2018年10月26日 下午15:14:32
 * @类现描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/finance")
public class FinanceController {

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
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;

    private static final String SING="FINANACE_JSD";
    private static final String DATA_TYPE1="ACTUAL_PAID";	//实付数据
    private static final String DATA_TYPE2="ACTUAL_INCOME";	//实收数据
    private static final String DATA_TYPE3="PROMISE_INCOME";	//应收数据

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
                userInfoMap.put("age", jsdUserDo.getBirthday());
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
                jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo, legalOrderCashDo, accountAmount, "", borrowCashDo.getUserId(), JsdRepayType.SETTLE_SYSTEM, null, null, null, dataId, operator+"_"+remark);
            }
            return resqBo;
        }catch (Exception e){
            logger.error("error message " + e);
            resqBo.setCode(BizThirdRespCode.SYSTEM_ERROR.getCode());
            resqBo.setMsg(BizThirdRespCode.SYSTEM_ERROR.getDesc());
            return resqBo;
        }
    }

    @RequestMapping(value = {"/getBorrowDetail"},method = RequestMethod.POST)
    @ResponseBody
    public ClearingResqBo getBorrowDetail(HttpServletRequest request){
        ClearingResqBo resqBo=new ClearingResqBo(BizThirdRespCode.SUCCESS.getCode(),"请求成功");
        try {
            String data= ObjectUtils.toString(request.getParameter("data"));
            String sign = ObjectUtils.toString(request.getParameter("sign"));
            if(!checkSign(data, sign)){
                resqBo.setCode(BizThirdRespCode.CLEARING_BORROW_IS_NULL.getCode());
                resqBo.setMsg(BizThirdRespCode.CLEARING_BORROW_IS_NULL.getDesc());
            }else {
                //解析参数
                JSONObject object=JSON.parseObject(data);
                String borrowNo= object.getString("borrowNo");
                JsdBorrowCashDo jsdBorrowCashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
                if(jsdBorrowCashDo==null){
                    resqBo.setCode(BizThirdRespCode.CLEARING_BORROW_IS_NULL.getCode());
                    resqBo.setMsg(BizThirdRespCode.CLEARING_BORROW_IS_NULL.getDesc());
                    return resqBo;
                }
                Map<String, Object> map = new HashMap<>();
                JsdBorrowCashDo jsdCashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
                JsdBorrowLegalOrderCashDo borrowLegalOrderCash = jsdBorrowLegalOrderCashService.getLegalOrderByBorrowId(jsdBorrowCashDo.getRid());
                List<JsdBorrowCashRepaymentDo> jsdBorrowCashRepaymentDo=jsdBorrowCashRepaymentService.getRepayByBorrowId(jsdCashDo.getRid());
                List<JsdBorrowCashRenewalDo> renewalDetailDos=jsdBorrowCashRenewalService.getJsdRenewalByBorrowId(jsdCashDo.getRid());
                Map renewalMap= buildRenewalInfos(renewalDetailDos);
                map.put("repayInfos", buildeRepayInfos(jsdBorrowCashRepaymentDo));
                map.put("goodsInfo", buildOrderInfo(jsdBorrowCashService.getGoodsInfoByBorrowNo(borrowNo)));
                map.put("borrowInfo", buildBorrowInfo(jsdCashDo,borrowLegalOrderCash));
                map.put("renewalInfos",renewalMap.get("renewalInfos"));
                map.put("renewalCount",renewalDetailDos.size());
                map.put("repayTotalCount",jsdBorrowCashRepaymentDo.size());
                map.put("renewalAllSumAmount", renewalMap.get("renewalAllSumAmount"));
                resqBo.setData(map);
            }
            return resqBo;
        }catch (Exception e){
            logger.error("error message " + e);
            resqBo.setCode(BizThirdRespCode.SYSTEM_ERROR.getCode());
            resqBo.setMsg(BizThirdRespCode.SYSTEM_ERROR.getMsg());
            return resqBo;
        }
    }

    private Map buildRenewalInfos(List<JsdBorrowCashRenewalDo> renewalDetailDos) {
        Iterator<JsdBorrowCashRenewalDo> iterator=renewalDetailDos.iterator();
        Map map=new HashMap();
        BigDecimal renewalAllSumAmount=BigDecimal.ZERO;
        List list=new ArrayList();
        while (iterator.hasNext()){
            Map<String,String> renewalMap=new HashMap();
            JsdBorrowCashRenewalDo renewalDetailDo=iterator.next();
            BigDecimal renewalSumAmount=renewalDetailDo.getRenewalAmount().add(renewalDetailDo.getNextPoundage());
            renewalMap.put("renewalId", String.valueOf(renewalDetailDo.getRid()));
            renewalMap.put("renewalAmount", String.valueOf(renewalDetailDo.getRenewalAmount()));
            renewalMap.put("renewalSumAmount", String.valueOf(renewalSumAmount));
            renewalMap.put("renewalRepayCapital", String.valueOf(renewalDetailDo.getRid()));
            renewalMap.put("renewalRepayLastAmount", String.valueOf(renewalDetailDo.getActualAmount()));
            renewalMap.put("renewalTime", DateUtil.formatDate(renewalDetailDo.getGmtCreate(),"yyyy-MM-dd HH:mm:ss"));
            renewalAllSumAmount=renewalAllSumAmount.add(renewalSumAmount);
            list.add(renewalMap);
        }
        map.put("renewalAllSumAmount",renewalAllSumAmount);
        map.put("renewalInfos",list);
        return map;
    }

    private Map buildOrderInfo(JsdCashDto jsdCashDto) {
        Map<String, String> goodsInfo= new HashMap<>();
        if(jsdCashDto!=null) {
            goodsInfo.put("goodsDeliveryUserName", jsdCashDto.getDeliveryUser());
            goodsInfo.put("goodsDeliveryMobile", jsdCashDto.getDeliveryPhone());
            goodsInfo.put("goodsDeliveryAddress", jsdCashDto.getAddress());
            goodsInfo.put("goodsOrderNo", jsdCashDto.getAddress());
            goodsInfo.put("goodsName", jsdCashDto.getGoodsName());
            goodsInfo.put("goodsAmount", String.valueOf(jsdCashDto.getPriceAmount()));
        }
        return goodsInfo;
    }

    private List buildeRepayInfos(List<JsdBorrowCashRepaymentDo> jsdBorrowCashRepaymentDo) {
        Iterator<JsdBorrowCashRepaymentDo> iterator=jsdBorrowCashRepaymentDo.iterator();
        List list=new ArrayList();
        while(iterator.hasNext()){
            JsdBorrowCashRepaymentDo  repaymentBorrowCashDo=iterator.next();
            Map<String,String> map=new HashMap<>();
            map.put("repayId", String.valueOf(repaymentBorrowCashDo.getRid()));
            map.put("accountTime", DateUtil.formatDate(repaymentBorrowCashDo.getGmtCreate(),"yyyy-MM-dd HH:mm:ss") );
            map.put("repayAmount", String.valueOf(repaymentBorrowCashDo.getRepaymentAmount()));
            map.put("name", repaymentBorrowCashDo.getName());
            map.put("remark", repaymentBorrowCashDo.getRemark());
            list.add(map);
        }
        return list;
    }

    private Map buildBorrowInfo(JsdBorrowCashDo jsdCashDto,JsdBorrowLegalOrderCashDo borrowLegalOrderCash) {
        BigDecimal legalOrderSumAmount=BigDecimal.ZERO;
        BigDecimal legalOrderOverdueAmount=BigDecimal.ZERO;
        BigDecimal legalOrderRepayAmount=BigDecimal.ZERO;
        BigDecimal legalServiceAmount=BigDecimal.ZERO;
        BigDecimal legalAmount=BigDecimal.ZERO;
        if(borrowLegalOrderCash!=null){
            legalOrderSumAmount = BigDecimalUtil.add(borrowLegalOrderCash.getAmount(), borrowLegalOrderCash.getOverdueAmount(), borrowLegalOrderCash.getSumRepaidOverdue(),
                    borrowLegalOrderCash.getInterestAmount(), borrowLegalOrderCash.getSumRepaidInterest(),
                    borrowLegalOrderCash.getPoundageAmount(), borrowLegalOrderCash.getSumRepaidPoundage());
            legalOrderOverdueAmount = BigDecimalUtil.add(borrowLegalOrderCash.getOverdueAmount(),borrowLegalOrderCash.getSumRepaidOverdue());
            legalOrderRepayAmount = BigDecimalUtil.add(borrowLegalOrderCash.getRepaidAmount());
            legalServiceAmount= BigDecimalUtil.add(borrowLegalOrderCash.getInterestAmount(), borrowLegalOrderCash.getSumRepaidInterest(),
                    borrowLegalOrderCash.getPoundageAmount(), borrowLegalOrderCash.getSumRepaidPoundage());
            legalAmount=borrowLegalOrderCash.getAmount();
        }
        Map<String,String> borrowInfo=new HashMap<>();
        borrowInfo.put("company", "绿游");
        borrowInfo.put("productType", "极速贷");
        borrowInfo.put("productName", "借吧");
        borrowInfo.put("planRepayTime", String.valueOf(jsdCashDto.getGmtPlanRepayment()));
        if(JsdBorrowCashStatus.FINISHED.name().equals(jsdCashDto.getStatus())){
            borrowInfo.put("npered","1");
        }else {
            borrowInfo.put("npered","0");
        }
        borrowInfo.put("nper","1");
        borrowInfo.put("orderNo", jsdCashDto.getBorrowNo());
        borrowInfo.put("tradeTime", String.valueOf(jsdCashDto.getGmtCreate()));
        borrowInfo.put("sumAmount", String.valueOf(BigDecimalUtil.add(jsdCashDto.getAmount(),
                jsdCashDto.getOverdueAmount(), jsdCashDto.getSumRepaidOverdue(),
                jsdCashDto.getInterestAmount(), jsdCashDto.getSumRepaidInterest(),
                jsdCashDto.getPoundageAmount(), jsdCashDto.getSumRepaidPoundage(),legalOrderSumAmount)));
        borrowInfo.put("repayAmount", String.valueOf(BigDecimalUtil.add(jsdCashDto.getRepayAmount(),legalOrderRepayAmount)));
        borrowInfo.put("restAmount", String.valueOf(jsdBorrowLegalOrderCashService.calculateLegalRestAmount(jsdCashDto, borrowLegalOrderCash)));
        borrowInfo.put("day",jsdCashDto.getType());
        borrowInfo.put("amount",String.valueOf(BigDecimalUtil.add(jsdCashDto.getAmount(),legalAmount)));
        borrowInfo.put("overdueAmount",String.valueOf(BigDecimalUtil.add(jsdCashDto.getOverdueAmount(),jsdCashDto.getSumRepaidOverdue(),legalOrderOverdueAmount)));
        borrowInfo.put("serviceAmount",String.valueOf(BigDecimalUtil.add(legalServiceAmount,
                jsdCashDto.getInterestAmount(), jsdCashDto.getSumRepaidInterest(),
                jsdCashDto.getPoundageAmount(), jsdCashDto.getSumRepaidPoundage())));
        borrowInfo.put("nperAmount",String.valueOf(BigDecimalUtil.add(jsdCashDto.getAmount(),
                jsdCashDto.getOverdueAmount(), jsdCashDto.getSumRepaidOverdue(),
                jsdCashDto.getInterestAmount(), jsdCashDto.getSumRepaidInterest(),
                jsdCashDto.getPoundageAmount(), jsdCashDto.getSumRepaidPoundage(),legalOrderSumAmount)));
        borrowInfo.put("status", jsdCashDto.getStatus());
        return borrowInfo;
    }

    /**
     * 统计清结算系统对账所需数据
     * @Param request {@link FinanceSystemRespBo} 对象
     *@return 根据数据类型得到所需数据 <code>List<code/>
     *
     * **/
    @RequestMapping(value = {"/getData"}, method = RequestMethod.POST)
    @ResponseBody
    public FinanceSystemRespBo getData(HttpServletRequest request){

        FinanceSystemRespBo finance = new FinanceSystemRespBo();
        try {
            String params = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject paramsJson = JSON.parseObject(params);
            String data = paramsJson.getString("data");
            String timestamp = paramsJson.getString("timestamp");
            String sign = paramsJson.getString("sign");
            String dataType = paramsJson.getString("dataType");

            logger.info("getData is begin  data=" + data + ",timestamp=" + timestamp + ",sign1=" + sign + ",dataType= " + dataType);

            if (StringUtil.equals(sign, DigestUtil.MD5(SING + timestamp))) {// 验签成功

                List<FinaneceDataDo> list = new ArrayList<FinaneceDataDo>();
                if (DATA_TYPE1.equals(dataType)) {    //实付数据
                    list = jsdBorrowCashService.getPaymentDetail();
                } else if (DATA_TYPE2.equals(dataType)) {    //实收数据
                    list = jsdBorrowCashRepaymentService.getRepayData();       //还款数据
                    list.addAll(jsdBorrowCashRenewalService.getRenewalData());   // 续借数据
                } else if (DATA_TYPE3.equals(dataType)) {    //应收数据
                    list = jsdBorrowCashService.getPromiseIncomeDetail();
                } else {
                    logger.info("dataType is not exist");
                }

                finance.setData(JSONObject.toJSONString(list));
                finance.setCode(BizThirdRespCode.SUCCESS.getCode());
                finance.setMsg(BizThirdRespCode.SUCCESS.getMsg());
                return finance;
            }

        } catch (Exception e) {
            logger.error("getData hava a Exception, e = "+e+" and currTime = "+new Date());
        }
        finance.setCode(BizThirdRespCode.FAILED.getCode());
        finance.setMsg(BizThirdRespCode.FAILED.getMsg());
        return finance;

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
