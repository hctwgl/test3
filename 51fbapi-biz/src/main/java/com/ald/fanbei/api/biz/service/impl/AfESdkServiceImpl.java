package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.ald.fanbei.api.biz.service.AfESdkService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserSealDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserSealDo;
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
import com.timevale.esign.sdk.tech.impl.constants.SignType;
import com.timevale.esign.sdk.tech.service.AccountService;
import com.timevale.esign.sdk.tech.service.SealService;
import com.timevale.esign.sdk.tech.service.SelfSignService;
import com.timevale.esign.sdk.tech.service.UserSignService;
import com.timevale.esign.sdk.tech.service.factory.AccountServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SealServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SelfSignServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.UserSignServiceFactory;

@Service("afESdkService")
public class AfESdkServiceImpl implements AfESdkService {

    @Resource
    AfUserSealDao afUserSealDao;
    
    @Resource
    BizCacheUtil bizCacheUtil;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AfESdkServiceImpl.class);

    private SealService SEAL = SealServiceFactory.instance();
    private UserSignService userSign = UserSignServiceFactory.instance();//用户签署
    private SelfSignService selfSign = SelfSignServiceFactory.instance();//平台签署
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
        AddSealResult r = SEAL.addTemplateSeal(accountId, template, sColor);
        logger.info("esdk createSealPersonal:",r);
        return r;

    }

    @Override
    public FileDigestSignResult userSign(Map<String,String> map) {
        // 待签署文 档路径
        String srcFile = map.get("PDFPath");//待签署文档路径
        logger.debug("sign doc: " + srcFile);
        String dstFile = map.get("userPath");//签署后文档保存路径
        String fileName = map.get("fileName");//文档显示名字
        String type = map.get("signType");//签章类型
        SignType signType = null;
        String sealData = map.get("personUserSeal");//签章数据
//        srcFile = "F:/doc/t.pdf";
        fileName = "反呗合同";
        type = "Multi";
//        dstFile = "F:/doc/m2.pdf";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        String accountId = map.get("accountId");
//        accountId = "57FD6990CE904C84A212A6D81E16213A";
//        int posX = Integer.valueOf(map.get("posX"));
//        int posY = Integer.valueOf(map.get("posY"));
//        int posType = Integer.valueOf(map.get("posType"));
//        float width = Float.valueOf(map.get("sealWidth"));
//        boolean isQrcodeSign = Boolean.valueOf(map.get("isQrcodeSign"));
        int posX = 170;
        int posY = 685;
        int posType = 0;
        int width = 159;
        boolean isQrcodeSign = false;
        String key = map.get("key");
        String posPage = map.get("posPage");
        logger.debug("sign account id: " + accountId);
        posPage = "5";
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
    public FileDigestSignResult secondSign(Map<String,String> map) {
        // 待签署文档路径
        String srcFile = map.get("selfPath");//待签署文档路径
        logger.debug("sign doc: " + srcFile);
        String dstFile = map.get("secondPath");//签署后文档保存路径
        String fileName = map.get("fileName");//文档显示名字

        String type = map.get("signType");//签章类型
        SignType signType = null;
        String sealData = map.get("secondSeal");//签章数据
//        srcFile = "F:/doc/t.pdf";
        fileName = "反呗合同";
        type = "Multi";
//        dstFile = "F:/doc/m2.pdf";
        if ("Single".equalsIgnoreCase(type)) {
            signType = SignType.Single;
        } else if ("Multi".equalsIgnoreCase(type)) {
            signType = SignType.Multi;
        } else if ("Edges".equalsIgnoreCase(type)) {
            signType = SignType.Edges;
        } else if ("Key".equalsIgnoreCase(type)) {
            signType = SignType.Key;
        }

        String accountId = map.get("secondAccoundId");
//        accountId = "57FD6990CE904C84A212A6D81E16213A";
//        int posX = Integer.valueOf(map.get("posX"));
//        int posY = Integer.valueOf(map.get("posY"));
//        int posType = Integer.valueOf(map.get("posType"));
//        float width = Float.valueOf(map.get("sealWidth"));
//        boolean isQrcodeSign = Boolean.valueOf(map.get("isQrcodeSign"));
        int posX = 420;
        int posY = 685;
        int posType = 0;
        int width = 159;
        boolean isQrcodeSign = false;
        String key = map.get("key");
        String posPage = map.get("posPage");
        logger.debug("sign account id: " + accountId);
        posPage = "5";
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
    public FileDigestSignResult selfSign(Map<String, String> map) {
        // 签章标识
//        int sealId = Integer.valueOf(map.get("sealId"));//签署印章的标识，为0表示用默认印章签署
        int sealId = 0;
        // 待签署文档路径
        String srcFile = map.get("userPath");
        logger.debug("sign doc: " + srcFile);
        String dstFile = map.get("selfPath");
        String fileName = map.get("fileName");
        SignType signType = null;
        String type = map.get("signType");
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
        String accountId = map.get("accountId");
       /* int posX = Integer.valueOf(map.get("posX"));
        int posY = Integer.valueOf(map.get("posY"));
        int posType = Integer.valueOf(map.get("posType"));
        float width = Float.valueOf(map.get("sealWidth"));
        boolean isQrcodeSign = Boolean.valueOf(map.get("isQrcodeSign"));*/
        int posX = 170;
        int posY = 540;
        int posType = 0;
        int width = 159;
        boolean isQrcodeSign = false;
        String key = map.get("key");
        String posPage = map.get("posPage");
        logger.debug("sign account id: " + accountId);
//        srcFile = "F:/doc/m2.pdf";
        fileName = "反呗合同";
//        dstFile = "F:/doc/mm.pdf";
        posPage = "5";
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
        logger.info("esdk addPerson:",r);
        return r;
    }

    @Override
    public GetAccountProfileResult getPerson(Map<String, String> map) {
        String idno = map.get("idno");
        GetAccountProfileResult r = SERVICE.getAccountInfoByIdNo(idno,11);
        logger.info("esdk getPerson:",r);
        return r;
    }


    @Override
    public AfUserSealDo selectUserSealByUserId(Long id) {
        return afUserSealDao.selectByUserId(id);
    }

    @Override
    public List<AfUserSealDo> selectByUserType(String type) {
        return afUserSealDao.selectByUserType(type);
    }

    @Override
    public int insertUserSeal(AfUserSealDo record) {
    	boolean isNotLock = bizCacheUtil.getLockTryTimes("insertUserSeal_lock", "1", 1000);
    	int num = 0;
		if (isNotLock) {
			try {
				num =  afUserSealDao.insert(record);
			} catch (Exception e) {
				logger.error("insertUserSeal error => {}",e.getMessage()) ;
			} finally {
				bizCacheUtil.delCache("insertUserSeal_lock");
			}
		}
		return num;
		
    }

    @Override
    public int updateUserSealByUserId(AfUserSealDo record) {
        return afUserSealDao.updateByUserId(record);
    }

    @Override
    public AfUserSealDo getSealPersonal(AfUserDo afUserDo, AfUserAccountDo accountDo) {
        
        AfUserSealDo afUserSealDo = selectUserSealByUserId(afUserDo.getRid());
        AfUserSealDo afUserSealDo1 = new AfUserSealDo();
        if (null == afUserSealDo){//第一次创建个人印章
            Map<String,String> map = new HashMap<>();
            map.put("name",afUserDo.getRealName());
            map.put("idno",accountDo.getIdNumber());
            map.put("email",afUserDo.getEmail());
            map.put("mobile",afUserDo.getMobile());
            AddAccountResult addAccountResult = addPerson(map);
            if (null != addAccountResult && 150016 == addAccountResult.getErrCode() ){//账户已存在(该证件号已被使用)
                GetAccountProfileResult getAccountProfileResult = getPerson(map);
                addAccountResult.setAccountId(getAccountProfileResult.getAccountInfo().getAccountUid());
            }else if (null == addAccountResult || !"成功".equals(addAccountResult.getMsg()) || 0 !=addAccountResult.getErrCode()){
                logger.error("personAccount create error" );
                throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            }
            afUserSealDo1.setUserAccountId(addAccountResult.getAccountId());
            afUserSealDo1.setUserType("2");
            AddSealResult addSealResult = createSealPersonal( addAccountResult.getAccountId(), "RECTANGLE", "RED");
            if (!addSealResult.isErrShow()){
                afUserSealDo1.setUserId(afUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
//                afUserSealDo1.setUserSeal("data:image/png;base64,"+addSealResult.getSealData());
//                userSeal = addSealResult.getSealData();
            }
            int num = insertUserSeal(afUserSealDo1);
            return afUserSealDo1;
        }else if (null != afUserSealDo.getUserAccountId() && null == afUserSealDo.getUserSeal()){//有账户没印章
            AddSealResult addSealResult = createSealPersonal( afUserSealDo.getUserAccountId().toString(), "SQUARE", "RED");
            if (!addSealResult.isErrShow()){
                afUserSealDo1.setUserId(afUserDo.getRid());
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
                afUserSealDo1.setUserAccountId(afUserSealDo.getUserAccountId());
//                userSeal = addSealResult.getSealData();
                int num = afUserSealDao.updateByUserId(afUserSealDo1);
            }
            return afUserSealDo1;
        }
        return afUserSealDo;
    }

    @Override
    public void GetSeal(ModelMap model, AfUserDo afUserDo, AfUserAccountDo accountDo) {
        try {
            AfUserSealDo companyUserSealDo = selectUserSealByUserId(-1l);
            if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
                model.put("CompanyUserSeal","data:image/png;base64," + companyUserSealDo.getUserSeal());
            }
            AfUserSealDo afUserSealDo = getSealPersonal(afUserDo, accountDo);
            if (null != afUserSealDo && null != afUserSealDo.getUserSeal()){
                model.put("personUserSeal","data:image/png;base64,"+afUserSealDo.getUserSeal());
            }
        }catch (Exception e){
            logger.error("UserSeal create error",e);
        }

    }
}
