package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayInvestorInfoBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.VelocityUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.HtmlToPdfUtil;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.biz.util.PdfCreateUtil;
import com.ald.fanbei.api.common.enums.ProductType;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.dao.JsdEdspayUserInfoDao;
import com.ald.fanbei.api.dal.dao.JsdUserSealDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.itextpdf.text.DocumentException;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("dsedLegalContractPdfCreateService")
public class JsdLegalContractPdfCreateServiceImpl implements JsdLegalContractPdfCreateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsdLegalContractPdfCreateServiceImpl.class);

    @Resource
    JsdUserService jsdUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    JsdESdkService jsdESdkService;

    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    JsdContractPdfDao jsdContractPdfDao;
    @Resource
    JsdUserSealService jsdUserSealService;
    @Resource
    JsdUserSealDao jsdUserSealDao;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdEdspayUserInfoDao edspayUserInfoDao;

    private static final BigDecimal NUM100 = new BigDecimal(100);

    private static String src = null;

    static {
        if (null != System.getProperty("os.name") && System.getProperty("os.name").toLowerCase().contains("windows")) {
            src = "E:/";
        } else {
            src = "/home/aladin/project/jsdCodeUse/PDF/";
        }
    }

    private JsdUserDo getUserInfo(long userId, Map<String, Object> map, List<EdspayInvestorInfoBo> investorList) {
        JsdUserDo jsdUserDo = jsdUserService.getById(userId);
        if (jsdUserDo == null) {
            logger.error("user not exist => {}" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        map.put("idNumber", jsdUserDo.getIdNumber());
        map.put("realName", jsdUserDo.getRealName());
        map.put("mobile", jsdUserDo.getMobile());// 联系电话
        getSeal(map, jsdUserDo, investorList);//获取印章
        return jsdUserDo;
    }

    private void getSeal(Map<String, Object> map, JsdUserDo dsedUserDo, List<EdspayInvestorInfoBo> investorList) {
        try {
            JsdUserSealDo companyUserSealDo = jsdESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("companyUserSeal", companyUserSealDo.getUserSeal());
            } else {
                logger.error("公司印章不存在 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
            JsdUserSealDo dsedUserSealDo = jsdESdkService.getSealPersonal(dsedUserDo);
            if (null == dsedUserSealDo || null == dsedUserSealDo.getUserAccountId() || null == dsedUserSealDo.getUserSeal()) {
                logger.error("创建个人印章失败 => {}" + BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
                throw new BizException(BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
            }
            map.put("personUserSeal", dsedUserSealDo.getUserSeal());
            map.put("accountId", dsedUserSealDo.getUserAccountId());
            if (investorList != null && investorList.size() != 0) {
                Byte protocolCashType = Byte.valueOf(String.valueOf(map.get("protocolCashType")));
                Long borrowId = Long.parseLong(String.valueOf(map.get("borrowId")));
                List<JsdEdspayUserInfoDo> userInfoDos = edspayUserInfoDao.getInfoByTypeAndTypeId(protocolCashType, borrowId);
                if (userInfoDos == null || userInfoDos.size() == 0) {//之前没插入在记录保存
                    for (EdspayInvestorInfoBo infoBo : investorList) {
                        JsdEdspayUserInfoDo edspayUserInfoDo = new JsdEdspayUserInfoDo();
                        edspayUserInfoDo.setAmount(infoBo.getAmount());
                        edspayUserInfoDo.setEdspayUserCardId(infoBo.getInvestorCardId());
                        edspayUserInfoDo.setUserName(infoBo.getInvestorName());
                        edspayUserInfoDo.setMobile(infoBo.getInvestorPhone());
                        edspayUserInfoDo.setType(Integer.valueOf(protocolCashType));
                        edspayUserInfoDo.setTypeId(borrowId);
                        edspayUserInfoDo.setProtocolUrl(String.valueOf(map.get("PDFPath")));
                        edspayUserInfoDao.saveRecord(edspayUserInfoDo);

                    }
                }
            }

            companyUserSealDo = jsdUserSealDao.selectByUserId(-4l);//浙江楚橡信息科技有限公司
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("thirdSeal", companyUserSealDo.getUserSeal());
                map.put("thirdAccoundId", companyUserSealDo.getUserAccountId());
            } else {
                logger.error("创建钱包印章失败 => {}" + BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new BizException(BizExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
        } catch (Exception e) {
            logger.error("UserSeal create error", e);
        }
    }

    @Override
    public String getProtocalLegalWithOutLenderByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException {
        Map<String, Object> map = new HashMap();
        map.put("personKey", borrowerName);//借款人印章定位关键字
        if (debtType == 4) {//白领贷借款
            JsdBorrowCashDo loanDo = jsdBorrowCashService.getByBorrowNo(orderNo);
            if (loanDo == null) {
                logger.error("白领贷借款信息不存在 => {}", orderNo);
                throw new BizException(BizExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            JsdContractPdfDo jsdContractPdfDo = new JsdContractPdfDo();
            jsdContractPdfDo.setType((byte) 5);
            jsdContractPdfDo.setTypeId(loanDo.getRid());
            JsdContractPdfDo pdf = jsdContractPdfDao.selectByTypeId(jsdContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfoWithOutLender(protocolUrl, map, loanDo.getUserId(), loanDo.getRid(), "whiteCashloan", "5", investorList);
        }
        return null;
    }

    private ByteArrayOutputStream getByteArrayOutputStream(String outFilePath) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(new File(outFilePath));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
        byte[] b = new byte[1024];
        int n;
        while ((n = bufferedInputStream.read(b)) != -1) {
            bos.write(b, 0, n);
        }
        fis.close();
        bos.close();
        return bos;
    }

    private File getFinalFile(String outFilePath, byte[] stream) throws IOException {
        File file = new File(outFilePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(stream);
        outputStream.flush();
        outputStream.close();
        return file;
    }

    private String getPdfInfoWithOutLender(String protocolUrl, Map<String, Object> map, Long userId, Long id, String type, String protocolCashType, List<EdspayInvestorInfoBo> investorList) throws IOException {
        map.put("borrowId", id);
        map.put("protocolCashType", protocolCashType);
        map.put("PDFPath", protocolUrl);
        JsdUserDo dsedUserDo = getUserInfo(userId, map, investorList);
        long time = new Date().getTime();
        map.put("uploadPath", src + dsedUserDo.getMobile() + type + time + ".pdf");
        map.put("userPath", src + dsedUserDo.getMobile() + type + time + 1 + ".pdf");
        map.put("sealWidth", "70");
        map.put("posType", "1");
        map.put("signType", "Key");
        map.put("secondPartyKey", "商务股份有限公司");//阿拉丁签章关键字
        return getLegalContractPdfWithOutLender(map);
    }

    private byte[] downLoadByUrl(String url) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            return PdfCreateUtil.downLoadByUrl(url, bos);
        } catch (Exception e) {
            logger.error("downLoadByUrl pdf合同生成失败 => {}", e);
        } finally {
            if (null != bos) {
                bos.close();
            }
        }
        return null;
    }

    public byte[] StreamSign(Long borrowId, String fileName, String type, String sealData, String accountId, int posType, float width, String key, String posPage, boolean isQrcodeSign, byte[] stream) {
        try {
            FileDigestSignResult fileDigestSignResult = jsdESdkService.streamSign(fileName, type, sealData, accountId, posType, width, key, posPage, isQrcodeSign, stream);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                logger.error("StreamSign 盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",personKey =" + key + ",borrowId = " + borrowId);
                throw new RuntimeException("StreamSign 盖章证书生成失败");
            }
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("StreamSign 盖章证书生成失败 => {}", e + ",personKey =" + key + ",borrowId = " + borrowId);
            throw new RuntimeException("StreamSign 盖章证书生成失败");
        }
    }

    public byte[] selfStreamSign(Long borrowId, String fileName, String type, String accountId, int posType, float width, String key, String posPage, boolean isQrcodeSign, byte[] stream) {
        try {
            FileDigestSignResult fileDigestSignResult = jsdESdkService.selfStreamSign(fileName, type, accountId, posType, width, key, posPage, isQrcodeSign, stream);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                logger.error("selfStreamSign 盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",personKey =" + key + ",borrowId = " + borrowId);
            }
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("selfStreamSign 盖章证书生成失败 => {}", e + ",personKey =" + key + ",borrowId = " + borrowId);
            throw e;
        }
    }

    private byte[] borrowerCreateSeal(byte[] stream, Map<String, Object> map) {
        try {
            FileDigestSignResult fileDigestSignResult = jsdESdkService.userSign(map);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                logger.error("getLegalContractPdf 甲方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("personKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
                throw new RuntimeException("getLegalContractPdf 甲方盖章证书生成失败");
            }
            map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("getLegalContractPdf 甲方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("personKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            throw new RuntimeException("getLegalContractPdf 甲方盖章证书生成失败");
        } finally {
            File file1 = new File(String.valueOf(map.get("userPath")));
            file1.delete();
        }
    }

    private byte[] aldCreateSeal(boolean result, byte[] stream, Map<String, Object> map) {
        try {
            FileDigestSignResult fileDigestSignResult = jsdESdkService.selfStreamSign(map, stream);//阿拉丁盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }
    }

    private String ossFileUploadWithEdspaySeal(Map<String, Object> map, String fileName, InputStream input) throws IOException {
        try {
            MultipartFile multipartFile = new MockMultipartFile("file", fileName, "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = String.valueOf(map.get("protocolCashType"));
                JsdContractPdfDo afContractPdfDo = new JsdContractPdfDo();
                afContractPdfDo.setType(Byte.valueOf(protocolCashType));
                afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                afContractPdfDo.setTypeId(Long.parseLong(map.get("borrowId").toString()));
                JsdContractPdfDo pdf = jsdContractPdfDao.selectByTypeId(afContractPdfDo);
                if (pdf == null) {
                    jsdContractPdfDao.insert(afContractPdfDo);
                } else {
                    afContractPdfDo.setId(pdf.getId());
                    jsdContractPdfDao.updateById(afContractPdfDo);
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
            File file1 = new File(String.valueOf(map.get("PDFPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("userPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("selfPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("secondPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("thirdPath")));
            file1.delete();
        }
        return null;
    }

    private String getLegalContractPdfWithOutLender(Map<String, Object> map) throws IOException {
        byte[] stream = new byte[1024];
        stream = borrowerCreateSeal(stream, map);//借款人签章
        if (stream != null) {
            if (null != map.get("secondPartyKey") && !"".equals(map.get("secondPartyKey"))) {//ald签章
                stream = selfStreamSign((Long) map.get("borrowId"), "爱上街合同", "Key", (String) map.get("secondAccoundId"), Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")), Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")), (String) map.get("secondPartyKey"), "6", false, stream);
            }
            if (null != map.get("thirdPartyKey") && !"".equals(map.get("thirdPartyKey"))) {//出借人签章
                stream = StreamSign((Long) map.get("borrowId"), "爱上街合同", "Key", (String) map.get("thirdSeal"), (String) map.get("thirdAccoundId"), Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")), Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")), (String) map.get("thirdPartyKey"), "6", false, stream);
            }
            InputStream inputStream = new ByteArrayInputStream(stream);
            return ossFileUploadWithEdspaySeal(map, String.valueOf(map.get("uploadPath")), inputStream);//oss上传
        } else {
            return null;
        }
    }

    @Override
    public void platformServiceSellProtocol(String tradeNoXgxy) throws IOException, DocumentException {
        long time = new Date().getTime();
        HashMap<String, Object> data = new HashMap<>();
        String html = VelocityUtil.getHtml(platformProtocol(data, tradeNoXgxy, "template/cashProtocol.vm"));
        String outFilePath = src + data.get("realName") + "platform" + time + ".pdf";
        try {
            HtmlToPdfUtil.htmlContentWithCssToPdf(html, outFilePath, null);
            getPlatformServiceSellProtocolPdf(data, outFilePath);
        } catch (Exception e) {
            throw e;
        } finally {
            File file = new File(outFilePath);
            file.delete();
        }
    }

    @Override
    public void platformServiceBeheadProtocol(String tradeNoXgxy) throws IOException, DocumentException {
        long time = new Date().getTime();
        HashMap<String, Object> data = new HashMap<>();
        String html = VelocityUtil.getHtml(platformBeheadProtocol(data, tradeNoXgxy, "template/cashPlusProtocol.vm"));
        String outFilePath = src + data.get("realName") + "platform" + time + ".pdf";
        try {
            HtmlToPdfUtil.htmlContentWithCssToPdf(html, outFilePath, null);
            getPlatformServiceBeheadProtocolPdf(data, outFilePath);
        } catch (Exception e) {
            throw e;
        } finally {
            File file = new File(outFilePath);
            file.delete();
        }
    }

    public Map platformProtocol(HashMap<String, Object> data, String tradeNoXgxy, String pdfTemplate) throws IOException {
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PROTOCOL_BORROW.name(), ResourceSecType.PROTOCOL_BORROW_CASH.name());
        data.put("yfCompany", resdo.getValue1());
        data.put("bfCompany", resdo.getValue2());
        data.put("dfCompany", resdo.getValue3());
        JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        data.put("borrowId",cashDo.getRid());
        JsdUserDo userDo = jsdUserService.getById(cashDo.getUserId());
        if (userDo == null) {
            logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        JsdUserSealDo afUserSealDo = jsdESdkService.getSealPersonal(userDo);
        data.put("personUserSeal", afUserSealDo.getUserSeal());
        data.put("accountId", afUserSealDo.getUserAccountId());
        data.put("idNumber", userDo.getIdNumber());
        data.put("realName", userDo.getRealName());
        data.put("email", userDo.getEmail());	//电子邮箱
        data.put("mobile", userDo.getMobile());	// 联系电话
        BigDecimal amountLower, interestRate, serviceRate;
        amountLower = cashDo.getAmount();
        interestRate = cashDo.getInterestRate();
        serviceRate = cashDo.getPoundageRate();
        data.put("borrowRemark",cashDo.getBorrowRemark());
        data.put("borrowNo", cashDo.getBorrowNo());
        data.put("gmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtPlanRepayment", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
        data.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
        data.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
        data.put("amountLower", amountLower);
        data.put("key", "platform");
        data.put("templateSrc",pdfTemplate);
        return data;
    }

    public Map platformBeheadProtocol(HashMap<String, Object> data, String tradeNoXgxy, String pdfTemplate) throws IOException {
        JsdResourceDo resdo = jsdResourceService.getByTypeAngSecType(ResourceType.PLUS_PROTOCOL_BORROW.name(), ResourceSecType.PLUS_PROTOCOL_BORROW_CASH.name());
        data.put("bfCompany", resdo.getValue2());
        data.put("yfCompany", resdo.getValue3());
        JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        JsdUserDo userDo = jsdUserService.getById(cashDo.getUserId());
        if (userDo == null) {
            logger.error("user not exist" + BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        JsdUserSealDo afUserSealDo = jsdESdkService.getSealPersonal(userDo);
        data.put("personUserSeal", afUserSealDo.getUserSeal());
        data.put("accountId", afUserSealDo.getUserAccountId());
        data.put("idNumber", userDo.getIdNumber());
        data.put("realName", userDo.getRealName());
        data.put("email", userDo.getEmail());	//电子邮箱
        data.put("mobile", userDo.getMobile());	// 联系电话
        BigDecimal amountLower, interestRate, serviceRate;
        String borrowRemark ="" ,repayRemark = "";
        amountLower = cashDo.getAmount();
        interestRate = cashDo.getInterestRate();
        serviceRate = cashDo.getPoundageRate();
        borrowRemark = cashDo.getBorrowRemark();
        repayRemark = cashDo.getRepayRemark();
        data.put("borrowNo", cashDo.getBorrowNo());
        data.put("gmtStart", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtEnd", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtPlanRepayment", DateUtil.formatDate(cashDo.getGmtPlanRepayment(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("gmtSign", DateUtil.formatDate(cashDo.getGmtCreate(), DateUtil.DEFAULT_CHINESE_SIMPLE_PATTERN));
        data.put("productType",resdo.getValue5());
        data.put("key", "platform");
        data.put("templateSrc",pdfTemplate);
        data.put("interestRate", interestRate.multiply(NUM100).setScale(2) + "%");
        data.put("serviceRate", serviceRate.multiply(NUM100).setScale(2) + "%");
        data.put("amountCapital", NumberUtil.number2CNMontrayUnit(amountLower));
        data.put("amountLower", amountLower);
        data.put("borrowRemark",borrowRemark);
        data.put("repayRemark",repayRemark);

        return data;
    }

    private String getPlatformServiceSellProtocolPdf(Map<String, Object> data, String outFilePath) throws IOException {
        data.put("userPath", outFilePath);
        byte[] stream = null;
        ByteArrayOutputStream bos = getByteArrayOutputStream(outFilePath);
        stream = bos.toByteArray();
        stream = borrowerCreateSealByStream(stream, data);//借款人签章

        data.put("thirdPartyKey","lvYou");
        getData(data,jsdUserSealService.getUserSealByUserName("杭州绿游网络科技有限公司"));
        stream = jsdESdkService.thirdStreamSign(data,stream).getStream();//绿游
        data.put("thirdPartyKey","chuXiang");
        getData(data,jsdUserSealService.getUserSealByUserName("浙江楚橡信息科技有限公司"));
        stream = jsdESdkService.thirdStreamSign(data,stream).getStream();//楚橡

        File file = getFinalFile(outFilePath, stream);

        OssUploadResult ossUploadResult = null;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
        } catch (Exception e) {
            throw e;
        } finally {
            input.close();
        }
        logger.info("ossUploadResult=" + ossUploadResult.getUrl());

        saveContractPdf(ossUploadResult.getUrl(), "4", Long.parseLong(String.valueOf(data.get("borrowId"))));
        return ossUploadResult.getUrl();
    }



    private String getPlatformServiceBeheadProtocolPdf(Map<String, Object> data, String outFilePath) throws IOException {
        data.put("userPath", outFilePath);
        byte[] stream = null;
        ByteArrayOutputStream bos = getByteArrayOutputStream(outFilePath);
        stream = bos.toByteArray();
        stream = borrowerCreateSealByStream(stream, data);//借款人签章
        if(StringUtils.equals(ProductType.DFD.name(),data.get("productType").toString())){
            data.put("thirdPartyKey","yfCompanyPic");
            getData(data,jsdUserSealService.getUserSealByUserName("浙江弹个指网络科技有限公司"));
            stream = jsdESdkService.thirdStreamSign(data,stream).getStream();
            data.put("thirdPartyKey","bfCompanyPic");
            getData(data,jsdUserSealService.getUserSealByUserName("杭州朗下网络科技有限公司"));
            stream = jsdESdkService.thirdStreamSign(data,stream).getStream();
        }
        if(StringUtils.equals(ProductType.JGD.name(),data.get("productType").toString())){
            data.put("thirdPartyKey","yfCompanyPic");
            getData(data,jsdUserSealService.getUserSealByUserName("金泰嘉鼎（深圳）资产管理有限公司"));
            stream = jsdESdkService.thirdStreamSign(data,stream).getStream();
            data.put("thirdPartyKey","bfCompanyPic");
            getData(data,jsdUserSealService.getUserSealByUserName("杭州绿游网络科技有限公司"));
            stream = jsdESdkService.thirdStreamSign(data,stream).getStream();
        }


        File file = getFinalFile(outFilePath, stream);

        OssUploadResult ossUploadResult = null;
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
            ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
        } catch (Exception e) {
            throw e;
        } finally {
            input.close();
        }
        logger.info("ossUploadResult=" + ossUploadResult.getUrl());

        saveContractPdf(ossUploadResult.getUrl(), "4", Long.parseLong(String.valueOf(data.get("borrowId"))));
        return ossUploadResult.getUrl();
    }





    private byte[] borrowerCreateSealByStream(byte[] stream, Map<String, Object> map) {
        FileDigestSignResult fileDigestSignResult = jsdESdkService.userStreamSign(map, stream);//借款人盖章
        if (fileDigestSignResult.getErrCode() != 0) {
            logger.error("getLegalContractPdf 借款人盖章pdf生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("personKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            throw new BizException(BizExceptionCode.CONTRACT_CREATE_FAILED);
        }
        map.put("esignIdFirst", fileDigestSignResult.getSignServiceId());
        return fileDigestSignResult.getStream();
    }

    private int saveContractPdf(String url, String protocolCashType, Long loanId) {
        int result = 0;
        JsdContractPdfDo afContractPdfDo = new JsdContractPdfDo();
        afContractPdfDo.setType(Byte.valueOf(protocolCashType));
        afContractPdfDo.setContractPdfUrl(url);
        afContractPdfDo.setTypeId(loanId);
        JsdContractPdfDo pdf = jsdContractPdfDao.selectByTypeId(afContractPdfDo);
        logger.info("saveContractPdf pdf = " + pdf +"afContractPdfDo = " + JSON.toJSONString(afContractPdfDo));
        if (pdf == null) {
            result = jsdContractPdfDao.insert(afContractPdfDo);
        } else {
            afContractPdfDo.setId(pdf.getId());
            result = jsdContractPdfDao.updateById(afContractPdfDo);
        }
        logger.info("saveContractPdf result = " + result);
        return result;
    }

    private void getUserSeal(ModelMap map, JsdUserDo afUserDo) {
        JsdUserSealDo afUserSealDo = jsdUserSealService.getUserSeal(afUserDo);
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId() || null == afUserSealDo.getUserSeal()) {
            logger.error("获取个人印章失败 => {}" + BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
            throw new BizException(BizExceptionCode.PERSON_SEAL_CREATE_FAILED);
        }
        map.put("personUserSeal", "data:image/png;base64," + afUserSealDo.getUserSeal());
    }

    private void getData(Map<String, Object> data,JsdUserSealDo jsdUserSealDo){
        data.put("thirdAccoundId",jsdUserSealDo.getUserAccountId());
        data.put("thirdSeal",jsdUserSealDo.getUserSeal());

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
