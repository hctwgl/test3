package com.ald.fanbei.api.biz.service.impl;

import java.net.BindException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Service;

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
    public JsdUserSealDo getSealPersonal(JsdUserDo jsdUserDo) throws BindException {

        JsdUserSealDo afUserSealDo = new JsdUserSealDo();
        JsdUserSealDo afUserSealDo1 = new JsdUserSealDo();
        if ("edspay".equals(jsdUserDo.getMajiabaoName()) || jsdUserDo.getRid() == null) {//e都市钱包用户
            afUserSealDo = selectUserSealByCardId(jsdUserDo.getIdNumber());
        } else {
            afUserSealDo = selectUserSealByUserId(jsdUserDo.getRid());
        }
        if (null == afUserSealDo) {// 第一次创建个人印章
            Map<String, String> map = new HashMap<>();
            map.put("name", jsdUserDo.getRealName() == null ? jsdUserDo.getRealName():jsdUserDo.getRealName());
            map.put("idno", jsdUserDo.getIdNumber());
            map.put("email", jsdUserDo.getEmail());
            map.put("mobile", jsdUserDo.getMobile());
            AddAccountResult addAccountResult = addUserSealAccount(map);
            if (null != addAccountResult && 150016 == addAccountResult.getErrCode()) {// 账户已存在(该证件号已被使用)
                GetAccountProfileResult getAccountProfileResult = getUserSealAccount(map);
                addAccountResult.setAccountId(getAccountProfileResult.getAccountInfo().getAccountUid());
            } else if (null == addAccountResult || !"成功".equals(addAccountResult.getMsg())
                    || 0 != addAccountResult.getErrCode()) {
                logger.error("personAccount create error = > {}",addAccountResult.getMsg() + "name="+jsdUserDo.getRealName());
                throw new BindException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR.getDesc());
            }
            afUserSealDo1.setUserAccountId(addAccountResult.getAccountId());
            if (jsdUserDo.getMajiabaoName() != null && "edspay".equals(jsdUserDo.getMajiabaoName())) {
                afUserSealDo1.setUserType("3");//钱包用户
            } else {
                afUserSealDo1.setUserType("2");//反呗用户
            }
            AddSealResult addSealResult = createUserSeal(addAccountResult.getAccountId(), "RECTANGLE", "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo1.setUserId(jsdUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
                if (jsdUserDo.getMajiabaoName() != null && "edspay".equals(jsdUserDo.getMajiabaoName())) {
                    afUserSealDo1.setEdspayUserCardId(jsdUserDo.getIdNumber());
                    afUserSealDo1.setUserName(jsdUserDo.getRealName());
                }
                // afUserSealDo1.setUserSeal("data:image/png;base64,"+addSealResult.getSealData());
                // userSeal = addSealResult.getSealData();
            }
            int num = insertUserSeal(afUserSealDo1);
            return afUserSealDo1;
        }

        if (null != afUserSealDo.getUserAccountId() && null == afUserSealDo.getUserSeal()) {// 有账户没印章
            AddSealResult addSealResult = createUserSeal(afUserSealDo.getUserAccountId().toString(), "SQUARE",
                    "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo1.setUserId(jsdUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
                afUserSealDo1.setUserAccountId(afUserSealDo.getUserAccountId());
                if (jsdUserDo.getMajiabaoName() != null && "edspay".equals(jsdUserDo.getMajiabaoName())) {
                    afUserSealDo1.setEdspayUserCardId(jsdUserDo.getIdNumber());
                    afUserSealDo1.setUserName(jsdUserDo.getRealName());
                }
                afUserSealDao.updateByUserId(afUserSealDo1);
            }
            return afUserSealDo1;
        }
        return afUserSealDo;
    }

    @Override
    public int insertUserSeal(JsdUserSealDo record) {
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
    public FileDigestSignResult thirdStreamSign(Map<String, Object> map, byte[] stream) {//钱包字节流签章
        String type = ObjectUtils.toString(map.get("signType"), "Key").toString();// 签章类型
        int posType = Integer.valueOf(ObjectUtils.toString(map.get("posType"), "").toString());
        float width = Float.valueOf(ObjectUtils.toString(map.get("sealWidth"), "").toString());
        boolean isQrcodeSign = false;
        String key = ObjectUtils.toString(map.get("thirdPartyKey"), "").toString();
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

}
