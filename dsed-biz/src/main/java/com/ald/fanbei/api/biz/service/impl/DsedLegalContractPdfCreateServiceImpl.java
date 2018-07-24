package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.EdspayInvestorInfoBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.*;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.DsedResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.dal.dao.DsedContractPdfDao;
import com.ald.fanbei.api.dal.dao.DsedContractPdfEdspaySealDao;
import com.ald.fanbei.api.dal.dao.DsedEdspayUserInfoDao;
import com.ald.fanbei.api.dal.dao.DsedUserSealDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.DsedContractPdfEdspaySealDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.DocumentException;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service("dsedLegalContractPdfCreateService")
public class DsedLegalContractPdfCreateServiceImpl implements DsedLegalContractPdfCreateService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DsedLegalContractPdfCreateServiceImpl.class);

    @Resource
    DsedUserService dsedUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    DsedESdkService dsedESdkService;
    @Resource
    DsedResourceService dsedResourceService;
    @Resource
    OssFileUploadService ossFileUploadService;
    @Resource
    DsedContractPdfDao afContractPdfDao;
    @Resource
    DsedContractPdfEdspaySealDao dsedContractPdfEdspaySealDao;
    @Resource
    DsedUserSealDao dsedUserSealDao;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    DsedLoanService dsedLoanService;
    @Resource
    DsedEdspayUserInfoDao edspayUserInfoDao;
    @Resource
    DsedLoanPeriodsService dsedLoanPeriodsService;

    private static final String src = "E:/";

    private DsedUserDo getUserInfo(long userId, Map<String, Object> map, List<EdspayInvestorInfoBo> investorList) {
        DsedUserDo dsedUserDo = dsedUserService.getUserById(userId);
        if (dsedUserDo == null) {
            logger.error("user not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        map.put("idNumber", dsedUserDo.getIdNumber());
        map.put("realName", dsedUserDo.getRealName());
        DsedResourceDo lenderDo = dsedResourceService.getConfigByTypesAndSecType(DsedResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        map.put("lender", lenderDo.getValue());// 出借人
        map.put("mobile", dsedUserDo.getMobile());// 联系电话
        getSeal(map, dsedUserDo, investorList);//获取印章
        return dsedUserDo;
    }

    private void getSeal(Map<String, Object> map, DsedUserDo dsedUserDo, List<EdspayInvestorInfoBo> investorList) {
        try {
            DsedUserSealDo companyUserSealDo = dsedESdkService.selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                map.put("companyUserSeal", companyUserSealDo.getUserSeal());
            } else {
                logger.error("公司印章不存在 => {}" + FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.COMPANY_SEAL_CREATE_FAILED);
            }
            DsedUserSealDo dsedUserSealDo = dsedESdkService.getSealPersonal(dsedUserDo);
            if (null == dsedUserSealDo || null == dsedUserSealDo.getUserAccountId() || null == dsedUserSealDo.getUserSeal()) {
                logger.error("创建个人印章失败 => {}" + FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
                throw new FanbeiException(FanbeiExceptionCode.PERSON_SEAL_CREATE_FAILED);
            }
            map.put("personUserSeal", dsedUserSealDo.getUserSeal());
            map.put("accountId", dsedUserSealDo.getUserAccountId());
            if (investorList != null && investorList.size() != 0) {
                Byte protocolCashType = Byte.valueOf(String.valueOf(map.get("protocolCashType")));
                Long borrowId = Long.parseLong(String.valueOf(map.get("borrowId")));
                List<DsedEdspayUserInfoDo> userInfoDos = edspayUserInfoDao.getInfoByTypeAndTypeId(protocolCashType,borrowId);
                if (userInfoDos== null || userInfoDos.size() == 0){//之前没插入在记录保存
                    for (EdspayInvestorInfoBo infoBo : investorList) {
                        DsedEdspayUserInfoDo edspayUserInfoDo = new DsedEdspayUserInfoDo();
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


            companyUserSealDo = dsedUserSealDao.selectByUserId(-4l);//浙江楚橡信息科技有限公司
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

    private void getResourceRate(Map map, String type, DsedResourceDo dsedResourceDo, String borrowType) {
        if (dsedResourceDo != null && dsedResourceDo.getValue2() != null) {
            String oneDay = "";
            String twoDay = "";
            if (null != dsedResourceDo) {
                oneDay = dsedResourceDo.getTypeDesc().split(",")[0];
                twoDay = dsedResourceDo.getTypeDesc().split(",")[1];
            }
            JSONArray array = new JSONArray();
            if ("instalment".equals(borrowType)) {
                array = JSONObject.parseArray(dsedResourceDo.getValue3());
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
                            } else if ("7".equals(type)){
                                map.put("yearRate", jsonObject.get("consumeFirstType"));
                            } else if ("14".equals(type)){
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
                            } else if ("7".equals(type)){
                                map.put("poundageRate", jsonObject.get("consumeFirstType"));
                            } else if ("14".equals(type)){
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
                            }else if ("7".equals(type)){
                                map.put("overdueRate", jsonObject.get("consumeFirstType"));
                            } else if ("14".equals(type)){
                                map.put("overdueRate", jsonObject.get("consumeSecondType"));
                            }
                        }
                    }
                }
            } else if ("borrow".equals(borrowType)) {
                array = JSONObject.parseArray(dsedResourceDo.getValue2());
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
                            }else if ("7".equals(type)){
                                map.put("yearRate", jsonObject.get("borrowFirstType"));
                            } else if ("14".equals(type)){
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
                            } else if ("7".equals(type)){
                                map.put("poundageRate", jsonObject.get("borrowFirstType"));
                            } else if ("14".equals(type)){
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
                            } else if ("7".equals(type)){
                                map.put("overdueRate", jsonObject.get("borrowFirstType"));
                            } else if ("14".equals(type)){
                                map.put("overdueRate", jsonObject.get("borrowSecondType"));
                            }
                        }
                    }
                }
            }

        }
    }

    @Override
    public String getProtocalLegalByType(Integer debtType, String orderNo, String protocolUrl, String borrowerName, List<EdspayInvestorInfoBo> investorList) throws IOException {
        Map<String, Object> map = new HashMap();
        map.put("personKey", borrowerName);//借款人印章定位关键字
        if (debtType == 3){//白领贷借款
            DsedLoanDo loanDo = dsedLoanService.getByLoanNo(orderNo);
            if (loanDo == null) {
                logger.error("白领贷借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            DsedContractPdfDo afContractPdfDo = new DsedContractPdfDo();
            afContractPdfDo.setType((byte) 5);
            afContractPdfDo.setTypeId(loanDo.getRid());
            DsedContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
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
        if (debtType == 2){//白领贷借款
            DsedLoanDo loanDo = dsedLoanService.getByLoanNo(orderNo);
            if (loanDo == null) {
                logger.error("白领贷借款信息不存在 => {}", orderNo);
                throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
            }
            DsedContractPdfDo dsedContractPdfDo = new DsedContractPdfDo();
            dsedContractPdfDo.setType((byte) 5);
            dsedContractPdfDo.setTypeId(loanDo.getRid());
            DsedContractPdfDo pdf = afContractPdfDao.selectByTypeId(dsedContractPdfDo);
            if (pdf != null) {
                return pdf.getContractPdfUrl();
            }
            return getPdfInfoWithOutLender(protocolUrl, map, loanDo.getUserId(), loanDo.getRid(), "whiteCashloan", "5", investorList);
        }
        return null;
    }

    @Override
    public String whiteLoanProtocolPdf(Integer debtType, String loanNo) throws IOException {
        long time = new Date().getTime();
        DsedLoanDo loanDo = dsedLoanService.getById(Long.valueOf(loanNo));
        if (loanDo == null){
            logger.error("白领贷订单不存在 => {}", loanNo);
            throw new FanbeiException(FanbeiExceptionCode.CONTRACT_NOT_FIND.getDesc());
        }
        Map<String, Object> map = new HashMap();
        DsedUserDo dsedUserDo = dsedUserService.getUserById(loanDo.getUserId());
        if (dsedUserDo == null) {
            logger.error("user not exist => {}" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        String html = "";
        String type = "whiteLoan";
        html = getWhiteVelocityHtml(dsedUserDo.getMobile(),loanDo.getAmount(),loanDo.getPeriods(),loanDo.getRid(),"protocolWhiteLoanProtocolTemplate.vm");
        String outFilePath = src + dsedUserDo.getMobile() + type + time + ".pdf";
        HtmlToPdfUtil.htmlContentWithCssToPdf(html, outFilePath, null);
        map.put("borrowId", String.valueOf(loanDo.getRid()));
        map.put("protocolCashType","5");
        map.put("personKey", "borrower");//借款人印章定位关键字
        map.put("posType",1);
        map.put("signType", "Key");
        map.put("firstPartyKey", "borrower");//用户签章关键字
        map.put("secondPartyKey", "ald");//阿拉丁签章关键字
        map.put("sealWidth", "60");
        map.put("PDFPath", outFilePath);
        map.put("userPath", src + dsedUserDo.getMobile() + type + time  + 1+".pdf");
        map.put("selfPath", src + dsedUserDo.getMobile() + type + time  + 2+".pdf");
        map.put("secondPath", src + dsedUserDo.getMobile() + type + time + 3 + ".pdf");
        map.put("thirdPath", src + dsedUserDo.getMobile() + type + time + 4 + ".pdf");
        return getLegalContractPdf(map);
    }

    private String getReceptContractPdf(Map<String, Object> data,String outFilePath) throws IOException {
        long time = new Date().getTime();
        data.put("userPath", outFilePath);
        String dstFile = String.valueOf(src + data.get("userName") + "recept" + time + 2 + ".pdf");
        data.put("selfPath", dstFile);
        boolean result = true;
        byte[] stream = null;
        logger.info("lease data ="+data);
//        stream = borrowerCreateSeal(result,stream,data);//借款人签章
//
        stream = aldLeaseCreateSeal(result,stream,data);//阿拉丁签章

        File file = new File(src + data.get("userName") + "recept" + time +3 + ".pdf");
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(stream);
        outputStream.flush();
        outputStream.close();
        InputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "application/pdf", IOUtils.toByteArray(input));
        OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
        logger.info("ossUploadResult="+ossUploadResult.getUrl());
        logger.info("lease url="+ossUploadResult.getUrl());
        return ossUploadResult.getUrl();
    }
    public Map protocolLease(Map<String,Object> data,String pdfTemplate) throws IOException {
        DsedUserDo dsedUserDo = dsedUserService.getUserByMobile(data.get("userName").toString());
        if (dsedUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = dsedUserDo.getRid();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        data.put("templateSrc",pdfTemplate);
        data.put("personKey","leaser");
        data.put("selfKey","ald");
        Calendar c = Calendar.getInstance();
        c.setTime((Date) data.get("gmtCreate"));
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        data.put("gmtLeaseCreate",year+"年"+month+"月"+day+"日");
        data.put("rentAmount",new BigDecimal(data.get("monthlyRent").toString()).multiply(new BigDecimal(12).setScale(2,BigDecimal.ROUND_HALF_UP)));
        data.put("overdueRate",new BigDecimal(data.get("overdueRate").toString()).multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP));
        c.setTime((Date) data.get("gmtStart"));
        data.put("gmtLeaseStart",c.get(Calendar.YEAR)+"年"+c.get(Calendar.MONTH) + 1+"月"+c.get(Calendar.DATE)+"日");
        c.setTime((Date) data.get("gmtEnd"));
        data.put("gmtLeaseEnd",c.get(Calendar.YEAR)+"年"+c.get(Calendar.MONTH) + 1+"月"+c.get(Calendar.DATE)+"日");
        return data;
    }

    private String getPdfInfo(String protocolUrl, Map<String, Object> map, Long userId, Long id, String type, String protocolCashType, List<EdspayInvestorInfoBo> investorList) throws IOException {
        DsedUserDo dsedUserDo = getUserInfo(userId, map, investorList);
        long time = new Date().getTime();
        map.put("PDFPath", protocolUrl);
        map.put("borrowId", id);
        map.put("protocolCashType", protocolCashType);
        map.put("userPath", src + dsedUserDo.getMobile() + type + time + 1 + ".pdf");
        map.put("selfPath", src + dsedUserDo.getMobile() + type + time + 2 + ".pdf");
        map.put("secondPath", src + dsedUserDo.getMobile() + type + time + 3 + ".pdf");
        map.put("thirdPath", src + dsedUserDo.getMobile() + type + time + 4 + ".pdf");
        return getLegalContractPdf(map);
    }

    private String getPdfInfoWithOutLender(String protocolUrl, Map<String, Object> map, Long userId, Long id, String type, String protocolCashType, List<EdspayInvestorInfoBo> investorList) throws IOException {
        map.put("borrowId", id);
        map.put("protocolCashType",protocolCashType);
        map.put("PDFPath", protocolUrl);
        DsedUserDo dsedUserDo = getUserInfo(userId, map, investorList);
        long time = new Date().getTime();
        map.put("uploadPath", src + dsedUserDo.getMobile() + type + time + ".pdf");
        map.put("userPath", src + dsedUserDo.getMobile() + type + time + 1 + ".pdf");
        map.put("sealWidth", "70");
        map.put("posType", "1");
        map.put("signType", "Key");
        map.put("secondPartyKey", "商务股份有限公司");//阿拉丁签章关键字
        return getLegalContractPdfWithOutLender(map);
    }

    private void createPdf(Map<String, Object> map,boolean result) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = null;
        try {
            PdfCreateUtil.create(map, fos, bos);
        } catch (Exception e) {
            logger.error("PdfCreateByStream pdf合同生成失败 => {}", e);
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
                File file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
            }
        }
    }

    private byte[] createByte(Map<String, Object> map,boolean result) throws IOException {
        OutputStream fos = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            return PdfCreateUtil.createByte(map, fos, bos);
        } catch (Exception e) {
            logger.error("createByte pdf合同生成失败 => {}", e);
        } finally {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
            if (null != bos) {
                bos.close();
            }
        }
        return null;
    }

    private byte[] downLoadByUrl(String url) throws IOException {
        ByteArrayOutputStream bos = null;
        try {
            return PdfCreateUtil.downLoadByUrl(url,bos);
        } catch (Exception e) {
            logger.error("downLoadByUrl pdf合同生成失败 => {}", e);
        } finally {
            if (null != bos) {
                bos.close();
            }
        }
        return null;
    }

    public byte[] firstPartySign(Map<String, Object> map,boolean result){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.firstPartySign(map);//借款人盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
            }
        }
        return null;
    }

    public byte[] StreamSign(Long borrowId,String fileName,String type,String sealData,String accountId,int posType,float width,String key,String posPage,boolean isQrcodeSign,byte[] stream){        try {
        FileDigestSignResult fileDigestSignResult = dsedESdkService.streamSign(fileName,type,sealData,accountId,posType,width,key,posPage,isQrcodeSign,stream);//借款人盖章
        if (fileDigestSignResult.getErrCode() != 0) {
            logger.error("StreamSign 盖章证书生成失败 => {}", fileDigestSignResult.getMsg()  + ",personKey =" + key + ",borrowId = " + borrowId );
        }
        return fileDigestSignResult.getStream();
    } catch (Exception e) {
        logger.error("StreamSign 盖章证书生成失败 => {}", e  + ",personKey =" + key + ",borrowId = " + borrowId );
    }
        return null;
    }

    public byte[] selfStreamSign(Long borrowId,String fileName,String type,String accountId,int posType,float width,String key,String posPage,boolean isQrcodeSign,byte[] stream){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.selfStreamSign(fileName,type,accountId,posType,width,key,posPage,isQrcodeSign,stream);//借款人盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                logger.error("selfStreamSign 盖章证书生成失败 => {}", fileDigestSignResult.getMsg()  + ",personKey =" + key + ",borrowId = " + borrowId );
            }
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("selfStreamSign 盖章证书生成失败 => {}", e  + ",personKey =" + key + ",borrowId = " + borrowId );
        }
        return null;
    }


    public byte[] secondPartySign(Map<String, Object> map,boolean result,byte[] stream){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.secondPartySign(map, stream);//阿拉丁盖章
            if (fileDigestSignResult.getErrCode() != 0) {
                result = false;
                logger.error("PdfCreateByStream 丙方盖章证书生成失败 => {}", fileDigestSignResult.getMsg() + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("secondPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            }
            map.put("esignIdSecond", fileDigestSignResult.getSignServiceId());
            return fileDigestSignResult.getStream();
        } catch (Exception e) {
            logger.error("PdfCreateByStream 丙方盖章证书生成失败 => {}", e + ",PDFPath =" + map.get("PDFPath") + ",personKey =" + map.get("secondPartyKey") + ",borrowId = " + map.get("borrowId") + ",protocolCashType = " + map.get("protocolCashType"));
            result = false;
        } finally {
            if (!result) {
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }
        return null;
    }

    public byte[] thirdPartySign(Map<String, Object> map,boolean result,byte[] stream){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.thirdStreamSign(map, stream);//钱包盖章
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
                File file1 = new File(String.valueOf(map.get("thirdPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("userPath")));
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

    private boolean PdfCreateByStreamNew(Map<String, Object> map) throws IOException {
        boolean result = true;
        byte[] stream = new byte[1024];

        stream = createByte(map,result);

        stream = StreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("personUserSeal"),(String)map.get("accountId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("firstPartyKey"),"5",false,stream);
        if (stream != null){
            if (null != map.get("companySelfSeal") && !"".equals(map.get("companySelfSeal"))) {//ald签章
                stream = selfStreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("secondAccoundId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("secondPartyKey"),"6",false,stream);
//                stream = secondPartySign(map,result,stream);
            }
            if (null != map.get("thirdSeal") && !"".equals(map.get("thirdSeal"))) {//出借人签章
                stream = StreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("thirdSeal"),(String)map.get("thirdAccoundId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("thirdPartyKey"),"6",false,stream);
            }
            InputStream inputStream = new ByteArrayInputStream(stream);
            return ossFileUpload(map, String.valueOf(map.get("uploadPath")),inputStream);//oss上传
        }else {
            return false;
        }
    }

    private boolean ossFileUpload(Map<String, Object> map,String dstFile,InputStream input) throws IOException {
        try {
            MultipartFile multipartFile = new MockMultipartFile("file", dstFile, "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = map.get("protocolCashType").toString();
                DsedContractPdfDo dsedContractPdfDo = new DsedContractPdfDo();
//                if (!"".equals(evId)) {
//                    afContractPdfDo.setEvId(evId);
//                }
                dsedContractPdfDo.setType(Byte.valueOf(protocolCashType));
                dsedContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                dsedContractPdfDo.setTypeId((Long) map.get("borrowId"));
                afContractPdfDao.insert(dsedContractPdfDo);
                return true;
            }
        } catch (Exception e) {
            logger.error("PdfCreateByStream 证书上传oss失败 => {}", e.getMessage());
            return false;
        } finally {
            if (null != input) {
                input.close();
            }
        }
        return false;
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
                DsedContractPdfDo dsedContractPdfDo = new DsedContractPdfDo();
//                if (!"".equals(evId)) {
//                    afContractPdfDo.setEvId(evId);
//                }
                dsedContractPdfDo.setType(Byte.valueOf(protocolCashType));
                dsedContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                dsedContractPdfDo.setTypeId((Long) map.get("borrowId"));
                afContractPdfDao.insert(dsedContractPdfDo);
                return true;
            }
        } catch (Exception e) {
            logger.error("PdfCreateByStream 证书上传oss失败 => {}", e.getMessage());
            return false;
        } finally {
            if (null != input) {
                input.close();
            }
            File file1 = new File(String.valueOf(map.get("PDFPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("userPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("thirdPath")));
            file1.delete();
        }
        return false;
    }

    private byte[] borrowerCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.userSign(map);//借款人盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }
    }

    private byte[] aldCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.selfStreamSign(map, stream);//阿拉丁盖章
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

    private byte[] edsPayLenderCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            List<DsedUserSealDo> list = (List<DsedUserSealDo>) map.get("userSealDoList");
            if (list == null || list.size() == 0){
                return stream;
            }
            FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
            for (DsedUserSealDo userSealDo : list) {
                map.put("key", userSealDo.getUserName());
                map.put("secondSeal", userSealDo.getUserSeal());
                map.put("secondAccoundId", userSealDo.getUserAccountId());
                fileDigestSignResult = dsedESdkService.secondStreamSign(map, stream);//出借人盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }
    }

    private byte[] lenderCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            List<DsedUserSealDo> list = (List<DsedUserSealDo>) map.get("userSealDoList");
            FileDigestSignResult fileDigestSignResult = new FileDigestSignResult();
//            map.put("key", userSealDo.getUserName());
//            map.put("secondSeal", userSealDo.getUserSeal());
//            map.put("secondAccoundId", userSealDo.getUserAccountId());
            fileDigestSignResult = dsedESdkService.secondStreamSign(map, stream);//出借人盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }
    }

    private byte[] edsPayCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            map.put("thirdPartyKey","楚橡信息科技有限公司");
            FileDigestSignResult fileDigestSignResult = dsedESdkService.thirdStreamSign(map, stream);//钱包盖章
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
                File file1 = new File(String.valueOf(map.get("userPath")));
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
                DsedContractPdfDo afContractPdfDo = new DsedContractPdfDo();
                afContractPdfDo.setType(Byte.valueOf(protocolCashType));
                afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                afContractPdfDo.setTypeId(Long.parseLong(map.get("borrowId").toString()));
                DsedContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
                if (pdf != null) {
                    List<DsedContractPdfEdspaySealDto> seal = dsedContractPdfEdspaySealDao.getByPDFId(pdf.getId());
                    if (seal == null || seal.size() == 0) {
                        List<DsedContractPdfEdspaySealDo> edspaySealDoList = (List<DsedContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                        if (edspaySealDoList != null && edspaySealDoList.size() > 0){
                            for (DsedContractPdfEdspaySealDo edspaySealDo : edspaySealDoList) {
                                edspaySealDo.setPdfId(afContractPdfDo.getId());
                            }
                            dsedContractPdfEdspaySealDao.batchInsert(edspaySealDoList);
                        }
                    }
                } else {
                    afContractPdfDao.insert(afContractPdfDo);
                    List<DsedContractPdfEdspaySealDo> edspaySealDoList = (List<DsedContractPdfEdspaySealDo>) map.get("edspaySealDoList");
                    if (edspaySealDoList == null || edspaySealDoList.size() == 0){
                        return ossUploadResult.getUrl();
                    }
                    for (DsedContractPdfEdspaySealDo edspaySealDo : edspaySealDoList) {
                        edspaySealDo.setPdfId(afContractPdfDo.getId());
                    }
                    dsedContractPdfEdspaySealDao.batchInsert(edspaySealDoList);
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

    private String ossFileUploadWithEdspaySeal(Map<String, Object> map,String fileName,InputStream input) throws IOException {
        try {
            MultipartFile multipartFile = new MockMultipartFile("file", fileName, "application/pdf", IOUtils.toByteArray(input));
            OssUploadResult ossUploadResult = ossFileUploadService.uploadFileToOss(multipartFile);
            input.close();
            logger.info(ossUploadResult.getMsg(), "url:", ossUploadResult.getUrl());
            if (null != ossUploadResult.getUrl()) {
                String protocolCashType = String.valueOf(map.get("protocolCashType"));
                DsedContractPdfDo afContractPdfDo = new DsedContractPdfDo();
                afContractPdfDo.setType(Byte.valueOf(protocolCashType));
                afContractPdfDo.setContractPdfUrl(ossUploadResult.getUrl());
                afContractPdfDo.setTypeId(Long.parseLong(map.get("borrowId").toString()));
                DsedContractPdfDo pdf = afContractPdfDao.selectByTypeId(afContractPdfDo);
                if (pdf == null) {
                    afContractPdfDao.insert(afContractPdfDo);
                }else {
                    afContractPdfDo.setId(pdf.getId());
                    afContractPdfDao.updateById(afContractPdfDo);
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
//        stream = downLoadByUrl(map.get("PDFPath").toString());
        stream = borrowerCreateSeal(result,stream,map);//借款人签章
//        stream = StreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("personUserSeal"),(String)map.get("accountId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("personKey"),"5",false,stream);
        if (stream != null){
            if (null != map.get("secondPartyKey") && !"".equals(map.get("secondPartyKey"))) {//ald签章
                stream = selfStreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("secondAccoundId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("secondPartyKey"),"6",false,stream);
//                stream = secondPartySign(map,result,stream);
            }
            if (null != map.get("thirdPartyKey") && !"".equals(map.get("thirdPartyKey"))) {//出借人签章
                stream = StreamSign((Long)map.get("borrowId"),"爱上街合同","Key",(String)map.get("thirdSeal"),(String)map.get("thirdAccoundId"),Integer.valueOf(ObjectUtils.toString(map.get("posType"), "")),Integer.valueOf(ObjectUtils.toString(map.get("sealWidth"), "")),(String)map.get("thirdPartyKey"),"6",false,stream);
            }
            InputStream inputStream = new ByteArrayInputStream(stream);
            return ossFileUploadWithEdspaySeal(map, String.valueOf(map.get("uploadPath")),inputStream);//oss上传
        }else {
            return null;
        }
    }

    private String getWhiteVelocityHtml(String userName,BigDecimal amount,Integer nper,long loanId,String pdfTemplate) {
        try {
            String html = VelocityUtil.getHtml(whiteLoanProtocol(userName, amount, nper, loanId,pdfTemplate));
            return html;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("getWhiteVelocityHtml IOException",e);
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("getWhiteVelocityHtml DocumentException",e);
        }
        return null;
    }

    public Map<String,Object> whiteLoanProtocol(String userName,BigDecimal amount,Integer nper,long loanId,String pdfTemplate) throws IOException {
        DsedUserDo dsedUserDo = dsedUserService.getUserByMobile(userName);
        Map<String, Object> map = new HashMap();
        if (dsedUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = dsedUserDo.getRid();
        map.put("templateSrc",pdfTemplate);
        map.put("idNumber", dsedUserDo.getIdNumber());
        map.put("realName", dsedUserDo.getRealName());
        map.put("email", dsedUserDo.getEmail());//电子邮箱
        map.put("mobile", dsedUserDo.getUserName());// 联系电话
        map.put("amountCapital", toCapital(amount.doubleValue()));//大写本金金额
        map.put("amount", amount);//借钱本金
        getModelLoanId(map, nper, loanId);
        return map;
    }

    private void getModelLoanId(Map<String,Object> map, Integer nper, long loanId) {
        DsedLoanDo afLoanDo = dsedLoanService.selectById(loanId);
        Calendar c = Calendar.getInstance();
        c.setTime(afLoanDo.getGmtCreate());
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        String time = year + "年" + month + "月" + day + "日";
        map.put("time", time);// 签署日期
        map.put("gmtStart", time);
        map.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
        List<DsedLoanPeriodsDo> dsedLoanPeriodsDoList = dsedLoanPeriodsService.listByLoanId(loanId);
        if (null != dsedLoanPeriodsDoList && dsedLoanPeriodsDoList.size() > 0) {
            List<Object> array = new ArrayList<Object>();
            for (int i = 1; i <= afLoanDo.getPeriods(); i++) {
                Map<String, Object> periods = new HashMap<String, Object>();
                DsedLoanPeriodsDo dsedLoanPeriodsDo = dsedLoanPeriodsDoList.get(i - 1);
                c.setTime(dsedLoanPeriodsDo.getGmtPlanRepay());
                int periodsMonth = c.get(Calendar.MONTH) + 1;
                int periodsDay = c.get(Calendar.DATE);
                int periodsYear = c.get(Calendar.YEAR);
                String periodsTime = periodsYear + "年" + periodsMonth + "月" + periodsDay + "日";
                if (i == nper) {
                    map.put("gmtEnd", periodsTime);
                    map.put("days", periodsDay);
                }
                periods.put("days", day);
                periods.put("gmtPlanRepay",periodsTime);
                periods.put("loanAmount", dsedLoanPeriodsDo.getAmount());
                periods.put("periods", i);
                periods.put("fee", BigDecimalUtil.add(dsedLoanPeriodsDo.getInterestFee(),dsedLoanPeriodsDo.getServiceFee(),dsedLoanPeriodsDo.getRepaidInterestFee(),dsedLoanPeriodsDo.getRepaidServiceFee()));
                array.add(periods);
            }
            map.put("nperArray", array);
        }
        map.put("repayRemark", afLoanDo.getRepayRemark());//还款方式
        map.put("loanRemark", afLoanDo.getLoanRemark());//借钱用途
        map.put("totalPeriods", afLoanDo);//总借钱信息
        map.put("interestRate", afLoanDo.getInterestRate().multiply(BigDecimal.valueOf(100))+"%");
        getEdspayInfo(map, loanId, (byte) 5);
    }

    private void getEdspayInfo(Map<String,Object> map, Long borrowId, byte type) {
        DsedContractPdfDo afContractPdfDo = new DsedContractPdfDo();
        afContractPdfDo.setTypeId(borrowId);
        afContractPdfDo.setType(type);
        afContractPdfDo = afContractPdfDao.selectByTypeId(afContractPdfDo);
        List<DsedEdspayUserInfoDo> userInfoDoList = edspayUserInfoDao.getInfoByTypeAndTypeId(type,borrowId);
        if (userInfoDoList != null && userInfoDoList.size() > 0){
            for (DsedEdspayUserInfoDo userInfoDo:userInfoDoList) {
                userInfoDo.setInvestorAmount(userInfoDo.getAmount());
            }
            map.put("edspaySealDoList", userInfoDoList);
        }else {
            if (afContractPdfDo != null){
                List<DsedContractPdfEdspaySealDto> edspaySealDoList = dsedContractPdfEdspaySealDao.getByPDFId(afContractPdfDo.getId());
                map.put("edspaySealDoList", edspaySealDoList);
            }
        }
    }

    private void lender(Map model) {
        DsedResourceDo lenderDo = dsedResourceService.getConfigByTypesAndSecType(DsedResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLenderForCash.getCode());
        model.put("lender", lenderDo.getValue());// 出借人
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
                File file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.userSign(map);
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
                File file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
            }
        }

        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.selfSign(map);
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
                File file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("selfPath")));
                file1.delete();
            }
        }
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.secondSign(map);
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
                File file1 = new File(String.valueOf(map.get("PDFPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("userPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("selfPath")));
                file1.delete();
                file1 = new File(String.valueOf(map.get("secondPath")));
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
                DsedContractPdfDo afContractPdfDo = new DsedContractPdfDo();
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
            File file1 = new File(String.valueOf(map.get("PDFPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("userPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("selfPath")));
            file1.delete();
            file1 = new File(String.valueOf(map.get("secondPath")));
            file1.delete();
        }
        return true;
    }

    private byte[] aldLeaseCreateSeal(boolean result,byte[] stream,Map<String, Object> map){
        try {
            FileDigestSignResult fileDigestSignResult = dsedESdkService.leaseSelfStreamSign(map, stream);//阿拉丁盖章
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

    private void secondSeal(Map map, DsedResourceDo lenderDo, DsedUserDo dsedUserDo) {
        DsedUserSealDo companySealDo = dsedESdkService.selectUserSealByUserId(-1l);
        if (null != companySealDo && null != companySealDo.getUserSeal()) {//ald签章
            map.put("companySelfSeal", companySealDo.getUserSeal());
            map.put("secondAccoundId", companySealDo.getUserAccountId());
        }

        DsedUserSealDo dsedUserSealDo = dsedESdkService.getSealPersonal(dsedUserDo);//借款人签章
        if (null != dsedUserSealDo && null != dsedUserSealDo.getUserSeal() && null != dsedUserSealDo.getUserAccountId()) {
            map.put("personUserSeal", dsedUserSealDo.getUserSeal());
            map.put("accountId", dsedUserSealDo.getUserAccountId());
        }
        if (null != lenderDo ) {//出借人签章
            DsedUserSealDo companyUserSealDo = dsedUserSealDao.selectByUserName(lenderDo.getValue());
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
