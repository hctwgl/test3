package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfESdkService;
import com.ald.fanbei.api.dal.dao.AfUserSealDao;
import com.ald.fanbei.api.dal.domain.AfUserSealDo;
import com.timevale.esign.sdk.tech.bean.OrganizeBean;
import com.timevale.esign.sdk.tech.bean.PersonBean;
import com.timevale.esign.sdk.tech.bean.PosBean;
import com.timevale.esign.sdk.tech.bean.SignPDFFileBean;
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
import com.timevale.esign.sdk.tech.service.EsignsdkService;
import com.timevale.esign.sdk.tech.service.SealService;
import com.timevale.esign.sdk.tech.service.UserSignService;
import com.timevale.esign.sdk.tech.service.factory.AccountServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.EsignsdkServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SealServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.UserSignServiceFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Service("afESdkService")
public class AfESdkServiceImpl implements AfESdkService {

    @Resource
    AfUserSealDao afUserSealDao;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AfESdkServiceImpl.class);

    private EsignsdkService SDK = EsignsdkServiceFactory.instance();
    private SealService SEAL = SealServiceFactory.instance();
    private UserSignService userSign = UserSignServiceFactory.instance();
    private AccountService SERVICE = AccountServiceFactory.instance();

    @Override
    public AddSealResult createSealOrganize(String accountId,
                                             String templateType, String color, String hText, String qText) {

        OrganizeTemplateType template = null;
        SealColor sColor = null;
        for(OrganizeTemplateType organizeTemplateType : OrganizeTemplateType.values()){
            if (templateType.equalsIgnoreCase(organizeTemplateType.template().disc())) {
                template = organizeTemplateType;
                break;
            }
        }
        for(SealColor sealColor : SealColor.values()){
            if (color.equalsIgnoreCase(sealColor.color().getColor())) {
                sColor = sealColor;
                break;
            }
        }
        return SEAL.addTemplateSeal(accountId, template, sColor, hText, qText);
    }
    @Override
    public AddSealResult createSealPersonal( String accountId,
                                             String templateType, String color) {

        PersonTemplateType template = null;
        SealColor sColor = null;
        for(PersonTemplateType personTemplateType : PersonTemplateType.values()){
            if (templateType.equalsIgnoreCase(personTemplateType.template().disc())) {
                template = personTemplateType;
                break;
            }
        }
        for(SealColor sealColor : SealColor.values()){
            if (color.equalsIgnoreCase(sealColor.color().getColor())) {
                sColor = sealColor;
                break;
            }
        }
        return SEAL.addTemplateSeal(accountId, template, sColor);

    }

    @Override
    public FileDigestSignResult sign(HttpServletRequest req, HttpServletResponse response) {
        // 签章数据
        String sealData = req.getParameter("sealData");//签章数据
        // 待签署文档路径
        String srcFile = req.getParameter("srcFile");//待签署文档路径
        logger.debug("sign doc: " + srcFile);
        String dstFile = req.getParameter("dstFile");//签署后文档保存路径
        String fileName = req.getParameter("fileName");//文档显示名字

        String type = req.getParameter("signType");//签章类型
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

        String accountId = req.getParameter("accountId");
        int posX = Integer.valueOf(req.getParameter("posX"));
        int posY = Integer.valueOf(req.getParameter("posY"));
        int posType = Integer.valueOf(req.getParameter("posType"));
        float width = Float.valueOf(req.getParameter("sealWidth"));
        boolean isQrcodeSign = Boolean.valueOf(req.getParameter("isQrcodeSign"));
        String key = req.getParameter("key");
        String posPage = req.getParameter("posPage");
        logger.debug("sign account id: " + accountId);

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
        FileDigestSignResult result = new FileDigestSignResult();
        result.setErrCode(r.getErrCode());
        result.setErrShow(r.isErrShow());
        result.setMsg(r.getMsg());
        result.setSignServiceId(r.getSignServiceId());
        // 使用用户印章签名

//        Tools.response(req, resp, result);
        return null;
    }

    @Override
    public AddAccountResult addPerson(Map<String,String> map) {
        String name = map.get("name");
        String idno = map.get("idno");
        String mobile = map.get("mobile");
        String email = map.get("email");
        int area = 0;
        PersonBean psn = new PersonBean();
        psn.setMobile(mobile).setEmail(email).setName(name).setIdNo(idno)
                .setPersonArea(area);
        AddAccountResult r = SERVICE.addAccount(psn);
        return r;
    }

    @Override
    public GetAccountProfileResult getPerson(Map<String, String> map) {
        String idno = map.get("idno");
        GetAccountProfileResult r = SERVICE.getAccountInfoByIdNo(idno,11);
        return r;
    }


    @Override
    public AfUserSealDo selectUserSealByUserId(Long id) {
        return afUserSealDao.selectByUserId(id);
    }

    @Override
    public AfUserSealDo selectByUserType(String type) {
        return afUserSealDao.selectByUserType(type);
    }

    @Override
    public int insertUserSeal(AfUserSealDo record) {
        return afUserSealDao.insert(record);
    }

    @Override
    public int updateUserSealByUserId(AfUserSealDo record) {
        return afUserSealDao.updateByUserId(record);
    }

    //    @Override
    public AddAccountResult addOrganize(HttpServletRequest req, HttpServletResponse response) {
        String mobile = req.getParameter("mobile");
        String email = req.getParameter("email");
        String name = req.getParameter("name");
        int organType = Integer.valueOf(req.getParameter("organType"));
        String regType = req.getParameter("regType");
        String organCode = req.getParameter("organCode");
        int userType = Integer.valueOf(req.getParameter("userType"));
        String agentName = req.getParameter("agentName");
        String agentIdNo = req.getParameter("agentIdNo");
        String legalName = req.getParameter("legalName");
        String legalIdNo = req.getParameter("legalIdNo");
        int legalArea = Integer.valueOf(req.getParameter("legalArea"));

        OrganRegType organRegType = null;
        if ("0".equalsIgnoreCase(regType)) {
            organRegType = OrganRegType.NORMAL;
        } else if ("1".equalsIgnoreCase(regType)) {
            organRegType = OrganRegType.MERGE;
        } else if ("2".equalsIgnoreCase(regType)){
            organRegType = OrganRegType.REGCODE;
        }

        OrganizeBean org = new OrganizeBean();
        org.setMobile(mobile).setEmail(email).setName(name)
                .setOrganType(organType).setRegType(organRegType)
                .setOrganCode(organCode).setUserType(userType)
                .setAgentName(agentName).setAgentIdNo(agentIdNo)
                .setLegalName(legalName).setLegalIdNo(legalIdNo)
                .setLegalArea(legalArea);

        AddAccountResult r = SERVICE.addAccount(org);
//        Tools.response(req, resp, r);
        return null;
    }
}
