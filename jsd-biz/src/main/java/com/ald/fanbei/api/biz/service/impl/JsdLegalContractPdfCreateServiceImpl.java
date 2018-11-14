package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.VelocityUtil;
import com.ald.fanbei.api.biz.util.HtmlToPdfUtil;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.itextpdf.text.DocumentException;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@Service("dsedLegalContractPdfCreateService")
public class JsdLegalContractPdfCreateServiceImpl implements JsdLegalContractPdfCreateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsdLegalContractPdfCreateService.class);

    @Resource
    JsdESdkService jsdESdkService;
    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    JsdContractPdfDao jsdContractPdfDao;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdUserSealService jsdUserSealService;

    private static final BigDecimal NUM100 = new BigDecimal(100);
    private static String src = null;

    static {
        if (null != System.getProperty("os.name") && System.getProperty("os.name").toLowerCase().contains("windows")) {
            src = "E:/";
        } else {
            src = "/home/aladin/project/ygCodeUse/PDF/";
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
        data.put("bfurl", resdo.getValue1());
        data.put("yfurl", resdo.getValue4());
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

        data.put("thirdPartyKey","tanGeZhi");
        getData(data,jsdUserSealService.getUserSealByUserName("浙江弹个指网络科技有限公司"));
        stream = jsdESdkService.thirdStreamSign(data,stream).getStream();
        data.put("thirdPartyKey","langXia");
        getData(data,jsdUserSealService.getUserSealByUserName("杭州朗下网络科技有限公司"));
        stream = jsdESdkService.thirdStreamSign(data,stream).getStream();

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
        if (pdf == null) {
            result = jsdContractPdfDao.insert(afContractPdfDo);
        } else {
            afContractPdfDo.setId(pdf.getId());
            result = jsdContractPdfDao.updateById(afContractPdfDo);
        }
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

}
