package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayInvestorInfoBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.dao.*;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("afLegalContractPdfCreateServiceV2")
public class AfLegalContractPdfCreateServiceV2Impl implements AfLegalContractPdfCreateServiceV2 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AfLegalContractPdfCreateServiceV2Impl.class);

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
    AfContractPdfEdspaySealDao afContractPdfEdspaySealDao;
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
    @Resource
    NumberWordFormat numberWordFormat;

    private static final String src = "/home/aladin/project/app_contract";

    private AfUserAccountDo getUserInfo(long userId, Map map,List<EdspayInvestorInfoBo> investorList) {
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
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        map.put("lender", lenderDo.getValue());// 出借人
        map.put("mobile", afUserDo.getMobile());// 联系电话
        GetSeal(map, afUserDo, accountDo,investorList);//获取印章
        return accountDo;
    }

    private AfUserAccountDo getUserInfoWithoutSeal(long userId, Map map) {
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
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        map.put("lender", lenderDo.getValue());// 出借人
        map.put("mobile", afUserDo.getMobile());// 联系电话
        return accountDo;
    }

    private void GetSeal(Map map, AfUserDo afUserDo, AfUserAccountDo accountDo,List<EdspayInvestorInfoBo> investorList) {
        try {
            AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
                map.put("companyUserSeal",companyUserSealDo.getUserSeal());
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
            AfUserDo investorUserDo = new AfUserDo();
            AfUserAccountDo investorAccountDo = new AfUserAccountDo();
            List<AfUserSealDo> userSealDoList = new ArrayList<>();
            List<AfContractPdfEdspaySealDo> edspaySealDoList = new ArrayList<>();
            if (investorList.size() <= 0){
                logger.error("创建出借人印章失败，出借人list为空 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }

            for (EdspayInvestorInfoBo infoBo : investorList){
                investorUserDo.setMobile(infoBo.getInvestorPhone());
                investorUserDo.setRealName(infoBo.getInvestorName());
                investorAccountDo.setIdNumber(infoBo.getInvestorCardId());
                investorUserDo.setMajiabaoName("edspay");
                AfUserSealDo investorAfUserSealDo = afESdkService.getSealPersonal(investorUserDo, investorAccountDo);
                if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
                    logger.error("创建e都市钱包用户印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                    throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                }
                userSealDoList.add(investorAfUserSealDo);//印章列表
                AfContractPdfEdspaySealDo afContractPdfEdspaySealDo = new AfContractPdfEdspaySealDo();
                afContractPdfEdspaySealDo.setInvestorAmount(infoBo.getAmount());
                afContractPdfEdspaySealDo.setUserSealId(investorAfUserSealDo.getId());
                edspaySealDoList.add(afContractPdfEdspaySealDo);//e都市钱包印章和协议关联表
            }
            map.put("userSealDoList",userSealDoList);
            map.put("edspaySealDoList",edspaySealDoList);

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
        if (afResourceDo != null && afResourceDo.getValue2() != null) {
            String oneDay = "";
            String twoDay = "";
            if(null != afResourceDo){
                oneDay = afResourceDo.getTypeDesc().split(",")[0];
                twoDay = afResourceDo.getTypeDesc().split(",")[1];
            }
            JSONArray array = new JSONArray();
            if ("instalment".equals(borrowType)) {
                array = JSONObject.parseArray(afResourceDo.getValue3());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String consumeTag = jsonObject.get("consumeTag").toString();
                    if ("INTEREST_RATE".equals(consumeTag)) {//借款利率
                        if ("SEVEN".equals(type)) {
                            map.put("yearRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("yearRate", jsonObject.get("consumeSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("yearRate",jsonObject.get("consumeFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("yearRate",jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                    if ("SERVICE_RATE".equals(consumeTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("consumeSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("poundageRate", jsonObject.get("consumeFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("poundageRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(consumeTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("consumeSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("overdueRate", jsonObject.get("consumeFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("overdueRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                }
            } else if ("borrow".equals(borrowType)) {
                array = JSONObject.parseArray(afResourceDo.getValue2());
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String borrowTag = jsonObject.get("borrowTag").toString();
                    if ("INTEREST_RATE".equals(borrowTag)) {//借款利率
                        if ("SEVEN".equals(type)) {
                            map.put("yearRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("yearRate", jsonObject.get("borrowSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("yearRate", jsonObject.get("borrowFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("yearRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("SERVICE_RATE".equals(borrowTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("borrowSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("poundageRate", jsonObject.get("borrowFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("poundageRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(borrowTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("borrowSecondType"));
                        }else if(numberWordFormat.isNumeric(type)){
                            if(oneDay.equals(type)){
                                map.put("overdueRate", jsonObject.get("borrowFirstType"));
                            }else if(twoDay.equals(type)){
                                map.put("overdueRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                }
            }

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
        try {
            Map map = new HashMap();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            AfUserAccountDo accountDo = getUserInfo(userId, map,null);
            /*AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
            if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
                logger.error("创建个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            }
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());*/
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
            AfBorrowCashDo afBorrowCashDo = null;
            if (borrowId > 0) {
                afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                if (afBorrowCashDo != null) {
                    getResourceRate(map, afBorrowCashDo.getType(),afResourceDo,"borrow");
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                    if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
//                        Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                        Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
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
    public void platformServiceProtocol(Long borrowId, String type, BigDecimal poundage, Long userId) {
        try {
            Map map = new HashMap();
            AfUserDo afUserDo = afUserService.getUserById(userId);
            if (afUserDo == null) {
                logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
            if (accountDo == null) {
                logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }

            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
            getResourceRate(map, type,afResourceDo,"borrow");
            map.put("email", afUserDo.getEmail());//电子邮箱
            map.put("mobile", afUserDo.getUserName());// 联系电话
            map.put("realName",accountDo.getRealName());
            map.put("poundage",poundage);//手续费
            if(borrowId > 0){
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                if(null != afBorrowCashDo){
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                }
                map.put("borrowId",borrowId);
                Calendar c = Calendar.getInstance();
                c.setTime(afBorrowCashDo.getGmtCreate());
                int month = c.get(Calendar.MONTH)+1;
                int day = c.get(Calendar.DATE);
                int year = c.get(Calendar.YEAR);
                String time = year + "年" + month + "月" + day + "日";
                map.put("time", time);// 签署日期
                secondSeal(map,null,afUserDo, accountDo);
            }
            String overdueRate = (String) map.get("overdueRate");
            map.put("overdueRate",BigDecimal.valueOf(Double.parseDouble(overdueRate)).divide(BigDecimal.valueOf(360)));
            long time = new Date().getTime();
//            map.put("templatePath",src+"renewal"+".pdf");
            map.put("protocolCashType","4");
            map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/2018-01-30/18/platform.pdf");
            map.put("PDFPath", src + accountDo.getUserName() + "platform" + time + 1 + ".pdf");
            map.put("userPath", src + accountDo.getUserName() + "platform" + time + 2 + ".pdf");
            map.put("selfPath", src + accountDo.getUserName() + "platform" + time + 3 + ".pdf");
            map.put("thirdPath", src + accountDo.getUserName() + "platform" + time + 4 + ".pdf");
            map.put("fileName", accountDo.getUserName() + "platform" + time + 4);
            map.put("signType","Key");
            map.put("secondPartyKey","first");
            map.put("firstPartyKey","second");
            map.put("sealWidth","60");
            map.put("posType","1");

            if (PdfCreateByStream(map))
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            logger.info(JSON.toJSONString(map));
        }catch (Exception e){
            logger.error("platformServiceProtocol error 平台服务协议生成失败 =>{}", e);
        }
    }

    @Override
    public String getProtocalLegalByType(Integer debtType, String orderNo,String protocolUrl,String borrowerName,List<EdspayInvestorInfoBo> investorList) throws IOException {
        Map map = new HashMap();
        map.put("personKey",borrowerName);//借款人印章定位关键字
        if (debtType == 0){//借款
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null){
                logger.error("借款信息不存在 => {}",orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            return getPdfInfo(protocolUrl, map,afBorrowCashDo.getUserId(),afBorrowCashDo.getRid(),"cashLoan","1",investorList);
        }else if (debtType == 1){//分期
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null){
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null){
                    logger.error("分期订单不存在 => {}",orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
                return getPdfInfo(protocolUrl, map,afBorrowLegalOrderCashDo.getUserId(),afBorrowLegalOrderCashDo.getRid(),"instalment","2",investorList);
            }
            return getPdfInfo(protocolUrl, map,afBorrowDo.getUserId(),afBorrowDo.getRid(),"instalment","2",investorList);
        }
        return null;
    }

    @Override
    public String getProtocalLegalByTypeWithoutSeal(Integer debtType, String orderNo) throws IOException {
        if (debtType == 0){//借款
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null){
                logger.error("借款信息不存在 => {}",orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            return getPdfInfoWithOutSeal(afBorrowCashDo.getUserId(),null,afBorrowCashDo,"cashLoan");
        }else if (debtType == 1){//分期
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null){
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null){
                    logger.error("分期订单不存在 => {}",orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
            }
            return getPdfInfoWithOutSeal(afBorrowDo.getUserId(),afBorrowDo,null,"instalment");
        }
        return null;
    }

    private String getPdfInfo(String protocolUrl, Map map, Long userId,Long id,String type,String protocolCashType,List<EdspayInvestorInfoBo> investorList) throws IOException {
        AfUserAccountDo accountDo = getUserInfo(userId,map,investorList);
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

    private boolean PdfCreateByStream(Map map) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        boolean result = true;
        byte[] stream;
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
            FileDigestSignResult fileDigestSignResult = afESdkService.firstPartySign(map);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("甲方盖章证书生成失败 => {}",fileDigestSignResult.getMsg());
                return result;
            }
            stream = fileDigestSignResult.getStream();
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("甲方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        }finally {
            if (!result){
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 =  new File(map.get("PDFPath").toString());
                file1.delete();
            }
        }
        if (null != map.get("companySelfSeal") && !"".equals(map.get("companySelfSeal"))){
            try {
                FileDigestSignResult fileDigestSignResult = afESdkService.secondPartySign(map,stream);//阿拉丁盖章
                if (fileDigestSignResult.getErrCode() != 0) {
                    result = false;
                    logger.error("丙方盖章证书生成失败 => {}",fileDigestSignResult.getMsg());
                    return result;
                }
                stream = fileDigestSignResult.getStream();
                map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
            } catch (Exception e) {
                logger.error("丙方盖章证书生成失败 => {}", e);
                result = false;
                return result;
            }finally {
                if (!result){
                    File file1 = new File(map.get("userPath").toString());
                    file1.delete();
                }
            }
        }
        if (null != map.get("thirdSeal") && !"".equals(map.get("thirdSeal"))){
            try {
                FileDigestSignResult fileDigestSignResult = afESdkService.thirdStreamSign(map,stream);//钱包盖章
                if (fileDigestSignResult.isErrShow()) {
                    result = false;
                    logger.error("e都市钱包盖章证书生成失败 => {}",fileDigestSignResult);
                    return result;
                }
                stream = fileDigestSignResult.getStream();
                map.put("esignIdFour", fileDigestSignResult.getSignServiceId());
                String dstFile = String.valueOf(map.get("thirdPath"));
                File file = new File(dstFile);
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(stream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                logger.error("e都市钱包盖章证书生成失败 => {}", e);
                return false ;
            }finally {
                if (!result){
                    File file1 = new File(map.get("thirdPath").toString());
                    file1.delete();
                    file1 = new File(map.get("userPath").toString());
                    file1.delete();
                }
            }
        }else {
            String dstFile = String.valueOf(map.get("thirdPath"));
            File file = new File(dstFile);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(stream);
            outputStream.flush();
            outputStream.close();
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
                } else if ("4".equals(protocolCashType)){
                    afContractPdfDo.setType((byte) 4);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                }
                afContractPdfDao.insert(afContractPdfDo);
                /*List<AfContractPdfEdspaySealDo> edspaySealDoList = (List<AfContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                for (AfContractPdfEdspaySealDo edspaySealDo:edspaySealDoList) {
                    edspaySealDo.setPdfId(afContractPdfDo.getId());
                }
                afContractPdfEdspaySealDao.batchInsert(edspaySealDoList);*/
                return true;
            }
        } catch (Exception e) {
            logger.error("证书上传oss失败 => {}", e.getMessage());
            return false;
        } finally {
            if (null != input) {
                input.close();
            }
            File file1 = new File(map.get("PDFPath").toString());
            file1.delete();
            file1 = new File(map.get("userPath").toString());
            file1.delete();
            file1 = new File(map.get("thirdPath").toString());
            file1.delete();
        }
        return true;
    }


    private String getLegalContractPdf(Map map) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        boolean result = true;
        byte[] stream;
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("甲方盖章证书生成失败 => {}",fileDigestSignResult.getMsg());
                return null;
            }
            stream = fileDigestSignResult.getStream();
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
            FileDigestSignResult fileDigestSignResult = afESdkService.selfStreamSign(map,stream);//阿拉丁盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("丙方盖章证书生成失败 => {}",fileDigestSignResult.getMsg());
                return null;
            }
            stream = fileDigestSignResult.getStream();
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
            List<AfUserSealDo> list  = (List<AfUserSealDo>) map.get("userSealDoList");
            FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
//            StringBuffer userSealIds = new StringBuffer();
            for (AfUserSealDo userSealDo: list) {
//                userSealIds.append(userSealDo.getId()+",");
                map.put("key",userSealDo.getUserName());
                map.put("secondSeal", userSealDo.getUserSeal());
                map.put("secondAccoundId", userSealDo.getUserAccountId());
//                map.put("edspayUserId", userSealDo.getId());
                fileDigestSignResult = afESdkService.secondStreamSign(map,stream);//出借人盖章
                if (fileDigestSignResult.getErrCode() != 0) {
                    result = false;
                    logger.error("乙方盖章证书生成失败 => {}",fileDigestSignResult.getMsg());
                    return null;
                }
                stream = fileDigestSignResult.getStream();
                map.put("secondStream",fileDigestSignResult.getStream());
                map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
            }
//            map.put("userSealIds",userSealIds);

            fileDigestSignResult.getStream();
//            FileDigestSignResult fileDigestSignResult = afESdkService.secondSign(map);//出借人盖章
            /*if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("乙方盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }*/

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
            FileDigestSignResult fileDigestSignResult = afESdkService.thirdStreamSign(map,stream);//钱包盖章
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("e都市钱包盖章证书生成失败 => {}",fileDigestSignResult);
                return null;
            }
            stream = fileDigestSignResult.getStream();
            map.put("esignIdFour", fileDigestSignResult.getSignServiceId());
            String dstFile = String.valueOf(map.get("thirdPath"));
            File file = new File(dstFile);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(stream);
            outputStream.flush();
            outputStream.close();
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
                /*if (map.get("userSealIds") != null){
                    afContractPdfDo.setUserSealId(String.valueOf(map.get("userSealIds")));
                }*/
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
                List<AfContractPdfEdspaySealDo> edspaySealDoList = (List<AfContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                for (AfContractPdfEdspaySealDo edspaySealDo:edspaySealDoList) {
                    edspaySealDo.setPdfId(afContractPdfDo.getId());
                }
                afContractPdfEdspaySealDao.batchInsert(edspaySealDoList);
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

    private String getPdfInfoWithOutSeal(Long userId,AfBorrowDo afBorrowDo,AfBorrowCashDo afBorrowCashDo,String type) throws IOException {
        long time = new Date().getTime();
        Map map = new HashMap();
        String url = "http://localhost:8080";
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        AfUserDo afUserDo = afUserService.getUserById(userId);
        if (afUserDo == null) {
            logger.error("user not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        if (type.equals("cashLoan")){
            url += ("/fanbei-web/app/protocolLegalCashLoanV2?");
            url += ("userName="+afUserDo.getUserName());
            url += ("&borrowId="+afBorrowCashDo.getRid());
            url += ("&amount="+afBorrowCashDo.getAmount());
            url += ("&poundage="+0);
        }else if (type.equals("instalment")){
            url += ("/fanbei-web/app/protocolLegalCashLoanV2?");
            url += ("userName="+afUserDo.getUserName());
            url += ("&borrowId="+afBorrowDo.getRid());
            url += ("&borrowAmount="+afBorrowDo.getAmount());
        }
        String html = HttpUtil.doGet(String.valueOf(url),10);
        PdfUtil.htmlContentWithCssToPdf(html,src + accountDo.getUserName() + type + time + 1 + ".pdf",null);
        return pdfCreateWithoutSeal(map);
    }

    private String pdfCreateWithoutSeal(Map map)throws IOException{
        /*OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        try {
            PdfCreateUtil.create(map, fos, bos);
        } catch (Exception e) {
            logger.error("pdf合同生成失败 => {}", e.getMessage());
            return null;
        } finally {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
            if (null != bos) {
                bos.close();
            }
        }*/
        InputStream input = null;
        try {
            File file = new File(map.get("PDFPath").toString());
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
                afContractPdfDo.setUserSealId(-1l);
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
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
            File file1 = new File(map.get("PDFPath").toString());
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
            map.put("secondAccoundId",companySealDo.getUserAccountId());
        }

        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null != afUserSealDo && null != afUserSealDo.getUserSeal() && null != afUserSealDo.getUserAccountId()) {
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());
        }
        if (null != lenderDo && null != lenderDo.getValue()) {
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
