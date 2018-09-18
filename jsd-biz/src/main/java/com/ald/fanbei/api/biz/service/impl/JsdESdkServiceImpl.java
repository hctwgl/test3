package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.JsdESdkService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.dal.dao.JsdUserSealDao;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;
import com.timevale.esign.sdk.tech.bean.PersonBean;
import com.timevale.esign.sdk.tech.bean.PosBean;
import com.timevale.esign.sdk.tech.bean.SignPDFFileBean;
import com.timevale.esign.sdk.tech.bean.SignPDFStreamBean;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.FileDigestSignResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import com.timevale.esign.sdk.tech.bean.seal.OrganizeTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.PersonTemplateType;
import com.timevale.esign.sdk.tech.bean.seal.SealColor;
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

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("jsdESdkService")
public class JsdESdkServiceImpl implements JsdESdkService {

    @Resource
    JsdUserSealDao afUserSealDao;

    @Resource
    BizCacheUtil bizCacheUtil;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JsdESdkServiceImpl.class);

    private SealService SEAL = SealServiceFactory.instance();
    private UserSignService userSign = UserSignServiceFactory.instance();// 用户签署
    private SelfSignService selfSign = SelfSignServiceFactory.instance();// 平台签署
    private AccountService SERVICE = AccountServiceFactory.instance();

    @Override
    public AddSealResult createOrganizeSeal(String accountId, String templateType, String color, String hText,
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
    public AddSealResult createUserSeal(String accountId, String templateType, String color) {
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
        logger.info("esdk createSealPersonal:", r.getMsg() + ",accountId = " + accountId);
        return r;

    }

    @Override
    public FileDigestSignResult userStreamSign(Map<String, Object> map, byte[] stream) {
        // 待签署文 档路径
        String srcFile = ObjectUtils.toString(map.get("PDFPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("userPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("personUserSeal"), "").toString();// 签章数据
        fileName = "反呗合同";
        type = "Key";
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
        int posType = 1;
        int width = 70;
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        key = "（借款人）：";
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        logger.info("sign account id: " + accountId);
        posPage = "6";
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
    public FileDigestSignResult secondPartySign(Map<String, Object> map, byte[] secondStream) {
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();// 文档显示名字
        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("companySelfSeal"), "");// 签章数据
        fileName = "反呗合同";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }
        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "").toString());
        String key = ObjectUtils.toString(map.get("secondPartyKey"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        logger.info("sdk secondPartySign sign account id = " + accountId, ",key =" + key + ",borrowId = " + map.get("borrowId") + ",srcFile = " + srcFile);
        posPage = "6";
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
    public FileDigestSignResult secondStreamSign(Map<String, Object> map, byte[] secondStream) {//乙方关键字字节流签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("selfPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();// 文档显示名字

        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("secondSeal"), "").toString();// 签章数据
        fileName = "反呗合同";
        type = "Key";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        String accountId = ObjectUtils.toString(map.get("secondAccoundId"), "").toString();
        int posType = 1;
        int width = 30;
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        logger.info("sdk secondStreamSign sign account id = " + accountId, ",key =" + key + ",borrowId = " + map.get("borrowId") + ",srcFile = " + srcFile);
        posPage = "6";
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
    public FileDigestSignResult thirdStreamSign(Map<String, Object> map, byte[] stream) {//钱包字节流签章
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("secondPath"), "").toString();// 待签署文档路径
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("thirdPath"), "");// 签署后文档保存路径
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();// 文档显示名字

        String type = ObjectUtils.toString(map.get("signType"), "").toString();// 签章类型
        SignType signType = null;
        String sealData = ObjectUtils.toString(map.get("thirdSeal"), "");// 签章数据
        fileName = "反呗合同";
        type = "Key";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        String accountId = ObjectUtils.toString(map.get("thirdAccoundId"), "");

        int posType = 1;
        int width = 70;

        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        key = "楚橡信息科技股份有限公司";
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        logger.info("sign account id: " + accountId);
        posPage = "6";
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
    public FileDigestSignResult selfStreamSign(Map<String, Object> map, byte[] stream) {//阿拉丁字节流签章
        // 签章标识
        // int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = ObjectUtils.toString(map.get("userPath"), "").toString();
        logger.info("sign doc: " + srcFile);
        String dstFile = ObjectUtils.toString(map.get("selfPath"), "").toString();
        String fileName = ObjectUtils.toString(map.get("fileName"), "").toString();
        SignType signType = null;
        String type = ObjectUtils.toString(map.get("signType"), "").toString();
        type = "Key";
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
        int posType = 1;
        int width = 70;
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("key"), "").toString();
        key = "商务股份有限公司";
        String posPage = ObjectUtils.toString(map.get("posPage"), "").toString();
        logger.info("sdk selfStreamSign sign account id = " + accountId, ",key =" + key + ",borrowId = " + map.get("borrowId") + ",srcFile = " + srcFile);
        fileName = "反呗合同";
        posPage = "6";
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
    public AddAccountResult addUserSealAccount(Map<String, String> map) {
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
    public GetAccountProfileResult getUserSealAccount(Map<String, String> map) {
        String idno = map.get("idno");
        GetAccountProfileResult r = SERVICE.getAccountInfoByIdNo(idno, 11);
        logger.info("esdk getPerson:", r);
        return r;
    }

    @Override
    public JsdUserSealDo selectUserSealByUserId(Long id) {
        return afUserSealDao.selectByUserId(id);
    }

    @Override
    public JsdUserSealDo selectUserSealByCardId(String id) {
        return afUserSealDao.selectByCardId(id);
    }

    @Override
    public List<JsdUserSealDo> selectByUserType(String type) {
        return afUserSealDao.selectByUserType(type);
    }
}
