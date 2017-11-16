package com.ald.fanbei.api.web.api.user;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.EviDoc;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.biz.util.PdfCreateUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ald.fanbei.api.web.apph5.controller.AppH5ProtocolController.toCapital;


/**
 * @author guoshuaiqiang 2017年10月27日下午1:49:07
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashProtocolApi")
public class GetBorrowCashProtocolApi implements ApiHandle {

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
    EviDoc eviDoc;
    @Resource
    private EsignPublicInit esignPublicInit;
    @Resource
    private ContractPdfThreadPool contractPdfThreadPool;

    private static final String src = "F:/doc/";
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String protocolCashType = ObjectUtils.toString(requestDataVo.getParams().get("protocolCashType"), "").toString();
        if (null != protocolCashType && !"".equals(protocolCashType)) {
            try {
                if ("1".equals(protocolCashType)) {//借款协议
                    protocolCashLoan(requestDataVo, resp);
                }else if ("2".equals(protocolCashType)){//分期服务协议
                    protocolFenqiService(requestDataVo, resp);
                }else if ("3".equals(protocolCashType)){//续借协议
                    protocolRenewal(requestDataVo, resp);
                }
            }catch (IOException e){
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);
            }
        }
        return resp;
    }

    public ApiHandleResponse protocolFenqiService(RequestDataVo requestDataVo,ApiHandleResponse resp) throws IOException {
        String userName = ObjectUtils.toString(requestDataVo.getParams().get("userName"), "").toString();
        Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("amount"), new BigDecimal(0));
        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("poundage"), new BigDecimal(0));

        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map map = new HashMap();
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        map.put("idNumber", accountDo.getIdNumber());
        map.put("realName", accountDo.getRealName());
        map.put("protocolCashType", "2");
        AfResourceDo consumeDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsume.getCode());
        AfResourceDo consumeOverdueDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowConsumeOverdue.getCode());
        AfResourceDo lenderDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode());
        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
        }
        map.put("personUserSeal", afUserSealDo.getUserSeal());
        map.put("accountId", afUserSealDo.getUserAccountId());
        map.put("lender", lenderDo.getValue());// 出借人
        secondSeal(map, lenderDo,afUserDo, accountDo);

        if (null == map.get("companySelfSeal") ) {
            logger.error("公司印章不存在" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
        }
//        map.put("CompanyUserSeal", companyUserSealDo.getUserSeal());
        if (null == map.get("personUserSeal")){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
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
        map.put("templatePath",src+"instalment"+".pdf");
//        map.put("templatePath","http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/dbc284bc91877554.pdf");
        map.put("PDFPath",src+accountDo.getUserName()+"分期服务协议"+new Date().getTime()+1+".pdf");
        map.put("userPath",src+accountDo.getUserName()+"分期服务协议"+new Date().getTime()+2+".pdf");
        map.put("selfPath",src+accountDo.getUserName()+"分期服务协议"+new Date().getTime()+3+".pdf");
        map.put("secondPath",src+accountDo.getUserName()+"分期服务协议"+new Date().getTime()+4+".pdf");
        map.put("fileName",accountDo.getUserName()+"分期服务协议"+new Date().getTime()+4+4);
        if (pdfCreate(map))
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
        logger.info(JSON.toJSONString(map));
        return resp;
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

    public ApiHandleResponse protocolRenewal(RequestDataVo requestDataVo,ApiHandleResponse resp) throws IOException {
        String userName = ObjectUtils.toString(requestDataVo.getParams().get("userName"), "").toString();
        Long borrowId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
        Long renewalId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("renewalId"), 0l);
        int renewalDay = NumberUtil.objToIntDefault(requestDataVo.getParams().get("renewalDay"), 0);
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("renewalAmount"), BigDecimal.ZERO);

        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Map map = new HashMap();
        map.put("realName", accountDo.getRealName());//借款人
        map.put("idNumber", accountDo.getIdNumber());//身份证号
        map.put("phone", userName);//联系方式
        map.put("protocolCashType", "3");
        map.put("email", afUserDo.getEmail());//电子邮箱
        AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()){
            logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
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
                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
                    }
//        map.put("CompanyUserSeal", companyUserSealDo.getUserSeal());
                    if (null == map.get("personUserSeal")){
                        logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
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
//        map.put("templatePath",src+"51返呗续借协议"+".pdf");
        long time = new Date().getTime();
        map.put("templatePath",src+"renewal"+".pdf");
        map.put("PDFPath",src+accountDo.getUserName()+"renewal"+time + 1+".pdf");
        map.put("userPath",src+accountDo.getUserName()+"renewal"+time+2+".pdf");
        map.put("selfPath",src+accountDo.getUserName()+"renewal"+time+3+".pdf");
        map.put("secondPath",src+accountDo.getUserName()+"renewal"+time+4+".pdf");
        map.put("fileName",accountDo.getUserName()+"renewal"+time+4);
        if (pdfCreate(map))
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
        logger.info(JSON.toJSONString(map));
//        afBorrowCashService.updateBalancedDate();
        return resp;
    }

    private ApiHandleResponse protocolCashLoan(RequestDataVo requestDataVo, ApiHandleResponse resp) throws IOException {
        Long borrowId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("borrowAmount"), new BigDecimal(0));
        String userName = ObjectUtils.toString(requestDataVo.getParams().get("userName"), "").toString();
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        Map map = new HashMap();
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        map.put("idNumber", accountDo.getIdNumber());
        map.put("protocolCashType", "1");
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
                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);//创建个人印章失败
                    }
//        map.put("CompanyUserSeal", companyUserSealDo.getUserSeal());
                    if (null == map.get("personUserSeal")){
                        logger.error("创建个人印章失败" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                        return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);//创建个人印章失败
                    }
                    map.put("lenderIdNumber", rate.get("lenderIdNumber"));
                    map.put("lenderIdAmount", afBorrowCashDo.getAmount());
                    map.put("gmtPlanRepayment", simpleDateFormat.format(afBorrowCashDo.getGmtPlanRepayment()));
                }
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = new Date().getTime();
        map.put("templatePath",src+"cashLoan"+".pdf");
//        map.put("templatePath","http://51fanbei-private.oss-cn-hangzhou.aliyuncs.com/test/b046c6f5026b7371.pdf");
        map.put("PDFPath",src+accountDo.getUserName()+"个人现金服务合同"+time+1+".pdf");
        map.put("userPath",src+accountDo.getUserName()+"个人现金服务合同"+time+2+".pdf");
        map.put("selfPath",src+accountDo.getUserName()+"个人现金服务合同"+time+3+".pdf");
        map.put("secondPath",src+accountDo.getUserName()+"个人现金服务合同"+time+4+".pdf");
        map.put("fileName",accountDo.getUserName()+"个人现金服务合同"+time+4);
        if (pdfCreate(map))
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CONTRACT_CREATE_FAILED);//
        logger.info(JSON.toJSONString(map));
        return resp;
    }

    private boolean pdfCreate(Map map) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        try {
            PdfCreateUtil.create(map,fos,bos);
        } catch (Exception e) {
            logger.error("pdf合同生成失败：", e);
            return true;
        }finally {
            if (null != fos){
                fos.flush();
                fos.close();
            }
            if (null != bos){
                bos.close();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.userSign(map);
            if (fileDigestSignResult.isErrShow()){
                return true;
            }
            map.put("esignIdFirst",fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("甲方盖章证书生成失败：", e);
            return true;
        }

        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.selfSign(map);
            if (fileDigestSignResult.isErrShow()){
                return true;
            }
            map.put("esignIdSecond",fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("丙方盖章证书生成失败：", e);
            return true;
        }
        try {
            FileDigestSignResult fileDigestSignResult = afESdkService.secondSign(map);
            if (fileDigestSignResult.isErrShow()){
                return true;
            }
            map.put("esignIdThird",fileDigestSignResult.getSignServiceId());
        } catch (Exception e) {
            logger.error("乙方盖章证书生成失败：", e.getMessage());
            return true;
        }
        String evId = "";
        try {
            evId = eviPdf(map);
        }catch (Exception e){
            logger.error("e签宝存证生成失败：", e);
        }
        FileInputStream input = null;
        try {
            File file = new File(map.get("secondPath").toString());
            input = new FileInputStream(file);
            MultipartFile multipartFile =new MockMultipartFile("file", file.getName(), "application/pdf", input);
            OssUploadResult ossUploadResult =  ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(),"url:",ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()){
                String protocolCashType = map.get("protocolCashType").toString();
                AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
                if (!"".equals(evId)){
                    afContractPdfDo.setEvId(evId);
                }
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
        }catch (Exception e){
            logger.error("证书上传oss失败：", e.getMessage());
            return true;
        }finally {
            if (null != input){
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
        return false;
    }

    private String eviPdf(Map map) {
        String filePath = map.get("secondPath").toString();
        String fileName = map.get("fileName").toString();
        String esignIdThird = map.get("esignIdThird").toString();
        String esignIdSecond = map.get("esignIdSecond").toString();
        String esignIdFirst = map.get("esignIdFirst").toString();
        eviDoc.initGlobalParameters(esignPublicInit.getProjectId(),esignPublicInit.getProjectSecret(),esignPublicInit.getEviUrl(),filePath,fileName,esignIdFirst,esignIdSecond,esignIdThird);
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
            eviDoc.updateFileRequestByPost(updateUrl,map.get("secondPath").toString());
        }
        return evId;
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

}
