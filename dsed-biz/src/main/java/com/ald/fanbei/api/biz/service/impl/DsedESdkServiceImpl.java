package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.DsedESdkService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.DsedUserSealDao;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.dal.domain.DsedUserSealDo;
import com.timevale.esign.sdk.tech.bean.*;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import com.timevale.esign.sdk.tech.bean.seal.OrganizeTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
import com.timevale.esign.sdk.tech.impl.constants.OrganRegType;
import com.timevale.esign.sdk.tech.impl.constants.SignType;
import com.timevale.esign.sdk.tech.service.AccountService;
import com.timevale.esign.sdk.tech.service.SealService;
import com.timevale.esign.sdk.tech.service.SelfSignService;
import com.timevale.esign.sdk.tech.service.UserSignService;
import com.timevale.esign.sdk.tech.service.factory.AccountServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SealServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SelfSignServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.UserSignServiceFactory;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("afESdkService")
public class DsedESdkServiceImpl implements DsedESdkService {

    public

    @Resource
    DsedUserSealDao afUserSealDao;

    @Resource
    BizCacheUtil bizCacheUtil;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DsedESdkServiceImpl.class);

    private SealService SEAL = SealServiceFactory.instance();
    private UserSignService userSign = UserSignServiceFactory.instance();// 用户签署
    private SelfSignService selfSign = SelfSignServiceFactory.instance();// 平台签署
    private AccountService SERVICE = AccountServiceFactory.instance();

    @Override
    public AddSealResult createSealOrganize(String accountId, String templateType, String color, String hText,
                                            String qText) {

        OrganizeTemplateType template = null;
        SealColor sColor = null;
        for (OrganizeTemplateType organizeTemplateType : OrganizeTemplateType.values()) {
            if (templateType.equalsIgnoreCase(organizeTemplateType.template().disc())) {
                template = organizeTemplateType;
                break;
            }
        }
        for (SealColor sealColor : SealColor.values()) {
            if (color.equalsIgnoreCase(sealColor.color().getColor())) {
                sColor = sealColor;
                break;
            }
        }
        return SEAL.addTemplateSeal(accountId, template, sColor, hText, qText);
    }

    @Override
    public AddSealResult createSealPersonal(String accountId, String templateType, String color) {
        PersonTemplateType template = null;
        SealColor sColor = null;
        for (PersonTemplateType personTemplateType : PersonTemplateType.values()) {
            if (templateType.equalsIgnoreCase(personTemplateType.template().disc())) {
                template = personTemplateType;
                break;
            }
        }
        for (SealColor sealColor : SealColor.values()) {
            if (color.equalsIgnoreCase(sealColor.color().getColor())) {
                sColor = sealColor;
                break;
            }
        }
        AddSealResult r = SEAL.addTemplateSeal(accountId, template, sColor);
        logger.info("esdk createSealPersonal:", r.getMsg()+",accountId = "+accountId);
        return r;

    }

    @Override
    public FileDigestSignResult userStreamSign(Map<String, Object> map, byte[] stream) {
        // 待签署文 档路径
        String srcFile = ObjectUtils.toString(map.get("PDFPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("userPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("personUserSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "（借款人）：").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("userStreamSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFStreamBean streamBean = new SignPDFStreamBean();
        streamBean.setStream(stream);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, streamBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult userSign(Map<String, Object> map) {
        // 待签署文 档路径
        String srcFile = ObjectUtils.toString(map.get("PDFPath"), "").toString();// 待签署文档路径
        String dstFile = ObjectUtils.toString(map.get("userPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("personUserSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1"));
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70"));
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("personKey"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk userSign sign account id = " + accountId+",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult selfOldSign(Map<String, Object> map) {//阿拉丁签章
        // 签章标识
        // int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("userPath"), "").toString();
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("selfPath"), "").toString();
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();
        SignType signType = null;
        String type = ObjectUtils.toString(map.get("signType"), "Multi").toString();
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posX = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "170").toString());
        int posY = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "540").toString());
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "0").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "159").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "5").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setPosX(posX);
        pos.setPosY(posY);
        pos.setPosPage(posPage);
        pos.setKey(key);
        pos.setWidth(width);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setDstPdfFile(dstFile);
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = selfSign.localSignPdf(fileBean, pos, sealId, signType);
        return r;
    }

    @Override
    public FileDigestSignResult secondOldSign(Map<String, Object> map) {//乙方签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Multi").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("secondSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posX = Integer.valueOf(ObjectUtils.toString(map.get("posX"), "420").toString());
        int posY = Integer.valueOf(ObjectUtils.toString(map.get("posY"), "685").toString());
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "0").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "159").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "5").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setPosX(posX);
        pos.setPosY(posY);
        pos.setPosPage(posPage);
        pos.setKey(key);
        pos.setWidth(width);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult userOldSign(Map<String, Object> map) {//甲方签章
        // 待签署文 档路径
        String srcFile = ObjectUtils.toString(map.get("PDFPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("userPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("personUserSeal"), "").toString();// 签章数据
        fileName = "爱上街合同";
        type = "Multi";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posX =Integer.valueOf(ObjectUtils.toString(map.get("posX"), "170").toString());
        int posY = Integer.valueOf(ObjectUtils.toString(map.get("posY"), "685").toString());
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "0").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "159").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "5").toString();
        logger.info("sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setPosX(posX);
        pos.setPosY(posY);
        pos.setPosPage(posPage);
        pos.setKey(key);
        pos.setWidth(width);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult firstPartySign(Map<String, Object> map) {
        // 待签署文 档路径
        String srcFile = ObjectUtils.toString(map.get("PDFPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("userPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("personUserSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), ""));
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), ""));
        String key = ObjectUtils.toString(map.get("firstPartyKey"), "");
        String posPage = ObjectUtils.toString(map.get("posPage"), "5").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk firstPartySign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setPosPage(posPage);
        pos.setKey(key);
        pos.setWidth(width);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult secondPartySign(Map<String, Object> map, byte[] secondStream) {
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("companySelfSeal"), "");// 签章数据
        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "").toString());
        String key = ObjectUtils.toString(map.get("secondPartyKey"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk secondPartySign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
		pos.setPosPage(posPage);
        pos.setKey(key);
        FileDigestSignResult r = new FileDigestSignResult();
        if (secondStream != null && secondStream.length > 0) {
            SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
            signPDFStreamBean.setStream(secondStream);
            r = selfSign.localSignPdf(signPDFStreamBean, pos, 0, signType);
        } else {
            SignPDFFileBean fileBean = new SignPDFFileBean();
            fileBean.setSrcPdfFile(srcFile);
            fileBean.setDstPdfFile(dstFile);
            fileBean.setFileName(fileName);
            r = selfSign.localSignPdf(fileBean, pos, 0, signType);
        }
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult thirdPartySign(Map<String, Object> map) {
        return null;
    }

    @Override
    public FileDigestSignResult secondSign(Map<String, Object> map) {//乙方关键字签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字

        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("secondSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "80").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        logger.info("sdk secondSign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);

        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult secondStreamSign(Map<String, Object> map, byte[] secondStream) {//乙方关键字字节流签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        String sealData = ObjectUtils.toString(map.get("secondSeal"), "").toString();// 签章数据
        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "30").toString());
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        boolean isQrcodeSign = false;
        SignType signType = null;
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk secondStreamSign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        FileDigestSignResult r = new FileDigestSignResult();
        if (secondStream != null && secondStream.length > 0) {
            SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
            signPDFStreamBean.setStream(secondStream);
            r = userSign.localSignPDF(accountId, sealData, signPDFStreamBean, pos, signType);
        } else {
            SignPDFFileBean fileBean = new SignPDFFileBean();
            fileBean.setSrcPdfFile(srcFile);
            fileBean.setDstPdfFile(dstFile);
            fileBean.setFileName(fileName);
            r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        }

        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult thirdSign(Map<String, Object> map) {//钱包签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("thirdPath"), "");// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        String accountId = ObjectUtils.toString(map.get("thirdAccoundId"), "");
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "楚橡信息科技股份有限公司").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("thirdSeal"), "");// 签章数据
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        logger.info("thirdSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setDstPdfFile(dstFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, fileBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult thirdStreamSign(Map<String, Object> map, byte[] stream) {//钱包字节流签章
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("thirdPartyKey"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        String accountId = ObjectUtils.toString(map.get("thirdAccoundId"), "");
        String sealData = ObjectUtils.toString(map.get("thirdSeal"), "");// 签章数据
        SignType signType = null;
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("thirdStreamSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
        signPDFStreamBean.setStream(stream);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, signPDFStreamBean, pos, signType);
        // 使用用户印章签名
        return r;
    }
    @Override
    public FileDigestSignResult selfStreamSign(String fileName,String type,String accountId,int posType,float width,String key,String posPage,boolean isQrcodeSign,byte[] stream) {//钱包字节流签章
        SignType signType = null;
//        fileName = "爱上街合同";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("selfStreamSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
        signPDFStreamBean.setStream(stream);
        FileDigestSignResult r = selfSign.localSignPdf(signPDFStreamBean, pos, 0, signType);
        // 使用用户印章签名
        return r;
    }

    public FileDigestSignResult streamSign(String fileName,String type,String sealData,String accountId,int posType,float width,String key,String posPage,boolean isQrcodeSign,byte[] stream) {//钱包字节流签章
        SignType signType = null;
//        fileName = "爱上街合同";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("streamSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
        signPDFStreamBean.setStream(stream);
        FileDigestSignResult r = userSign.localSignPDF(accountId, sealData, signPDFStreamBean, pos, signType);
        // 使用用户印章签名
        return r;
    }

    @Override
    public FileDigestSignResult selfSign(Map<String, Object> map) {//阿拉丁签章
        // 签章标识
        // int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("userPath"), "").toString();
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("selfPath"), "").toString();
        String fileName = ObjectUtils.toString(map.get("fileName"), "商务股份有限公司").toString();
        SignType signType = null;
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "商务股份有限公司").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        logger.info("selfSign sign account id: " + accountId);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFFileBean fileBean = new SignPDFFileBean();
        fileBean.setDstPdfFile(dstFile);
        fileBean.setSrcPdfFile(srcFile);
        fileBean.setFileName(fileName);
        FileDigestSignResult r = selfSign.localSignPdf(fileBean, pos, sealId, signType);
        return r;
    }

    @Override
    public FileDigestSignResult selfStreamSign(Map<String, Object> map, byte[] stream) {//阿拉丁字节流签章
        // 签章标识
        // int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("userPath"), "").toString();
        logger.info("sign doc: " + srcFile);
        SignType signType = null;
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "商务股份有限公司").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk selfStreamSign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        SignPDFStreamBean streamBean = new SignPDFStreamBean();
        streamBean.setStream(stream);
        FileDigestSignResult r = selfSign.localSignPdf(streamBean, pos, sealId, signType);
        return r;
    }

    @Override
    public FileDigestSignResult leaseSelfStreamSign(Map<String, Object> map, byte[] stream) {//阿拉丁字节流签章
        // 签章标识
        // int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("userPath"), "").toString();
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("selfPath"), "").toString();
        String fileName = ObjectUtils.toString(map.get("fileName"), "爱上街合同").toString();
        SignType signType = null;
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();
        String accountId = ObjectUtils.toString(map.get("accountId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "1").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "70").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("selfKey"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "6").toString();
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        logger.info("sdk selfStreamSign sign account id = " + accountId,",key =" + key + ",borrowId = " + map.get("borrowId")+",srcFile = "+srcFile);
        PosBean pos = new PosBean();
        pos.setPosType(posType);
        pos.setWidth(width);
        pos.setKey(key);
        pos.setQrcodeSign(isQrcodeSign);
        FileDigestSignResult r;
        if (stream != null && stream.length > 0) {
            SignPDFStreamBean signPDFStreamBean = new SignPDFStreamBean();
            signPDFStreamBean.setStream(stream);
            r = selfSign.localSignPdf(signPDFStreamBean, pos, 0, signType);
        } else {
            SignPDFFileBean fileBean = new SignPDFFileBean();
            fileBean.setSrcPdfFile(srcFile);
            fileBean.setDstPdfFile(dstFile);
            fileBean.setFileName(fileName);
            r = selfSign.localSignPdf(fileBean, pos, 0, signType);
        }
        /*SignPDFStreamBean streamBean = new SignPDFStreamBean();
        streamBean.setStream(stream);
        FileDigestSignResult r = selfSign.localSignPdf(streamBean, pos, sealId, signType);*/
        return r;
    }

    @Override
    public AddAccountResult addPerson(Map<String, String> map) {
        String name = map.get("name");
        String idno = map.get("idno");
        String mobile = map.get("mobile");
        String email = map.get("email");
        int area = 0;
        PersonBean psn = new PersonBean();
        psn.setMobile(mobile).setEmail(email).setName(name).setIdNo(idno).setPersonArea(area);
        AddAccountResult r = SERVICE.addAccount(psn);
        logger.info("esdk addPerson:", r, ",name =", name, ",idno =", idno, ",mobile =", mobile, ",email =", email);
        return r;
    }

    @Override
    public GetAccountProfileResult getPerson(Map<String, String> map) {
        String idno = map.get("idno");
        GetAccountProfileResult r = SERVICE.getAccountInfoByIdNo(idno, 11);
        logger.info("esdk getPerson:", r);
        return r;
    }

    @Override
    public DsedUserSealDo selectUserSealByUserId(Long id) {
        return afUserSealDao.selectByUserId(id);
    }

    @Override
    public DsedUserSealDo selectByUserName(String name) {
        return afUserSealDao.selectByUserName(name);
    }

    @Override
    public DsedUserSealDo selectUserSealByCardId(String id) {
        return afUserSealDao.selectByCardId(id);
    }

    @Override
    public List<DsedUserSealDo> selectByUserType(String type) {
        return afUserSealDao.selectByUserType(type);
    }

    @Override
    public int insertUserSeal(DsedUserSealDo record) {
        String key = "insertUserSeal_lock" + record.getUserId();
        boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
        int num = 0;
        if (isNotLock) {
            try {
                num = afUserSealDao.insert(record);
                logger.info("insertUserSeal num => {}", num);
            } catch (Exception e) {
                logger.error("insertUserSeal error => {}", e.getMessage());
            } finally {
                bizCacheUtil.delCache(key);
            }
        }
        return num;
    }

    @Override
    public int updateUserSealByUserId(DsedUserSealDo record) {
        return afUserSealDao.updateByUserId(record);
    }

    @Override
    public DsedUserSealDo getSealPersonal(DsedUserDo dsedUserDo) {

        DsedUserSealDo afUserSealDo = new DsedUserSealDo();
        DsedUserSealDo afUserSealDo1 = new DsedUserSealDo();
        if ("edspay".equals(dsedUserDo.getMajiabaoName()) || dsedUserDo.getRid() == null) {//e都市钱包用户
            afUserSealDo = selectUserSealByCardId(dsedUserDo.getIdNumber());
        } else {
            afUserSealDo = selectUserSealByUserId(dsedUserDo.getRid());
        }
        if (null == afUserSealDo) {// 第一次创建个人印章
            Map<String, String> map = new HashMap<>();
            map.put("name", dsedUserDo.getRealName() == null ? dsedUserDo.getRealName():dsedUserDo.getRealName());
            map.put("idno", dsedUserDo.getIdNumber());
            map.put("email", dsedUserDo.getEmail());
            map.put("mobile", dsedUserDo.getMobile());
            AddAccountResult addAccountResult = addPerson(map);
            if (null != addAccountResult && 150016 == addAccountResult.getErrCode()) {// 账户已存在(该证件号已被使用)
                GetAccountProfileResult getAccountProfileResult = getPerson(map);
                addAccountResult.setAccountId(getAccountProfileResult.getAccountInfo().getAccountUid());
            } else if (null == addAccountResult || !"成功".equals(addAccountResult.getMsg())
                    || 0 != addAccountResult.getErrCode()) {
                logger.error("personAccount create error = > {}",addAccountResult.getMsg() + "name="+dsedUserDo.getRealName());
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            afUserSealDo1.setUserAccountId(addAccountResult.getAccountId());
            if (dsedUserDo.getMajiabaoName() != null && "edspay".equals(dsedUserDo.getMajiabaoName())) {
                afUserSealDo1.setUserType("3");//钱包用户
            } else {
                afUserSealDo1.setUserType("2");//反呗用户
            }
            AddSealResult addSealResult = createSealPersonal(addAccountResult.getAccountId(), "RECTANGLE", "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo1.setUserId(dsedUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
                if (dsedUserDo.getMajiabaoName() != null && "edspay".equals(dsedUserDo.getMajiabaoName())) {
                    afUserSealDo1.setEdspayUserCardId(dsedUserDo.getIdNumber());
                    afUserSealDo1.setUserName(dsedUserDo.getRealName());
                }
                // afUserSealDo1.setUserSeal("data:image/png;base64,"+addSealResult.getSealData());
                // userSeal = addSealResult.getSealData();
            }
            int num = insertUserSeal(afUserSealDo1);
            return afUserSealDo1;
        }

        if (null != afUserSealDo.getUserAccountId() && null == afUserSealDo.getUserSeal()) {// 有账户没印章
            AddSealResult addSealResult = createSealPersonal(afUserSealDo.getUserAccountId().toString(), "SQUARE",
                    "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo1.setUserId(dsedUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
                afUserSealDo1.setUserAccountId(afUserSealDo.getUserAccountId());
                if (dsedUserDo.getMajiabaoName() != null && "edspay".equals(dsedUserDo.getMajiabaoName())) {
                    afUserSealDo1.setEdspayUserCardId(dsedUserDo.getIdNumber());
                    afUserSealDo1.setUserName(dsedUserDo.getRealName());
                }
                afUserSealDao.updateByUserId(afUserSealDo1);
            }
            return afUserSealDo1;
        }
        return afUserSealDo;
    }

    @Override
    public void getSeal(ModelMap model, DsedUserDo dsedUserDo) {
        try {
            DsedUserSealDo companyUserSealDo = selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()) {
                model.put("CompanyUserSeal", "data:image/png;base64," + companyUserSealDo.getUserSeal());
            }
            DsedUserSealDo afUserSealDo = getSealPersonal(dsedUserDo);
            if (null != afUserSealDo && null != afUserSealDo.getUserSeal()) {
                model.put("personUserSeal", "data:image/png;base64," + afUserSealDo.getUserSeal());
            }
        } catch (Exception e) {
            logger.error("UserSeal create error", e);
        }

    }

}
