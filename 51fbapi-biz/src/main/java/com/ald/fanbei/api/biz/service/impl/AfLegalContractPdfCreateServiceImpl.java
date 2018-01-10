package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.EviDoc;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.biz.util.PdfCreateUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserSealDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("afLegalContractPdfCreateService")
public class AfLegalContractPdfCreateServiceImpl implements AfLegalContractPdfCreateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AfLegalContractPdfCreateServiceImpl.class);

    @Resource
    AfUserService afUserService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfESdkService afESdkService;
    @Resource
    AfRescourceLogService afRescourceLogService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfRenewalDetailDao afRenewalDetailDao;
    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    AfContractPdfDao afContractPdfDao;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfFundSideBorrowCashService afFundSideBorrowCashService;
    @Resource
    AfUserSealDao afUserSealDao;
    @Resource
    EviDoc eviDoc;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
    @Resource
    private EsignPublicInit esignPublicInit;
    @Resource
    private AfBorrowDao afBorrowDao;
    private static final String src = "F:/doc/";

    @Override
    public void protocolLegalInstalment(long userId, BigDecimal borrowAmount, Long orderId) {//分期
        try {
            Map map = new HashMap();
            AfUserAccountDo accountDo = getUserInfo(userId, map);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            if (null != orderId && 0 != orderId) {
                AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderById(orderId);
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(orderId);
                if (afBorrowLegalOrderCashDo != null){
//                    map.put("instalmentGmtCreate", afBorrowLegalOrderCashDo.getGmtCreate());
                    map.put("instalmentGmtCreate", simpleDateFormat.format(afBorrowLegalOrderCashDo.getGmtCreate()));
//                    map.put("instalmentRepayDay", afBorrowLegalOrderCashDo.getGmtPlanRepay());
                    map.put("instalmentRepayDay", simpleDateFormat.format(afBorrowLegalOrderCashDo.getGmtPlanRepay()));
//                    map.put("gmtEnd", afBorrowLegalOrderCashDo.getGmtPlanRepay());
                    map.put("gmtEnd", simpleDateFormat.format(afBorrowLegalOrderCashDo.getGmtPlanRepay()));
                    map.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate()+"%");//手续费率
                    map.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate()+"%");//利率
                }
                AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL.getCode());
                getResourceRate(map, afBorrowLegalOrderCashDo.getType(),afResourceDo,"borrow");
                if (afBorrowLegalOrderDo != null){
                    date = afBorrowLegalOrderDo.getGmtCreate();
                }
                if ("SEVEN".equals(afBorrowLegalOrderCashDo.getType())){
//                    map.put("gmtEnd", DateUtil.addDays(date, 6));
                    map.put("gmtEnd", simpleDateFormat.format(DateUtil.addDays(date, 6)));
                }else if ("FOURTEEN".equals(afBorrowLegalOrderCashDo.getType())){
//                    map.put("gmtEnd", DateUtil.addDays(date, 13));
                    map.put("gmtEnd", simpleDateFormat.format(DateUtil.addDays(date, 13)));
                }
                map.put("overdueRate","36%");
            }else {
//                getResourceRate(map, type,afResourceDo,"instalment");
                logger.error("orderId为空 => {}" + FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
                throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
            }
            map.put("borrowId", orderId);
            map.put("protocolCashType", "2");
            map.put("amountLower", borrowAmount);//借款本金
            map.put("amountCapital", toCapital(borrowAmount.doubleValue()));//借款本金大写金额
            map.put("gmtStart", simpleDateFormat.format(date));

            logger.info("orderId => {}",orderId,",map => {}",JSON.toJSONString(map));
            long time = new Date().getTime();
//            map.put("templatePath",src+"instalment"+".pdf");
            map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/instalment.pdf");
            map.put("PDFPath", src + accountDo.getUserName() + "instalment" + time + 1 + ".pdf");
            map.put("userPath", src + accountDo.getUserName() + "instalment" + time + 2 + ".pdf");
            map.put("selfPath", src + accountDo.getUserName() + "instalment" + time + 3 + ".pdf");
            map.put("secondPath", src + accountDo.getUserName() + "instalment" + time + 4 + ".pdf");
            map.put("fileName", accountDo.getUserName() + "instalment" + time + 4);
            if (!pdfCreate(map))
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            logger.info(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("protocolInstalment error 分期合同生成失败 => {}", e);
        }

    }

    private AfUserAccountDo getUserInfo(long userId, Map map) {
        AfUserDo afUserDo = afUserService.getUserById(userId);
        if (afUserDo == null) {
            logger.error("user not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
//        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        map.put("lender", lenderDo.getValue());// 出借人
        map.put("mobile", afUserDo.getMobile());// 联系电话
        GetSeal(map, afUserDo, accountDo,lenderDo);
        return accountDo;
    }

    private void GetSeal(Map map, AfUserDo afUserDo, AfUserAccountDo accountDo,AfResourceDo lenderDo) {
        try {
            AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
                map.put("CompanyUserSeal",companyUserSealDo.getUserSeal());
            }else {
                logger.error("公司印章不存在 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
            AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
            if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
                logger.error("创建个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            }
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());
            /*if (null != lenderDo.getValue()) {
                companyUserSealDo = afUserSealDao.selectByUserName(lenderDo.getValue());
                if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                    map.put("secondSeal", companyUserSealDo.getUserSeal());
                    map.put("secondAccoundId", companyUserSealDo.getUserAccountId());
                }else {
                    logger.error("创建第三方印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                    throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                }
            }*/
            AfUserDo investorUserDo = new AfUserDo();
            AfUserAccountDo investorAccountDo = new AfUserAccountDo();
            investorUserDo.setMobile(map.get("investorPhone").toString());
//            investorUserDo.setRid((Long)map.get("investorCardId"));
            investorUserDo.setRealName(map.get("investorName").toString());
            investorAccountDo.setIdNumber(map.get("investorCardId").toString());
            investorUserDo.setMajiabaoName("edspay");
            AfUserSealDo investorAfUserSealDo = afESdkService.getSealPersonal(investorUserDo, investorAccountDo);
            if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
                logger.error("创建e都市钱包用户印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
            map.put("secondSeal", investorAfUserSealDo.getUserSeal());
            map.put("secondAccoundId", investorAfUserSealDo.getUserAccountId());
            companyUserSealDo = afUserSealDao.selectByUserName("浙江楚橡信息科技股份有限公司");
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("thirdSeal", companyUserSealDo.getUserSeal());
                map.put("thirdAccoundId", companyUserSealDo.getUserAccountId());
            }else {
                logger.error("创建钱包印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
        }catch (Exception e){
            logger.error("UserSeal create error",e);
        }
    }

    private void getResourceRate(Map map, String type,AfResourceDo afResourceDo,String borrowType) {
        if (afResourceDo != null && afResourceDo.getValue2() != null){
            JSONArray array = new JSONArray();
            if ("instalment".equals(borrowType)){
                array = JSONObject.parseArray(afResourceDo.getValue3());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String consumeTag = jsonObject.get("consumeTag").toString();
                    if ("INTEREST_RATE".equals(consumeTag)){//借款利率
                        if ("SEVEN".equals(type)){
                            map.put("yearRate",jsonObject.get("consumeSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("yearRate",jsonObject.get("consumeFourteenDay"));
                        }
                    }
                    if ("SERVICE_RATE".equals(consumeTag)){//手续费利率
                        if ("SEVEN".equals(type)){
                            map.put("poundageRate",jsonObject.get("consumeSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("poundageRate",jsonObject.get("consumeFourteenDay"));
                        }
                    }
                    if ("OVERDUE_RATE".equals(consumeTag)){//逾期利率
                        if ("SEVEN".equals(type)){
                            map.put("overdueRate",jsonObject.get("consumeSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("overdueRate",jsonObject.get("consumeFourteenDay"));
                        }
                    }
                }
            }else if ("borrow".equals(borrowType)){
                array = JSONObject.parseArray(afResourceDo.getValue2());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String borrowTag = jsonObject.get("borrowTag").toString();
                    if ("INTEREST_RATE".equals(borrowTag)){//借款利率
                        if ("SEVEN".equals(type)){
                            map.put("yearRate",jsonObject.get("borrowSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("yearRate",jsonObject.get("borrowFourteenDay"));
                        }
                    }
                    if ("SERVICE_RATE".equals(borrowTag)){//手续费利率
                        if ("SEVEN".equals(type)){
                            map.put("poundageRate",jsonObject.get("borrowSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("poundageRate",jsonObject.get("borrowFourteenDay"));
                        }
                    }
                    if ("OVERDUE_RATE".equals(borrowTag)){//逾期利率
                        if ("SEVEN".equals(type)){
                            map.put("overdueRate",jsonObject.get("borrowSevenDay"));
                        }else if ("FOURTEEN".equals(type)){
                            map.put("overdueRate",jsonObject.get("borrowFourteenDay"));
                        }
                    }
                }
            }

        }
    }

    @Override
    public void protocolLegalCashLoan(Long borrowId, BigDecimal borrowAmount, long userId) {//借款
//        Long borrowId = NumberUtil.objToLongDefault(content.get("borrowId"), 0l);
//        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(content.get("borrowAmount"), new BigDecimal(0));
//        String userName = ObjectUtils.toString(content.get("userName"), "").toString();
        try {
            Map map = new HashMap();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            AfUserAccountDo accountDo = getUserInfo(userId, map);
            map.put("protocolCashType", "1");
            map.put("borrowId", borrowId);
            /*map.put("idNumber", accountDo.getIdNumber());
            map.put("realName", accountDo.getRealName());
            map.put("email", afUserDo.getEmail());//电子邮箱
            map.put("phone", afUserDo.getUserName());//联系方式*/
            AfBorrowCashDo afBorrowCashDo = null;
            map.put("amountCapital", "人民币" + toCapital(borrowAmount.doubleValue()));
            map.put("amountLower", "￥" + borrowAmount);
//            map.put("amountCapital", toCapital(borrowAmount.doubleValue()));
//            map.put("amountLower", borrowAmount);
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL.getCode());
            if (borrowId > 0) {
                afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                if (afBorrowCashDo != null) {
                    AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowIdNoStatus(borrowId);
                    if (afBorrowLegalOrderCashDo != null){
                        map.put("useType",afBorrowLegalOrderCashDo.getBorrowRemark());
					/*map.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate());//手续费率
					map.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate());//利率
					map.put("overdueRate","36");*/
                    }else {
//                        getResourceRate(map, afBorrowCashDo.getType(),afResourceDo,"borrow");
                    }
                    getResourceRate(map, afBorrowCashDo.getType(),afResourceDo,"borrow");
                    map.put("gmtCreate", simpleDateFormat.format(afBorrowCashDo.getGmtCreate()));// 出借时间
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());
                    if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                        map.put("gmtArrival", simpleDateFormat.format(afBorrowCashDo.getGmtArrival()));
                        Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                        Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                        Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                        map.put("repaymentDay", simpleDateFormat.format(repaymentDay));
                        map.put("lenderIdAmount", afBorrowCashDo.getAmount());
                        map.put("gmtPlanRepayment", simpleDateFormat.format(afBorrowCashDo.getGmtPlanRepayment()));
                    }
                }else {
                    logger.error("afBorrowCashDo not exist => {}" + FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
                    throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
                }
            }else {
                logger.error("borrowId not exist => {}" + FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
            }
            map.put("purpose", "个人消费");
            map.put("repayWay", "到期一次性还本付息");
            logger.info(JSON.toJSONString(map));
           /* List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
            Map<String, Object> rate = getObjectWithResourceDolist(list, borrowId);
            BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
            BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
            BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());
            BigDecimal overduePoundage = new BigDecimal(rate.get("overduePoundage").toString());

            BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
            BigDecimal overdue = bankService.add(poundage).add(overduePoundage);

            Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
            if (poundageRateCash != null) {
                poundage = new BigDecimal(poundageRateCash.toString());
            }

            map.put("dayRate", bankService.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");//日利率
            map.put("overdueRate", overdue.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");//逾期费率（日）
            map.put("poundageRate", poundage.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");//手续费率
            map.put("overduePoundageRate", overduePoundage.multiply(new BigDecimal(100)).setScale(4, BigDecimal.ROUND_HALF_UP) + "%");//逾期手续费率
            map.put("purpose", "个人消费");
            map.put("repayWay", "到期一次性还本付息");
            map.put("amountCapital", "人民币" + toCapital(borrowAmount.doubleValue()));
            map.put("amountLower", "￥" + borrowAmount);
            if (borrowId > 0) {
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                map.put("gmtCreate", afBorrowCashDo.getGmtCreate());// 出借人
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                if (afBorrowCashDo != null) {
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());
                    if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                        Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                        Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                        Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                        map.put("gmtTime", simpleDateFormat.format(afBorrowCashDo.getGmtArrival()) + "至" + simpleDateFormat.format(repaymentDay));
//                      map.put("repaymentDay", repaymentDay);
//                      AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
//                      map.put("lender", lenderDo.getValue());// 出借人
                        //查看有无和资金方关联，有的话，替换里面的借出人信息
                        AfFundSideInfoDo fundSideInfo = afFundSideBorrowCashService.getLenderInfoByBorrowCashId(borrowId);
                        if (fundSideInfo != null && StringUtil.isNotBlank(fundSideInfo.getName())) {
                            map.put("lender", fundSideInfo.getName());// 出借人
                            AfResourceDo lenderDo = new AfResourceDo();
                            lenderDo.setValue(fundSideInfo.getName());
                            secondSeal(map, lenderDo, afUserDo, accountDo);
                        } else {
                            AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
                            map.put("lender", lenderDo.getValue());// 出借人
                            secondSeal(map, lenderDo, afUserDo, accountDo);
                        }

                        if (null == map.get("companySelfSeal")) {
                            logger.error("公司印章不存在 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                            throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
                        }
                        if (null == map.get("personUserSeal")) {
                            logger.error("创建个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                            throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                        }
                        map.put("lenderIdNumber", rate.get("lenderIdNumber"));
                        map.put("lenderIdAmount", afBorrowCashDo.getAmount());
                        map.put("gmtPlanRepayment", simpleDateFormat.format(afBorrowCashDo.getGmtPlanRepayment()));
                    }
                }
            }*/
            long time = new Date().getTime();
//            map.put("templatePath",src+"cashLoan"+".pdf");
            map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/cashLoan.pdf");
            map.put("PDFPath", src + accountDo.getUserName() + "cashLoan" + time + 1 + ".pdf");
            map.put("userPath", src + accountDo.getUserName() + "cashLoan" + time + 2 + ".pdf");
            map.put("selfPath", src + accountDo.getUserName() + "cashLoan" + time + 3 + ".pdf");
            map.put("secondPath", src + accountDo.getUserName() + "cashLoan" + time + 4 + ".pdf");
            map.put("fileName", accountDo.getUserName() + "cashLoan" + time + 4);
            if (!pdfCreate(map))
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
            logger.info(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("protocolCashLoan error 借款合同生成失败 => {}", e);
        }

    }

    public Map<String, Object> getObjectWithResourceDolist(List<AfResourceDo> list, Long borrowId) {
        Map<String, Object> data = new HashMap<String, Object>();
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
        for (AfResourceDo afResourceDo : list) {
            if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
                if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

                    data.put("maxAmount", afResourceDo.getValue());
                    data.put("minAmount", afResourceDo.getValue1());

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
                    data.put("bankDouble", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashBaseBankDouble.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankDouble", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashPoundage.getCode())) {
                    data.put("poundage", afResourceDo.getValue());

                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashPoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("poundage", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
                    data.put("overduePoundage", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashOverduePoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("overduePoundage", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
                    data.put("bankRate", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BaseBankRate.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankRate", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashLender.getCode())) {
                    data.put("lender", afResourceDo.getValue());
                    data.put("lenderIdNumber", afResourceDo.getValue1());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("lender", borrow.getValue());
                            data.put("lenderIdNumber", borrow.getValue1());

                        }
                    }
                }
            } else {
                if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
                    data.put("borrowCashDay", afResourceDo.getValue());

                }
            }
        }

        return data;

    }

    @Override
    public void protocolLegalRenewal(long userId, Long borrowId, Long renewalId, int renewalDay, BigDecimal renewalAmount) {//续借
//        String userName = ObjectUtils.toString(content.get("userName"), "").toString();
//        Long borrowId = NumberUtil.objToLongDefault(content.get("borrowId"), 0l);
//        Long renewalId = NumberUtil.objToLongDefault(content.get("renewalId"), 0l);
//        int renewalDay = NumberUtil.objToIntDefault(content.get("renewalDay"), 0);
//        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(content.get("renewalAmount"), BigDecimal.ZERO);
        try {
            Map map = new HashMap();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            AfUserAccountDo accountDo = getUserInfo(userId, map);
            /*AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
            if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
                logger.error("创建个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            }
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());*/
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL.getCode());
            AfBorrowCashDo afBorrowCashDo = null;
            if (borrowId > 0) {
                afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                if (afBorrowCashDo != null) {
                    getResourceRate(map, afBorrowCashDo.getType(),afResourceDo,"borrow");
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                    if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                        Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                        Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                        Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                        map.put("gmtBorrowBegin", dateFormat.format(arrivalStart));//到账时间，借款起息日
                        map.put("gmtBorrowEnd", dateFormat.format(repaymentDay));//借款结束日
                        map.put("amountCapital", "人民币" +toCapital(afBorrowCashDo.getAmount().doubleValue()));
                        map.put("amountLower", "￥" + afBorrowCashDo.getAmount());
                    }
                }

                if (renewalId > 0) {
                    AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalId);
                    Date gmtCreate = afRenewalDetailDo.getGmtCreate();
                    Date gmtPlanRepayment = afRenewalDetailDo.getGmtPlanRepayment();
                    if (afRenewalDetailDo != null){
                        AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getLastOrderCashByBorrowId(afRenewalDetailDo.getBorrowId());
                        if (afBorrowLegalOrderCashDo != null){
                            map.put("useType",afBorrowLegalOrderCashDo.getBorrowRemark());
                            map.put("poundageRate",afBorrowLegalOrderCashDo.getPoundageRate()+"%");//手续费率
                            map.put("yearRate",afBorrowLegalOrderCashDo.getInterestRate()+"%");//利率
                            map.put("overdueRate","36%");
                        }
                    }
                    // 如果预计还款时间在申请日期之后，则在原预计还款时间的基础上加上续期天数，否则在申请日期的基础上加上续期天数，作为新的续期截止时间
                    if (gmtPlanRepayment.after(gmtCreate)) {
                        Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, afRenewalDetailDo.getRenewalDay()));
                        afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                        map.put("gmtRenewalBegin", dateFormat.format(gmtPlanRepayment));
                        map.put("gmtRenewalEnd", dateFormat.format(repaymentDay));
                        map.put("repaymentDay", dateFormat.format(repaymentDay));
                    } else {
                        Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtCreate, afRenewalDetailDo.getRenewalDay()));
                        afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                        map.put("gmtRenewalBegin", dateFormat.format(gmtCreate));
                        map.put("gmtRenewalEnd", dateFormat.format(repaymentDay));
                        map.put("repaymentDay", dateFormat.format(repaymentDay));
                    }
                    map.put("renewalAmountLower", "￥" + afRenewalDetailDo.getRenewalAmount());//续借金额小写
                    map.put("renewalGmtCreate", dateFormat.format(afRenewalDetailDo.getGmtCreate()));//续借时间
                    map.put("renewalAmountCapital",  "人民币" +toCapital(afRenewalDetailDo.getRenewalAmount().doubleValue()));//续借金额大写
                    map.put("repayAmountLower", "￥" + afRenewalDetailDo.getCapital());//续借金额小写
                    map.put("repayAmountCapital",  "人民币" +toCapital(afRenewalDetailDo.getCapital().doubleValue()));//续借金额大写
//				Date gmtRenewalBegin = afRenewalDetailDo.getGmtCreate();
//				Date gmtRenewalEnd = DateUtil.addDays(gmtRenewalBegin, afRenewalDetailDo.getRenewalDay());
                } else {
                    Date gmtPlanRepayment = afBorrowCashDo.getGmtPlanRepayment();
                    Date now = new Date(System.currentTimeMillis());
                    // 如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的续期截止时间
                    if (gmtPlanRepayment.after(now)) {
                        Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, renewalDay));
                        afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                        map.put("gmtRenewalBegin", dateFormat.format(gmtPlanRepayment));
                        map.put("gmtRenewalEnd", dateFormat.format(repaymentDay));
                        map.put("repaymentDay", dateFormat.format(repaymentDay));
                    } else {
                        Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, renewalDay));
                        afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                        map.put("gmtRenewalBegin", dateFormat.format(now));
                        map.put("gmtRenewalEnd", dateFormat.format(repaymentDay));
                        map.put("repaymentDay", dateFormat.format(repaymentDay));
                    }

                    map.put("renewalAmountLower", "￥" + renewalAmount);//续借金额小写
                    map.put("renewalAmountCapital",  "人民币" +toCapital(renewalAmount.doubleValue()));//续借金额大写
//				AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
//				BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
                    String yearRate = afResourceDo.getValue();
                    if (yearRate != null && !"".equals(yearRate)){
                        BigDecimal capital = afBorrowCashDo.getAmount().divide(BigDecimal.valueOf(100)).multiply(new BigDecimal(yearRate)).setScale(2, RoundingMode.HALF_UP);
                        map.put("repayAmountLower", "￥" + capital);//续借金额小写
                        map.put("repayAmountCapital",  "人民币" +toCapital(capital.doubleValue()));//续借金额大写
                    }
                }
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long time = new Date().getTime();
//            map.put("templatePath",src+"renewal"+".pdf");
            map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/renewal.pdf");
            map.put("PDFPath", src + accountDo.getUserName() + "renewal" + time + 1 + ".pdf");
            map.put("userPath", src + accountDo.getUserName() + "renewal" + time + 2 + ".pdf");
            map.put("selfPath", src + accountDo.getUserName() + "renewal" + time + 3 + ".pdf");
            map.put("secondPath", src + accountDo.getUserName() + "renewal" + time + 4 + ".pdf");
            map.put("fileName", accountDo.getUserName() + "renewal" + time + 4);
            if (!pdfCreate(map))
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            logger.info(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("protocolRenewal error 续借合同生成失败 =>{}", e);
        }

    }

    @Override
    public String getProtocalLegalByType(Integer debtType, String orderNo,String protocolUrl,String investorPhone,
                                         String investorName,String investorCardId) throws IOException {
        Map map = new HashMap();
        map.put("investorName",investorName);
        map.put("investorPhone",investorPhone);
        map.put("investorCardId",investorCardId);
        if (debtType == 1){
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null){
                logger.error("借款信息不存在 => {}",orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            return getPdfInfo(protocolUrl, map,afBorrowCashDo.getUserId(),afBorrowCashDo.getRid(),"cashLoan","1");
//            protocolLegalCashLoan(afBorrowCashDo.getRid(), afBorrowCashDo.getAmount(),afBorrowCashDo.getUserId());
        }else if (debtType == 2){
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null){
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null){
                    logger.error("分期订单不存在 => {}",orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
                return getPdfInfo(protocolUrl, map,afBorrowLegalOrderCashDo.getUserId(),afBorrowLegalOrderCashDo.getRid(),"instalment","2");
            }
            return getPdfInfo(protocolUrl, map,afBorrowDo.getUserId(),afBorrowDo.getRid(),"instalment","2");
        }
        return null;
    }

    private String getPdfInfo(String protocolUrl, Map map, Long userId,Long id,String type,String protocolCashType) throws IOException {
        AfUserAccountDo accountDo = getUserInfo(userId,map);
        long time = new Date().getTime();
        map.put("PDFPath", protocolUrl);
        map.put("borrowId", id);
        map.put("protocolCashType", protocolCashType);
        map.put("userPath", src + accountDo.getUserName() + type + time + 1 + ".pdf");
        map.put("selfPath", src + accountDo.getUserName() + type + time + 2 + ".pdf");
        map.put("secondPath", src + accountDo.getUserName() + type + time + 3 + ".pdf");
        map.put("thirdPath", src + accountDo.getUserName() + type + time + 4 + ".pdf");
        return getLegalContractPdf(map);
    }

    private String getLegalContractPdf(Map map) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        boolean result = true;
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("甲方盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("甲方盖章证书生成失败 => {}", e);
            result = false;
            return null;
        }finally {
            if (!result){
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }

        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.selfSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("丙方盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("丙方盖章证书生成失败 => {}", e);
            result = false;
            return null;
        }finally {
            if (!result){
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
            }
        }

        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.secondSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("乙方盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }
            map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("乙方盖章证书生成失败 => {}", e);
            result = false;
            return null;
        }finally {
            if (!result){
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
                file1 = new File(map.get("secondPath").toString());
                file1.delete();
            }
        }

        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.thirdSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("e都市钱包盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }
            map.put("esignIdFour", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("e都市钱包盖章证书生成失败 => {}", e);
            result = false;
            return null;
        }finally {
            if (!result){
                File file1 = new File(map.get("thirdPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
                file1 = new File(map.get("secondPath").toString());
                file1.delete();
            }
        }
        //存证暂时不用
        /*String evId = "";
        try {
            evId = eviPdf(map);
        } catch (Exception e) {
            logger.error("e签宝存证生成失败 => {}", e);
        }*/
        InputStream input = null;
        try {
            File file = new File(map.get("thirdPath").toString());
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
//                if (!"".equals(evId)) {
//                    afContractPdfDo.setEvId(evId);
//                }
                if ("1".equals(protocolCashType)) {//借款协议
                    afContractPdfDo.setType((byte) 1);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                } else if ("2".equals(protocolCashType)) {//分期服务协议
                    afContractPdfDo.setType((byte) 2);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                } else if ("3".equals(protocolCashType)) {//续借协议
                    afContractPdfDo.setType((byte) 3);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("renewalId"));
                }
                afContractPdfDao.insert(afContractPdfDo);
                return ossUploadResult.getUrl();
            }
        } catch (Exception e) {
            logger.error("证书上传oss失败 => {}", e.getMessage());
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
            File file1 = new File(map.get("PDFPath").toString());
            file1.delete();
            file1 = new File(map.get("userPath").toString());
            file1.delete();
            file1 = new File(map.get("selfPath").toString());
            file1.delete();
            file1 = new File(map.get("secondPath").toString());
            file1.delete();
            file1 = new File(map.get("thirdPath").toString());
            file1.delete();
        }
        return null;
    }

    private boolean pdfCreate(Map map) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        boolean result = true;
        try {
            PdfCreateUtil.create(map, fos, bos);
        } catch (Exception e) {
            logger.error("pdf合同生成失败 => {}", e.getMessage());
            result = false;
            return result;
        } finally {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
            if (null != bos) {
                bos.close();
            }
            if (!result){
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("甲方盖章证书生成失败 => {}",fileDigestSignResult);
                return result;
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("甲方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        }finally {
            if (!result){
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }

        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.selfSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("丙方盖章证书生成失败 => {}",fileDigestSignResult);
                return result;
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("丙方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        }finally {
            if (!result){
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.secondSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("乙方盖章证书生成失败 => {}",fileDigestSignResult);
                return result;
            }
            map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("乙方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        }finally {
            if (!result){
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
                file1 = new File(map.get("secondPath").toString());
                file1.delete();
            }
        }
        //存证暂时不用
        /*String evId = "";
        try {
            evId = eviPdf(map);
        } catch (Exception e) {
            logger.error("e签宝存证生成失败 => {}", e);
        }*/
        InputStream input = null;
        try {
            File file = new File(map.get("secondPath").toString());
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
//                if (!"".equals(evId)) {
//                    afContractPdfDo.setEvId(evId);
//                }
                if ("1".equals(protocolCashType)) {//借款协议
                    afContractPdfDo.setType((byte) 1);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                } else if ("2".equals(protocolCashType)) {//分期服务协议
                    afContractPdfDo.setType((byte) 2);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                } else if ("3".equals(protocolCashType)) {//续借协议
                    afContractPdfDo.setType((byte) 3);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("renewalId"));
                }
                afContractPdfDao.insert(afContractPdfDo);
            }
        } catch (Exception e) {
            logger.error("证书上传oss失败 => {}", e.getMessage());
            return true;
        } finally {
            if (null != input) {
                input.close();
            }
            File file1 = new File(map.get("PDFPath").toString());
            file1.delete();
            file1 = new File(map.get("userPath").toString());
            file1.delete();
            file1 = new File(map.get("selfPath").toString());
            file1.delete();
            file1 = new File(map.get("secondPath").toString());
            file1.delete();
        }
        return true;
    }

    private String eviPdf(Map map) {
        String filePath = map.get("secondPath").toString();
        String fileName = map.get("fileName").toString();
        String esignIdThird = map.get("esignIdThird").toString();
        String esignIdSecond = map.get("esignIdSecond").toString();
        String esignIdFirst = map.get("esignIdFirst").toString();
        eviDoc.initGlobalParameters(esignPublicInit.getProjectId(), esignPublicInit.getProjectSecret(), esignPublicInit.getEviUrl(), filePath, fileName, esignIdFirst, esignIdSecond, esignIdThird);
        // 用户获取文档保全Url和存证编号
        Map<String, String> eviMap = eviDoc.getEviUrlAndEvId();
        String evId = "";
        // 上传需要保全的文档
        if ("0".equals(eviMap.get("errCode"))) {
            String updateUrl = eviMap.get("文档保全上传Url");
            evId = eviMap.get("存证编号");
            logger.info("存证编号= " + evId);
            logger.info("文件上传地址= " + updateUrl);
            // 文件上传
            eviDoc.updateFileRequestByPost(updateUrl, map.get("secondPath").toString());
        }
        return evId;
    }

    private void secondSeal(Map map, AfResourceDo lenderDo, AfUserDo afUserDo, AfUserAccountDo accountDo) {
        AfUserSealDo companySealDo = afESdkService.selectUserSealByUserId(-1l);
        if (null != companySealDo && null != companySealDo.getUserSeal()) {
            map.put("companySelfSeal", companySealDo.getUserSeal());
        }

        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null != afUserSealDo && null != afUserSealDo.getUserSeal() && null != afUserSealDo.getUserAccountId()) {
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());
        }
        if (null != lenderDo.getValue()) {
            AfUserSealDo companyUserSealDo = afUserSealDao.selectByUserName(lenderDo.getValue());
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("secondSeal", companyUserSealDo.getUserSeal());
                map.put("secondAccoundId", companyUserSealDo.getUserAccountId());
            }
        }
    }

    public static String ToBig(int num) {
        String str[] = {"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
        return str[num - 1];
    }

    public static String toCapital(double x) {
        DecimalFormat format = new DecimalFormat("#.00");
        String str = format.format(x);
        System.out.println(str);
        String s[] = str.split("\\.");
        String temp = "";
        int ling = 0;
        int shu = 0;
        int pos = 0;
        for (int j = 0; j < s[0].length(); ++j) {
            int num = s[0].charAt(j) - '0';
            if (num == 0) {
                ling++;
                if (ling == s[0].length()) {
                    temp = "零";
                } else if (s[0].length() - j - 1 == 4) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 5 && (s[0].length() - pos - 1) <= 7) {
                        temp += "万";
                    }
                } else if (s[0].length() - j - 1 == 8) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 9 && (s[0].length() - pos - 1) <= 11) {
                        temp += "亿";
                    }
                }
            } else {
                shu++;
                int flag = 0;
                if (shu == 1) {
                    ling = 0;
                    pos = j;
                }
                if (shu == 2) {
                    flag = 1;
                    if (ling > 0) {
                        temp += "零";
                    }
                    shu = 1;
                    pos = j;
                    ling = 0;
                }
                if (s[0].length() - j - 1 == 11) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 10) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 9) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 8) {
                    temp += ToBig(num) + "亿";
                } else if (s[0].length() - j - 1 == 7) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 6) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 5) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 4) {
                    temp += ToBig(num) + "万";
                } else if (s[0].length() - j - 1 == 3) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 2) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 1) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else {
                    temp += ToBig(num);
                }
            }
            // System.out.println(temp);
        }
        temp += "元";
        for (int j = 0; j < s[1].length(); ++j) {
            int num = s[1].charAt(j) - '0';
            if (j == 0) {
                if (num != 0)
                    temp += ToBig(num) + "角";
                else if (num == 0 && 1 < s[1].length() && s[1].charAt(1) != '0') {
                    temp += "零";
                }
            } else if (j == 1) {
                if (num != 0)
                    temp += ToBig(num) + "分";
            }
        }
        System.out.println(temp);
        return temp;
    }

}
