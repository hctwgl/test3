package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.biz.util.PdfCreateUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("afContractPdfCreateService")
public class AfContractPdfCreateServiceImpl implements AfContractPdfCreateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AfContractPdfCreateServiceImpl.class);

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

    private static final String src = "F:/doc/";
    @Override
    public void protocolInstalment(String userName,Integer nper,BigDecimal amount,BigDecimal poundage ,Long borrowId,BigDecimal borrowAmount) {//分期

//        String userName = ObjectUtils.toString(content.get("userName"), "").toString();
//        Integer nper = NumberUtil.objToIntDefault(content.get("nper"), 0);
//        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(content.get("amount"), new BigDecimal(0));
//        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(content.get("poundage"), new BigDecimal(0));
//        Long borrowId = NumberUtil.objToLongDefault(content.get("borrowId"),0);
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map map = new HashMap();
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
        AfResourceDo consumeDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsume.getCode());
        AfResourceDo consumeOverdueDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsumeOverdue.getCode());
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
        }
        map.put("protocolCashType","2");
        map.put("borrowId",borrowId);
        map.put("personUserSeal", afUserSealDo.getUserSeal());
        map.put("accountId", afUserSealDo.getUserAccountId());
        map.put("lender", lenderDo.getValue());// 出借人
        secondSeal(map, lenderDo,afUserDo, accountDo);
        if (null == map.get("companySelfSeal") ) {
            logger.error("公司印章不存在" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
        }
        if (null == map.get("personUserSeal")){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
        }
        map.put("mobile", afUserDo.getMobile());// 联系电话
        List<NperDo> list = JSONArray.parseArray(consumeDo.getValue(), NperDo.class);
        List<NperDo> overduelist = JSONArray.parseArray(consumeOverdueDo.getValue(), NperDo.class);
        map.put("lateFeeRate", consumeOverdueDo.getValue1());
        if (StringUtils.isNotBlank(consumeOverdueDo.getValue2())) {
            String[] amounts = consumeOverdueDo.getValue2().split(",");
            map.put("lateFeeMin", new BigDecimal(amounts[0]));
            map.put("lateFeeMax", new BigDecimal(amounts[1]));
        }
        map.put("amountCapital", "人民币"+toCapital(borrowAmount.doubleValue()));
        map.put("amountLower", "￥"+borrowAmount);
        map.put("poundage", "￥"+poundage);

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        map.put("gmtTime", simpleDateFormat.format(date)+"至"+simpleDateFormat.format(DateUtil.addMonths(date, nper)));

        for (NperDo nperDo : list) {
            if (nperDo.getNper() == nper) {
                map.put("yearRate", nperDo.getRate());
            }
        }
        for (NperDo nperDo : overduelist) {
            if (nperDo.getNper() == nper) {
                map.put("overdueRate", nperDo.getRate() != null?nperDo.getRate():"");
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("templatePath",src+"分期服务协议"+".pdf");
        map.put("PDFPath",src+accountDo.getRealName()+"分期服务协议"+new Date().getTime()+".pdf");
        map.put("userPath",src+accountDo.getRealName()+"分期服务协议"+new Date().getTime()+".pdf");
        map.put("selfPath",src+accountDo.getRealName()+"分期服务协议"+new Date().getTime()+".pdf");
        map.put("secondPath",src+accountDo.getRealName()+"分期服务协议"+new Date().getTime()+".pdf");
        if (pdfCreate(map))
            throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
        logger.info(JSON.toJSONString(map));
    }

    @Override
    public void protocolCashLoan(Long borrowId,BigDecimal borrowAmount,String userName ) {//借款
//        Long borrowId = NumberUtil.objToLongDefault(content.get("borrowId"), 0l);
//        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(content.get("borrowAmount"), new BigDecimal(0));
//        String userName = ObjectUtils.toString(content.get("userName"), "").toString();
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map map = new HashMap();
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        map.put("protocolCashType","1");
        map.put("borrowId",borrowId);
        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
        map.put("email", afUserDo.getEmail());//电子邮箱
        map.put("phone", userName);//联系方式
        List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
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

        map.put("dayRate", bankService.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//日利率
        map.put("overdueRate", overdue.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//逾期费率（日）
        map.put("poundageRate", poundage.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//手续费率
        map.put("overduePoundageRate", overduePoundage.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//逾期手续费率
        map.put("purpose", "个人消费");
        map.put("repayWay", "到期一次性还本付息");
        map.put("amountCapital", "人民币"+toCapital(borrowAmount.doubleValue()));
        map.put("amountLower", "￥"+borrowAmount);
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
                    AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
                    map.put("gmtTime", simpleDateFormat.format(afBorrowCashDo.getGmtArrival())+"至"+simpleDateFormat.format(repaymentDay));
//                    map.put("repaymentDay", repaymentDay);
                    map.put("lender", lenderDo.getValue());// 出借人
                    secondSeal(map, lenderDo,afUserDo, accountDo);
                    if (null == map.get("companySelfSeal") ) {
                        logger.error("公司印章不存在" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                        throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
                    }
                    if (null == map.get("personUserSeal")){
                        logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                        throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                    }
                    map.put("lenderIdNumber", rate.get("lenderIdNumber"));
                    map.put("lenderIdAmount", afBorrowCashDo.getAmount());
                    map.put("gmtPlanRepayment", simpleDateFormat.format(afBorrowCashDo.getGmtPlanRepayment()));
                }
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("templatePath",src+"个人现金服务合同"+".pdf");
        map.put("PDFPath",src+accountDo.getRealName()+"个人现金服务合同"+new Date().getTime()+".pdf");
        map.put("userPath",src+accountDo.getRealName()+"个人现金服务合同"+new Date().getTime()+".pdf");
        map.put("selfPath",src+accountDo.getRealName()+"个人现金服务合同"+new Date().getTime()+".pdf");
        map.put("secondPath",src+accountDo.getRealName()+"个人现金服务合同"+new Date().getTime()+".pdf");
        if (pdfCreate(map))
            throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
        logger.info(JSON.toJSONString(map));
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
    public void protocolRenewal(String userName,Long borrowId,Long renewalId,int renewalDay ,BigDecimal renewalAmount) {//续借
//        String userName = ObjectUtils.toString(content.get("userName"), "").toString();
//        Long borrowId = NumberUtil.objToLongDefault(content.get("borrowId"), 0l);
//        Long renewalId = NumberUtil.objToLongDefault(content.get("renewalId"), 0l);
//        int renewalDay = NumberUtil.objToIntDefault(content.get("renewalDay"), 0);
//        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(content.get("renewalAmount"), BigDecimal.ZERO);

        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Map map = new HashMap();
        map.put("realName", accountDo.getRealName());//借款人
        map.put("idNumber", accountDo.getIdNumber());//身份证号
        map.put("phone", userName);//联系方式
        map.put("email", afUserDo.getEmail());//电子邮箱
        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
            throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
        }
        map.put("personUserSeal", afUserSealDo.getUserSeal());
        map.put("accountId", afUserSealDo.getUserAccountId());
        List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
        Map<String, Object> rate = getObjectWithResourceDolist(list, borrowId);
        BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
        BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
        BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);
        map.put("dayRate", bankService.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");

        BigDecimal poundage = new BigDecimal(rate.get("poundage").toString());
        Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
        if (poundageRateCash != null) {
            poundage = new BigDecimal(poundageRateCash.toString());
        }
        map.put("protocolCashType","3");
        map.put("renewalId",renewalId);
        BigDecimal overduePoundage = new BigDecimal(rate.get("overduePoundage").toString());
        map.put("poundageRate", poundage.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//手续费率
        map.put("overduePoundageRate", overduePoundage.multiply(new BigDecimal(100)).setScale(4,BigDecimal.ROUND_HALF_UP)+"%");//逾期手续费率
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        if (borrowId > 0) {
            AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
            if (afBorrowCashDo != null) {
                map.put("borrowNo", afBorrowCashDo.getBorrowNo());//原始借款协议编号
                if (StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode()) || StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
                    Integer day = NumberUtil.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                    Date arrivalStart = DateUtil.getStartOfDate(afBorrowCashDo.getGmtArrival());
                    Date repaymentDay = DateUtil.addDays(arrivalStart, day - 1);
                    map.put("gmtBorrowBegin", arrivalStart);//到账时间，借款起息日
                    map.put("gmtBorrowEnd", repaymentDay);//借款结束日
                    map.put("gmtBorrowTime", format.format(arrivalStart)+"至"+format.format(repaymentDay));//借款结束日
                    map.put("amountCapital", "人民币"+toCapital(afBorrowCashDo.getAmount().doubleValue()));
                    map.put("amountLower", "￥"+afBorrowCashDo.getAmount());
                    AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
                    map.put("lender", lenderDo.getValue());// 出借人
                    secondSeal(map, lenderDo,afUserDo, accountDo);
                    if (null == map.get("companySelfSeal") ) {
                        logger.error("公司印章不存在" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
                        throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                    }
//        map.put("CompanyUserSeal", companyUserSealDo.getUserSeal());
                    if (null == map.get("personUserSeal")){
                        logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
//                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                        throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                    }
                }
            }

            if (renewalId > 0) {
                AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalId);
                Date gmtCreate = afRenewalDetailDo.getGmtCreate();
                Date gmtPlanRepayment = afRenewalDetailDo.getGmtPlanRepayment();
                // 如果预计还款时间在申请日期之后，则在原预计还款时间的基础上加上续期天数，否则在申请日期的基础上加上续期天数，作为新的续期截止时间
                if (gmtPlanRepayment.after(gmtCreate)) {
                    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, afRenewalDetailDo.getRenewalDay()));
                    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                    map.put("gmtRenewalBegin", gmtPlanRepayment);
                    map.put("gmtRenewalEnd", repaymentDay);
                    map.put("gmtRenewalTime", format.format(gmtPlanRepayment)+ "至" + format.format(repaymentDay));
                    map.put("repaymentDay", repaymentDay);
                } else {
                    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtCreate, afRenewalDetailDo.getRenewalDay()));
                    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                    map.put("gmtRenewalBegin", gmtCreate);
                    map.put("gmtRenewalEnd", repaymentDay);
                    map.put("gmtRenewalTime", format.format(gmtPlanRepayment)+ "至" + format.format(repaymentDay));
                    map.put("repaymentDay", format.format(repaymentDay));
                }
                map.put("renewalAmountLower", "￥"+afRenewalDetailDo.getRenewalAmount());//续借金额小写
                map.put("renewalAmountCapital", "人民币"+toCapital(afRenewalDetailDo.getRenewalAmount().doubleValue()));//续借金额大写
                map.put("repayAmountLower", "￥"+afRenewalDetailDo.getCapital());//续借金额小写
                map.put("repayAmountCapital", "人民币"+toCapital(afRenewalDetailDo.getCapital().doubleValue()));//续借金额大写
//				Date gmtRenewalBegin = afRenewalDetailDo.getGmtCreate();
//				Date gmtRenewalEnd = DateUtil.addDays(gmtRenewalBegin, afRenewalDetailDo.getRenewalDay());
            } else {
                Date gmtPlanRepayment = afBorrowCashDo.getGmtPlanRepayment();
                Date now = new Date(System.currentTimeMillis());
                // 如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的续期截止时间
                if (gmtPlanRepayment.after(now)) {
                    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, renewalDay));
                    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                    map.put("gmtRenewalBegin", gmtPlanRepayment);
                    map.put("gmtRenewalEnd", repaymentDay);
                    map.put("gmtRenewalTime", format.format(gmtPlanRepayment)+ "至" + format.format(repaymentDay));
                    map.put("repaymentDay", format.format(repaymentDay));
                } else {
                    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, renewalDay));
                    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                    map.put("gmtRenewalBegin", now);
                    map.put("gmtRenewalEnd", repaymentDay);
                    map.put("gmtRenewalTime", format.format(gmtPlanRepayment)+ "至" + format.format(repaymentDay));
                    map.put("repaymentDay", format.format(repaymentDay));
                }
                map.put("renewalAmountLower", "￥"+renewalAmount);//续借金额小写
                map.put("renewalAmountCapital", "人民币"+toCapital(renewalAmount.doubleValue()));//续借金额大写
                AfResourceDo capitalRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RENEWAL_CAPITAL_RATE);
                BigDecimal renewalCapitalRate = new BigDecimal(capitalRateResource.getValue());// 借钱手续费率（日）
                BigDecimal capital = afBorrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
                map.put("repayAmountLower", "￥"+capital);//续借金额小写
                map.put("repayAmountCapital", "人民币"+toCapital(capital.doubleValue()));//续借金额大写
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("templatePath",src+"51返呗续借协议"+".pdf");
        map.put("PDFPath",src+accountDo.getRealName()+"51返呗续借协议"+new Date().getTime()+".pdf");
        map.put("userPath",src+accountDo.getRealName()+"51返呗续借协议"+new Date().getTime()+".pdf");
        map.put("selfPath",src+accountDo.getRealName()+"51返呗续借协议"+new Date().getTime()+".pdf");
        map.put("secondPath",src+accountDo.getRealName()+"51返呗续借协议"+new Date().getTime()+".pdf");
        if (pdfCreate(map))
//            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
            throw new FanbeiException(FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
        logger.info(JSON.toJSONString(map));
    }

    private boolean pdfCreate(Map map) {
        try {
            PdfCreateUtil.create(map);
        } catch (Exception e) {
            logger.error("pdf合同生成失败：", e);
            return true;
        }
        try {
            afESdkService.userSign(map);
        } catch (Exception e) {
            logger.error("甲方盖章证书生成失败：", e);
            return true;
        }

        try {
            afESdkService.selfSign(map);
        } catch (Exception e) {
            logger.error("丙方盖章证书生成失败：", e);
            return true;
        }
        try {
            afESdkService.secondSign(map);
        } catch (Exception e) {
            logger.error("乙方盖章证书生成失败：", e.getMessage());
            return true;
        }
        try {
            File file = new File(map.get("secondPath").toString());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile =new MockMultipartFile("file", file.getName(), "text/plain", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult =  ossFileUploadService.uploadFileToOss(multipartFile);
            if (null != ossUploadResult.getUrl()){
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
                if ("1".equals(protocolCashType)) {//借款协议
                    afContractPdfDo.setType((byte) 1);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long)map.get("borrowId"));
                }else if ("2".equals(protocolCashType)){//分期服务协议
                    afContractPdfDo.setType((byte) 2);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long)map.get("borrowId"));
                }else if ("3".equals(protocolCashType)){//续借协议
                    afContractPdfDo.setType((byte) 3);
                    afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                    afContractPdfDo.setTypeId((Long)map.get("renewalId"));
                }
                    afContractPdfDao.insert(afContractPdfDo);
            }

            if (ossUploadResult.isSuccess()){
                File file1 = new File(map.get("PDFPath").toString());
                file1.delete();
                file1 = new File(map.get("userPath").toString());
                file1.delete();
                file1 = new File(map.get("selfPath").toString());
                file1.delete();
                file1 = new File(map.get("secondPath").toString());
                file1.delete();
            }
        }catch (Exception e){
            logger.error("证书上传oss失败：", e.getMessage());
            return true;
        }
        return false;
    }

    private void secondSeal(Map map, AfResourceDo lenderDo,AfUserDo afUserDo,AfUserAccountDo accountDo) {
        AfUserSealDo companySealDo = afESdkService.selectUserSealByUserId(-1l);
        if (null != companySealDo && null != companySealDo.getUserSeal()){
            map.put("companySelfSeal",companySealDo.getUserSeal());
        }
        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null != afUserSealDo && null != afUserSealDo.getUserSeal() && null != afUserSealDo.getUserAccountId()){
            map.put("personUserSeal", afUserSealDo.getUserSeal());
            map.put("accountId", afUserSealDo.getUserAccountId());
        }
        if (null != lenderDo.getValue()){
            if ("浙江名信信息科技有限公司".equals(lenderDo.getValue())){
                AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-2l);
                if (null != companyUserSealDo.getUserSeal()){
                    map.put("secondSeal",companyUserSealDo.getUserSeal());
                    map.put("secondAccoundId",companyUserSealDo.getUserAccountId());
                }
            }else if ("浙江名恒投资管理有限公司".equals(lenderDo.getValue())){
                AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-3l);
                if (null != companyUserSealDo.getUserSeal()){
                    map.put("secondSeal",companyUserSealDo.getUserSeal());
                    map.put("secondAccoundId",companyUserSealDo.getUserAccountId());
                }
            }
        }
    }

    public static String ToBig(int num) {
        String str[] = { "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾" };
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
