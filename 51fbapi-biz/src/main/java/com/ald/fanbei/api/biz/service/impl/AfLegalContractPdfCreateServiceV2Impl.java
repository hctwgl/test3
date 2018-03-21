package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayInvestorInfoBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.*;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfContractPdfEdspaySealDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.DocumentException;
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
    @Resource
    AfLoanService afLoanService;
    @Resource
    AfUserOutDayDao afUserOutDayDao;
    @Resource
    AfBorrowService afBorrowService;

    private static final String src = "/home/aladin/project/app_contract";

    private AfUserAccountDo getUserInfo(long userId, Map<String, Object> map, List<EdspayInvestorInfoBo> investorList) {
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
        getSeal(map, afUserDo, accountDo, investorList);//获取印章
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

    private void getSeal(Map<String, Object> map, AfUserDo afUserDo, AfUserAccountDo accountDo, List<EdspayInvestorInfoBo> investorList) {
        try {
            AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("companyUserSeal", companyUserSealDo.getUserSeal());
            } else {
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
            if (investorList.size() <= 0) {
                logger.error("创建出借人印章失败，出借人list为空 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }

            for (EdspayInvestorInfoBo infoBo : investorList) {
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
            map.put("userSealDoList", userSealDoList);
            map.put("edspaySealDoList", edspaySealDoList);

            companyUserSealDo = afUserSealDao.selectByUserName("浙江楚橡信息科技有限公司");
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("thirdSeal", companyUserSealDo.getUserSeal());
                map.put("thirdAccoundId", companyUserSealDo.getUserAccountId());
            } else {
                logger.error("创建钱包印章失败 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
        } catch (Exception e) {
            logger.error("UserSeal create error", e);
        }
    }

    private void getResourceRate(Map map, String type, AfResourceDo afResourceDo, String borrowType) {
        if (afResourceDo != null && afResourceDo.getValue2() != null) {
            String oneDay = "";
            String twoDay = "";
            if (null != afResourceDo) {
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
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("yearRate", jsonObject.get("consumeFirstType"));
                            } else if (twoDay.equals(type)) {
                                map.put("yearRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                    if ("SERVICE_RATE".equals(consumeTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("consumeSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("poundageRate", jsonObject.get("consumeFirstType"));
                            } else if (twoDay.equals(type)) {
                                map.put("poundageRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(consumeTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("consumeFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("consumeSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("overdueRate", jsonObject.get("consumeFirstType"));
                            } else if (twoDay.equals(type)) {
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
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("yearRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
                                map.put("yearRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("SERVICE_RATE".equals(borrowTag)) {//手续费利率
                        if ("SEVEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("poundageRate", jsonObject.get("borrowSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("poundageRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
                                map.put("poundageRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                    if ("OVERDUE_RATE".equals(borrowTag)) {//逾期利率
                        if ("SEVEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("borrowFirstType"));
                        } else if ("FOURTEEN".equals(type)) {
                            map.put("overdueRate", jsonObject.get("borrowSecondType"));
                        } else if (numberWordFormat.isNumeric(type)) {
                            if (oneDay.equals(type)) {
                                map.put("overdueRate", jsonObject.get("borrowFirstType"));
                            } else if (twoDay.equals(type)) {
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
            AfUserAccountDo accountDo = getUserInfo(userId, map, null);
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
                    getResourceRate(map, afBorrowCashDo.getType(), afResourceDo, "borrow");
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                    if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
//                        Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                        Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
                        Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                        Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                        map.put("gmtBorrowBegin", dateFormat.format(arrivalStart));//到账时间，借款起息日
                        map.put("gmtBorrowEnd", dateFormat.format(repaymentDay));//借款结束日
                        map.put("amountCapital", "人民币" + toCapital(afBorrowCashDo.getAmount().doubleValue()));
                        map.put("amountLower", "￥" + afBorrowCashDo.getAmount());
                    }
                }

                if (renewalId > 0) {
                    AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalId);
                    Date gmtCreate = afRenewalDetailDo.getGmtCreate();
                    Date gmtPlanRepayment = afRenewalDetailDo.getGmtPlanRepayment();
                    if (afRenewalDetailDo != null) {
                        AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getLastOrderCashByBorrowId(afRenewalDetailDo.getBorrowId());
                        if (afBorrowLegalOrderCashDo != null) {
                            map.put("useType", afBorrowLegalOrderCashDo.getBorrowRemark());
                            map.put("poundageRate", afBorrowLegalOrderCashDo.getPoundageRate() + "%");//手续费率
                            map.put("yearRate", afBorrowLegalOrderCashDo.getInterestRate() + "%");//利率
                            map.put("overdueRate", "36%");
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
                    map.put("renewalAmountCapital", "人民币" + toCapital(afRenewalDetailDo.getRenewalAmount().doubleValue()));//续借金额大写
                    map.put("repayAmountLower", "￥" + afRenewalDetailDo.getCapital());//续借金额小写
                    map.put("repayAmountCapital", "人民币" + toCapital(afRenewalDetailDo.getCapital().doubleValue()));//续借金额大写
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
                    map.put("renewalAmountCapital", "人民币" + toCapital(renewalAmount.doubleValue()));//续借金额大写
//				AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
//				BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
                    String yearRate = afResourceDo.getValue();
                    if (yearRate != null && !"".equals(yearRate)) {
                        BigDecimal capital = afBorrowCashDo.getAmount().divide(BigDecimal.valueOf(100)).multiply(new BigDecimal(yearRate)).setScale(2, RoundingMode.HALF_UP);
                        map.put("repayAmountLower", "￥" + capital);//续借金额小写
                        map.put("repayAmountCapital", "人民币" + toCapital(capital.doubleValue()));//续借金额大写
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
            Map<String,Object> map = new HashMap();
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
            getResourceRate(map, type, afResourceDo, "borrow");
            map.put("email", afUserDo.getEmail());//电子邮箱
            map.put("mobile", afUserDo.getUserName());// 联系电话
            map.put("realName", accountDo.getRealName());
            if (borrowId > 0) {
                AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
                if (null != afBorrowCashDo) {
                    map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                }
                poundage =  afBorrowCashDo.getAmount().multiply(BigDecimal.valueOf(Double.parseDouble(map.get("poundageRate").toString()))).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(numberWordFormat.borrowTime(afBorrowCashDo.getType()))).divide(BigDecimal.valueOf(360),2,BigDecimal.ROUND_HALF_UP);
                map.put("poundage", poundage);//手续费
                map.put("borrowId", borrowId);
                Calendar c = Calendar.getInstance();
                c.setTime(afBorrowCashDo.getGmtCreate());
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DATE);
                int year = c.get(Calendar.YEAR);
                String time = year + "年" + month + "月" + day + "日";
                map.put("time", time);// 签署日期
                secondSeal(map, null, afUserDo, accountDo);
            }
            String overdueRate = (String) map.get("overdueRate");
            map.put("overdueRate", BigDecimal.valueOf(Double.parseDouble(overdueRate)).divide(BigDecimal.valueOf(360),2,BigDecimal.ROUND_HALF_UP));
            long time = new Date().getTime();
            map.put("protocolCashType", "4");
            map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/2018-03-14/13/platform.pdf");
            map.put("PDFPath", src + accountDo.getUserName() + "platform" + time + 1 + ".pdf");
            map.put("userPath", src + accountDo.getUserName() + "platform" + time + 2 + ".pdf");
            map.put("selfPath", src + accountDo.getUserName() + "platform" + time + 3 + ".pdf");
            map.put("thirdPath", src + accountDo.getUserName() + "platform" + time + 4 + ".pdf");
            map.put("fileName", accountDo.getUserName() + "platform" + time + 4);
            map.put("signType", "Key");
            map.put("secondPartyKey", "first");//阿拉丁签章关键字
            map.put("firstPartyKey", "second");//用户签章关键字
            map.put("thirdStreamKey","楚橡信息科技有限公司");
            map.put("sealWidth", "60");
            map.put("posType", "1");
            if (!PdfCreateByStream(map))
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            logger.info(JSON.toJSONString(map));
        } catch (Exception e) {
            logger.error("platformServiceProtocol error 平台服务协议生成失败 =>{}", e.getMessage());
        }
    }

    @Override
    public void goodsInstalmentProtocol(Long borrowId, String type, Long userId) {
        try {
            Map<String,Object> map = new HashMap();
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
            secondSeal(map, null, afUserDo, accountDo);
            getResourceRate(map, type, afResourceDo, "borrow");
            map.put("dayOverdueRate",BigDecimal.valueOf(Double.parseDouble(map.get("overdueRate").toString())).divide(BigDecimal.valueOf(360),2,BigDecimal.ROUND_HALF_UP));//每日逾期费
            map.put("idNumber", accountDo.getIdNumber());
            String poundageRate = String.valueOf(map.get("poundageRate"));
            String yearRate = String.valueOf(map.get("yearRate"));
            map.put("poundageRate", poundageRate + "%");
            map.put("yearRate", yearRate + "%");
            map.put("realName", accountDo.getRealName());
            map.put("email", afUserDo.getEmail());//电子邮箱
            map.put("mobile", afUserDo.getUserName());// 联系电话
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (afBorrowCashDo != null) {
                AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(borrowId);
                map.put("amountLower",borrowLegalOrderDo.getPriceAmount()+"元");
                map.put("amountCapital", "人民币" + toCapital(borrowLegalOrderDo.getPriceAmount().doubleValue()));
                map.put("signTime", dateFormat.format(afBorrowCashDo.getGmtCreate()));// 出借时间
                map.put("borrowNo", afBorrowCashDo.getBorrowNo());
                map.put("borrowId", borrowLegalOrderDo.getRid());
                if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                    Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
                    Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                    Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                    map.put("gmtTime", dateFormat.format(afBorrowCashDo.getGmtArrival())+"至"+dateFormat.format(repaymentDay));//到账时间
                    map.put("gmtPlanRepayment", dateFormat.format(afBorrowCashDo.getGmtPlanRepayment()));//还款日
                    AfUserSealDo companyUserSealDo = afUserSealDao.selectByUserName("金泰嘉鼎（深圳）资产管理有限公司");
                    map.put("thirdSeal",companyUserSealDo.getUserSeal());
                    map.put("thirdAccoundId",companyUserSealDo.getUserAccountId());
                }
                long time = new Date().getTime();
                map.put("protocolCashType", "7");
                map.put("templatePath", "http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/2018-03-14/13/goodsInstalment1.pdf");
                map.put("PDFPath", src + accountDo.getUserName() + "goodsInstalment" + time + 1 + ".pdf");
                map.put("userPath", src + accountDo.getUserName() + "goodsInstalment" + time + 2 + ".pdf");
                map.put("thirdPath", src + accountDo.getUserName() + "goodsInstalment" + time + 4 + ".pdf");
                map.put("fileName", accountDo.getUserName() + "goodsInstalment" + time + 4);
                map.put("signType", "Key");
                map.put("firstPartyKey", "borrower");//用户签章关键字
                map.put("secondPartyKey", "ald");//阿拉丁签章关键字
                map.put("thirdPartyKey", "lender");//出借人签章关键字
                map.put("sealWidth", "60");
                map.put("posType", "1");
            }
            if (!PdfCreateByStream(map))
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
        }catch (Exception e){
            logger.error("goodsInstalmentProtocol error 借款分期协议生成失败 =>{}", e.getMessage());
        }

    }

    @Override
    public String getProtocalLegalByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException {
        Map<String, Object> map = new HashMap();
        map.put("personKey", borrowerName);//借款人印章定位关键字
        if (debtType == 0) {//借款
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null) {
                logger.error("借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 1);
            afContractPdfDo.setTypeId(afBorrowCashDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfo(protocolUrl, map, afBorrowCashDo.getUserId(), afBorrowCashDo.getRid(), "cashLoan", "1", investorList);
        } else if (debtType == 1) {//分期
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null) {
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null) {
                    logger.error("分期订单不存在 => {}", orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
                return getPdfInfo(protocolUrl, map, afBorrowLegalOrderCashDo.getUserId(), afBorrowLegalOrderCashDo.getRid(), "instalment", "2", investorList);
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 2);
            afContractPdfDo.setTypeId(afBorrowDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfo(protocolUrl, map, afBorrowDo.getUserId(), afBorrowDo.getRid(), "instalment", "2", investorList);
        } else if (debtType == 5){//白领贷借款
            AfLoanDo loanDo = afLoanService.getByLoanNo(orderNo);
            if (loanDo == null) {
                logger.error("白领贷借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 5);
            afContractPdfDo.setTypeId(loanDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfo(protocolUrl, map, loanDo.getUserId(), loanDo.getRid(), "whiteCashloan", "5", investorList);
        }
        return null;
    }

    @Override
    public String getProtocalLegalWithOutLenderByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException {
        Map<String, Object> map = new HashMap();
        map.put("personKey", borrowerName);//借款人印章定位关键字
        if (debtType == 0) {//借款
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null) {
                logger.error("借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 1);
            afContractPdfDo.setTypeId(afBorrowCashDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfoWithOutLender(protocolUrl, map, afBorrowCashDo.getUserId(), afBorrowCashDo.getRid(), "cashLoan", "1", investorList);
        } else if (debtType == 1) {//分期
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null) {
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null) {
                    logger.error("分期订单不存在 => {}", orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
                return getPdfInfoWithOutLender(protocolUrl, map, afBorrowLegalOrderCashDo.getUserId(), afBorrowLegalOrderCashDo.getRid(), "instalment", "2", investorList);
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 2);
            afContractPdfDo.setTypeId(afBorrowDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfoWithOutLender(protocolUrl, map, afBorrowDo.getUserId(), afBorrowDo.getRid(), "instalment", "2", investorList);
        } else if (debtType == 5){//白领贷借款
            AfLoanDo loanDo = afLoanService.getByLoanNo(orderNo);
            if (loanDo == null) {
                logger.error("白领贷借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
            afContractPdfDo.setType((byte) 5);
            afContractPdfDo.setTypeId(loanDo.getRid());
            AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfoWithOutLender(protocolUrl, map, loanDo.getUserId(), loanDo.getRid(), "whiteCashloan", "5", investorList);
        }
        return null;
    }

    @Override
    public String getProtocalLegalByTypeWithoutSeal(Integer debtType, String orderNo) throws IOException {
        if (debtType == 0) {//借款
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(orderNo);
            if (afBorrowCashDo == null) {
                logger.error("借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            return getPdfInfoWithOutSeal(afBorrowCashDo.getUserId(), null, afBorrowCashDo, "cashLoan");
        } else if (debtType == 1) {//分期
            AfBorrowDo afBorrowDo = afBorrowDao.getBorrowInfoByBorrowNo(orderNo);
            if (afBorrowDo == null) {
                AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByCashNo(orderNo);
                if (afBorrowLegalOrderCashDo == null) {
                    logger.error("分期订单不存在 => {}", orderNo);
                    throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
                }
            }
            return getPdfInfoWithOutSeal(afBorrowDo.getUserId(), afBorrowDo, null, "instalment");
        }
        return null;
    }

    private String getPdfInfo(String protocolUrl, Map<String, Object> map, Long userId, Long id, String type, String protocolCashType, List<EdspayInvestorInfoBo> investorList) throws IOException {
        AfUserAccountDo accountDo = getUserInfo(userId, map, investorList);
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

    private String getPdfInfoWithOutLender(String protocolUrl, Map<String, Object> map, Long userId, Long id, String type, String protocolCashType, List<EdspayInvestorInfoBo> investorList) throws IOException {
        AfUserAccountDo accountDo = getUserInfo(userId, map, investorList);
        long time = new Date().getTime();
        map.put("PDFPath", protocolUrl);
        map.put("borrowId", id);
        map.put("protocolCashType", protocolCashType);
        map.put("userPath", src + accountDo.getUserName() + type + time + 1 + ".pdf");
        map.put("selfPath", src + accountDo.getUserName() + type + time + 2 + ".pdf");
        map.put("secondPath", src + accountDo.getUserName() + type + time + 3 + ".pdf");
        map.put("thirdPath", src + accountDo.getUserName() + type + time + 4 + ".pdf");
        return getLegalContractPdfWithOutLender(map);
    }

    private void createPdf(Map<String, Object> map,boolean result) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        try {
            PdfCreateUtil.create(map, fos, bos);
        } catch (Exception e) {
            logger.error("PdfCreateByStream pdf合同生成失败 => {}", e.getMessage());
            result = false;
        } finally {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
            if (null != bos) {
                bos.close();
            }
            if (!result) {
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
            }
        }
    }

    public byte[] firstPartySign(Map<String, Object> map,boolean result){
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.firstPartySign(map);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("PdfCreateByStream 甲方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("firstPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("PdfCreateByStream 甲方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("firstPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("PDFPath").toString());
                file1.delete();
            }
        }
        return null;
    }

    public byte[] secondPartySign(Map<String, Object> map,boolean result,byte[] stream){
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.secondPartySign(map, stream);//阿拉丁盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("PdfCreateByStream 丙方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("secondPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("PdfCreateByStream 丙方盖章证书生成失败 => {}", e.getMessage() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("secondPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
        return null;
    }

    public byte[] thirdPartySign(Map<String, Object> map,boolean result,byte[] stream){
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.thirdStreamSign(map, stream);//钱包盖章
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("PdfCreateByStream e都市钱包盖章证书生成失败 => {}", fileDigestSignResult + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            }
            map.put("esignIdFour", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("PdfCreateByStream e都市钱包盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
        } finally {
            if (!result) {
                File file1 = new File(map.get("thirdPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
        return null;
    }

    private boolean PdfCreateByStream(Map<String, Object> map) throws IOException {
        boolean result = true;
        byte[] stream = new byte[1024];

        createPdf(map,result);

        stream = firstPartySign(map,result);//借款人签章

        if (null != map.get("companySelfSeal") && !"".equals(map.get("companySelfSeal"))) {//ald签章
            stream = secondPartySign(map,result,stream);
        }
        if (null != map.get("thirdSeal") && !"".equals(map.get("thirdSeal"))) {//出借人签章
            stream = thirdPartySign(map,result,stream);
        }
        String dstFile = String.valueOf(map.get("thirdPath"));
        File finalFile = new File(dstFile);
        FileOutputStream outputStream = new FileOutputStream(finalFile);
        outputStream.write(stream);
        outputStream.flush();
        outputStream.close();
        return ossFileUpload(map,dstFile);//oss上传
    }

    private boolean ossFileUpload(Map<String, Object> map,String dstFile) throws IOException {
        InputStream input = null;
        try {
            File file = new File(dstFile);
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
                afContractPdfDo.setType(Byte.valueOf(protocolCashType));
                afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                afContractPdfDao.insert(afContractPdfDo);
                return true;
            }
        } catch (Exception e) {
            logger.error("PdfCreateByStream 证书上传oss失败 => {}", e.getMessage());
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
        return false;
    }

    private byte[] borrowerCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("getLegalContractPdf 甲方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("personKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
                return null;
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("getLegalContractPdf 甲方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("personKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
            return null;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
    }

    private byte[] aldCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.selfStreamSign(map, stream);//阿拉丁盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("getLegalContractPdf 丙方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
                return null;
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("getLegalContractPdf 丙方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
            return null;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
    }

    private byte[] edsPayLenderCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            List<AfUserSealDo> list = (List<AfUserSealDo>) map.get("userSealDoList");
            FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
            for (AfUserSealDo userSealDo : list) {
                map.put("key", userSealDo.getUserName());
                map.put("secondSeal", userSealDo.getUserSeal());
                map.put("secondAccoundId", userSealDo.getUserAccountId());
                fileDigestSignResult = afESdkService.secondStreamSign(map, stream);//出借人盖章
                if (fileDigestSignResult.getErrCode() != 0) {
                    result = false;
                    logger.error("getLegalContractPdf 乙方盖章证书生成失败 => {}",fileDigestSignResult.getMsg()+",key="+userSealDo.getUserName()+",PDFPath =" + map.get("PDFPath") + ",borrowId = "+ map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
                    return null;
                }
                map.put("secondStream", fileDigestSignResult.getStream());
                map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
            }
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("getLegalContractPdf 乙方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
            return null;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
    }

    private byte[] lenderCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            List<AfUserSealDo> list = (List<AfUserSealDo>) map.get("userSealDoList");
            FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
//            map.put("key", userSealDo.getUserName());
//            map.put("secondSeal", userSealDo.getUserSeal());
//            map.put("secondAccoundId", userSealDo.getUserAccountId());
            fileDigestSignResult = afESdkService.secondStreamSign(map, stream);//出借人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("getLegalContractPdf 乙方盖章证书生成失败 => {}",fileDigestSignResult.getMsg()+",key="+map.get("key")+",PDFPath =" + map.get("PDFPath") + ",borrowId = "+ map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
                return null;
            }
            stream = fileDigestSignResult.getStream();
            map.put("secondStream", fileDigestSignResult.getStream());
            map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
            return stream;
        } catch (Exception e) {
            logger.error("getLegalContractPdf 乙方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
            return null;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
    }

    private byte[] edsPayCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            map.put("thirdPartyKey","楚橡信息科技有限公司");
            FileDigestSignResult fileDigestSignResult = afESdkService.thirdStreamSign(map, stream);//钱包盖章
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("getLegalContractPdf e都市钱包盖章证书生成失败 => {}", fileDigestSignResult + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            }
            map.put("esignIdFour", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();

        } catch (Exception e) {
            logger.error("getLegalContractPdf e都市钱包盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
        } finally {
            if (!result) {
                File file1 = new File(map.get("userPath").toString());
                file1.delete();
            }
        }
        return null;
    }

    private String ossFileUploadWithEdspaySeal(Map<String, Object> map,String fileName) throws IOException {
        InputStream input = null;
        try {
            File file = new File(fileName);
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
                afContractPdfDo.setType(Byte.valueOf(protocolCashType));
                afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                afContractPdfDo.setTypeId((Long) map.get("borrowId"));
                AfContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
                if (pdf != null) {
                    List<AfContractPdfEdspaySealDto> seal = afContractPdfEdspaySealDao.getByPDFId(pdf.getId());
                    if (seal == null || seal.size() == 0) {
                        List<AfContractPdfEdspaySealDo> edspaySealDoList = (List<AfContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                        for (AfContractPdfEdspaySealDo edspaySealDo : edspaySealDoList) {
                            edspaySealDo.setPdfId(afContractPdfDo.getId());
                        }
                        afContractPdfEdspaySealDao.batchInsert(edspaySealDoList);
                    }
                } else {
                    afContractPdfDao.insert(afContractPdfDo);
                    List<AfContractPdfEdspaySealDo> edspaySealDoList = (List<AfContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                    for (AfContractPdfEdspaySealDo edspaySealDo : edspaySealDoList) {
                        edspaySealDo.setPdfId(afContractPdfDo.getId());
                    }
                    afContractPdfEdspaySealDao.batchInsert(edspaySealDoList);
                }
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
            file1 = new File(map.get("thirdPath").toString());
            file1.delete();
        }
        return null;
    }

    private String getLegalContractPdf(Map<String, Object> map) throws IOException {
        boolean result = true;
        byte[] stream = new byte[1024];
        stream = borrowerCreateSeal(result,stream,map);//借款人签章

        stream = aldCreateSeal(result,stream,map);//阿拉丁签章

        stream = edsPayLenderCreateSeal(result,stream,map);//出借人签章

        stream = edsPayCreateSeal(result,stream,map);//钱包签章
        String dstFile = String.valueOf(map.get("thirdPath"));
        File file = new File(dstFile);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(stream);
        outputStream.flush();
        outputStream.close();
        return ossFileUploadWithEdspaySeal(map,dstFile);//oss上传

    }

    private String getLegalContractPdfWithOutLender(Map<String, Object> map) throws IOException {
        boolean result = true;
        byte[] stream = new byte[1024];
        stream = borrowerCreateSeal(result,stream,map);//借款人签章

        stream = aldCreateSeal(result,stream,map);//阿拉丁签章

        String dstFile = String.valueOf(map.get("selfPath"));
        File file = new File(dstFile);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(stream);
        outputStream.flush();
        outputStream.close();
        return ossFileUploadWithEdspaySeal(map,map.get("selfPath").toString());//oss上传
    }

    private String getPdfInfoWithOutSeal(Long userId, AfBorrowDo afBorrowDo, AfBorrowCashDo afBorrowCashDo, String type) throws IOException {
        long time = new Date().getTime();
        Map<String, String> map = new HashMap();
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
        String html = "";
        if (type.equals("cashLoan")){
            html = getCashVelocityHtml(afUserDo.getUserName(),afBorrowCashDo.getRid(),afBorrowCashDo.getAmount(), BigDecimal.valueOf(0),afBorrowCashDo.getType(),"protocolLegalCashLoanV2WithoutSealTemplate.vm");
            map.put("protocolCashType","1");
        }else if (type.equals("instalment")){
            html = getInstalmentVelocityHtml(afUserDo.getUserName(),afBorrowDo.getRid(),afBorrowDo.getNper(),afBorrowDo.getAmount(),BigDecimal.ZERO,"protocolLegalInstalmentV2WithoutSealTemplate.vm");
            map.put("protocolCashType","2");
        }
        /*if (type.equals("instalment")) {
            url += ("/fanbei-web/app/protocolLegalInstalmentV2WithoutSeal?");
            url += ("userName=" + afUserDo.getUserName());
            url += ("&borrowId=" + afBorrowDo.getRid());
            url += ("&amount=" + afBorrowDo.getAmount());
            url += ("&poundage=" + 0);
            map.put("protocolCashType", "2");
            map.put("borrowId", afBorrowDo.getRid().toString());
        } else if (type.equals("cashLoan")) {
            url += ("/fanbei-web/app/protocolLegalCashLoanV2WithoutSeal?");
            url += ("userName=" + afUserDo.getUserName());
            url += ("&borrowId=" + afBorrowCashDo.getRid());
            url += ("&borrowAmount=" + afBorrowCashDo.getAmount());
            url += ("&type=" + afBorrowCashDo.getType());
            map.put("protocolCashType", "1");
            map.put("borrowId", afBorrowCashDo.getRid().toString());
        }
        html = HttpUtil.doGet(String.valueOf(url), 10);*/
        String outFilePath = src + accountDo.getUserName() + type + time + 1 + ".pdf";
        HtmlToPdfUtil.htmlContentWithCssToPdf(html, outFilePath, null);
        map.put("PDFPath", outFilePath);

        return pdfCreateWithoutSeal(map);
    }

    private String getCashVelocityHtml(String userName, Long borrowId, BigDecimal amount, BigDecimal poundage, String type,String pdfType) {
        try {
            String html = VelocityUtil.getHtml(protocolLegalCashLoan(userName, borrowId, amount, poundage, type,pdfType));
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("getCashVelocityHtml IOException",e);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("getCashVelocityHtml DocumentException",e);
        }
        return null;
    }

    private String getInstalmentVelocityHtml(String userName,Long borrowId,Integer nper,BigDecimal borrowAmount,BigDecimal poundage,String pdfTemplate) {
        try {
            String html = VelocityUtil.getHtml(protocolLegalInstalmentV2WithoutSeal(userName,borrowId,nper,borrowAmount,poundage,pdfTemplate));
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("getCashVelocityHtml IOException",e);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("getCashVelocityHtml DocumentException",e);
        }
        return null;
    }

    public Map protocolLegalInstalmentV2WithoutSeal(String userName,Long borrowId,Integer nper,BigDecimal borrowAmount,BigDecimal poundage,String pdfTemplate){
        AfResourceDo consumeDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsume.getCode());
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        Map<String, Object> map = new HashMap();
        map.put("templateSrc",pdfTemplate);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
        AfResourceDo consumeOverdueDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsumeOverdue.getCode());
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
        map.put("lender", lenderDo.getValue());// 出借人
        map.put("mobile", afUserDo.getUserName());// 联系电话
        map.put("lateFeeRate", consumeOverdueDo.getValue1());
        if (StringUtils.isNotBlank(consumeOverdueDo.getValue2())) {
            String[] amounts = consumeOverdueDo.getValue2().split(",");
            map.put("lateFeeMin", new BigDecimal(amounts[0]));
            map.put("lateFeeMax", new BigDecimal(amounts[1]));
        }
        List<NperDo> rateList = JSONArray.parseArray(consumeDo.getValue3(), NperDo.class);
        for (NperDo nperDo : rateList) {
            if (nperDo.getNper() == nper) {
                map.put("interest", (BigDecimal)nperDo.getRate().multiply(BigDecimal.valueOf(100))+"%");
            }
        }

        if (null != borrowId && 0 != borrowId) {
            AfBorrowDo afBorrowDo = afBorrowService.getBorrowById(borrowId);
            lender(map);
            Date date = afBorrowDo.getGmtCreate();
            BigDecimal nperAmount = afBorrowDo.getNperAmount();
            map.put("nperAmount", nperAmount);
            map.put("gmtStart", date);
            map.put("gmtEnd", DateUtil.addMonths(date, nper));
            map.put("borrowNo", afBorrowDo.getBorrowNo());
            nper = afBorrowDo.getNper();
            String borrowRate = afBorrowDo.getBorrowRate();
            String interest = borrowRate.split(";")[1].split(",")[1];
            if (interest != null){
                BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(interest)).multiply(BigDecimal.valueOf(100));
                map.put("interest", rate.toString()+"%");
            }
            List repayPlan = new ArrayList();
            if (nper != null) {
                List<AfBorrowBillDo> afBorrowBillDos = afBorrowBillService.getAllBorrowBillByBorrowId(borrowId);
                int num = 1;
                for (AfBorrowBillDo bill:afBorrowBillDos) {
                    AfBorrowDo borrowDo = new AfBorrowDo();
                    borrowDo.setGmtCreate(bill.getGmtPayTime());
                    borrowDo.setNperAmount(bill.getInterestAmount());
                    borrowDo.setAmount(bill.getPrincipleAmount());
                    borrowDo.setNper(num);
                    repayPlan.add(borrowDo);
                    num++;
                }
                Date repayDay = null;
                if (afBorrowBillDos.size() > 0){
                    AfBorrowBillDo billDo = afBorrowBillDos.get(afBorrowBillDos.size()-1);
                    AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(userId);
                    if(afUserOutDayDo !=null) {
                        repayDay = billDo.getGmtPayTime();
                    }
                }
                map.put("repayDay", repayDay);
                map.put("repayPlan", repayPlan);
            }
        }

        map.put("amountCapital", toCapital(borrowAmount.doubleValue()));
        map.put("amountLower", borrowAmount);
        map.put("poundage", poundage);
        return map;
    }

    private void lender(Map model) {
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        model.put("lender", lenderDo.getValue());// 出借人
    }

    public Map protocolLegalCashLoan(String userName, Long borrowId, BigDecimal borrowAmount, BigDecimal poundage, String type,String pdfTemplate) throws IOException {
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Map<String, Object> map = new HashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        map.put("templateSrc",pdfTemplate);
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
        getResourceRate(map, type, afResourceDo, "borrow");
        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
        map.put("email", afUserDo.getEmail());//电子邮箱
        map.put("mobile", afUserDo.getUserName());// 联系电话
        List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
        Map<String, Object> rate = getObjectWithResourceDolist(list, borrowId);
        AfBorrowCashDo afBorrowCashDo = null;

        map.put("amountCapital", toCapital(borrowAmount.doubleValue()));
        map.put("amountLower", borrowAmount);
        getResourceRate(map, type, afResourceDo, "borrow");
        if (borrowId > 0) {
            afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            if (afBorrowCashDo != null) {
                getResourceRate(map, type, afResourceDo, "borrow");
                map.put("gmtCreate", format.format(afBorrowCashDo.getGmtCreate()));// 出借时间
                map.put("borrowNo", afBorrowCashDo.getBorrowNo());
                if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                    map.put("gmtArrival", format.format(afBorrowCashDo.getGmtArrival()));
//					Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                    Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
                    Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                    Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                    map.put("repaymentDay", format.format(repaymentDay));
                    map.put("lenderIdNumber", rate.get("lenderIdNumber"));
                    map.put("lenderIdAmount", afBorrowCashDo.getAmount());
                    map.put("gmtPlanRepayment", format.format(afBorrowCashDo.getGmtPlanRepayment()));
                    //查看有无和资金方关联，有的话，替换里面的借出人信息
                    AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
                    map.put("lender", lenderDo.getValue());// 出借人
                }
            }
        }
        logger.info(JSON.toJSONString(map));
        return map;
    }

    private String pdfCreateWithoutSeal(Map map) throws IOException {
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
            if (!result) {
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);
            if (fileDigestSignResult.isErrShow()) {
                result = false;
                logger.error("pdfCreate 甲方盖章证书生成失败 => {}", fileDigestSignResult);
                return result;
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("pdfCreate 甲方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        } finally {
            if (!result) {
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
                logger.error("pdfCreate 丙方盖章证书生成失败 => {}", fileDigestSignResult);
                return result;
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("pdfCreate 丙方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        } finally {
            if (!result) {
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
                logger.error("pdfCreate 乙方盖章证书生成失败 => {}", fileDigestSignResult);
                return result;
            }
            map.put("esignIdThird", fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("pdfCreate 乙方盖章证书生成失败 => {}", e);
            result = false;
            return result;
        } finally {
            if (!result) {
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
            logger.error("pdfCreate 证书上传oss失败 => {}", e.getMessage());
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
        if (null != companySealDo && null != companySealDo.getUserSeal()) {//ald签章
            map.put("companySelfSeal", companySealDo.getUserSeal());
            map.put("secondAccoundId", companySealDo.getUserAccountId());
        }

        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);//借款人签章
        if (null != afUserSealDo && null != afUserSealDo.getUserSeal() && null != afUserSealDo.getUserAccountId()) {
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());
        }
        if (null != lenderDo ) {//出借人签章
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
