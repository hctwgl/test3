package com.ald.fanbei.api.web.application;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.ald.fanbei.api.biz.service.JsdESdkService;
import com.ald.fanbei.api.biz.service.JsdUserSealService;
import com.ald.fanbei.api.common.EsignPublicInit;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;
import com.timevale.esign.sdk.tech.bean.OrganizeBean;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import com.timevale.esign.sdk.tech.bean.result.Result;
import com.timevale.esign.sdk.tech.impl.constants.OrganRegType;
import com.timevale.esign.sdk.tech.service.AccountService;
import com.timevale.esign.sdk.tech.service.EsignsdkService;
import com.timevale.esign.sdk.tech.service.factory.AccountServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.EsignsdkServiceFactory;
import com.timevale.tech.sdk.bean.HttpConnectionConfig;
import com.timevale.tech.sdk.bean.ProjectConfig;
import com.timevale.tech.sdk.bean.SignatureConfig;
import com.timevale.tech.sdk.constants.AlgorithmType;
import com.timevale.tech.sdk.constants.HttpType;

/**
 * @author honghzengpei 2017/10/20 14:38
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SpringInit implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SpringInit.class);
    private EsignsdkService SDK = EsignsdkServiceFactory.instance();
    private AccountService SERVICE = AccountServiceFactory.instance();
    @Resource
    private JsdESdkService afESdkService;
    @Resource
    private JsdUserSealService jsdUserSealService;
    @Resource
    private EsignPublicInit esignPublicInit;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getSource();
        String projectId = esignPublicInit.getProjectId();
        String projectSecret = esignPublicInit.getProjectSecret();
        String apisUrl = esignPublicInit.getApisUrl();
        String proxyIp = esignPublicInit.getProxyIp();
        String port = esignPublicInit.getProxyPort();
        int proxyPort = 0;
        if (null != port && !port.equals("")) {
            proxyPort = Integer.valueOf(port);
        }
        String httpType = esignPublicInit.getHttpType();
        int retry = Integer.valueOf(esignPublicInit.getRetry());
        String algorithm = esignPublicInit.getAlgorithm();
        String esignPublicKey = esignPublicInit.getEsignPublicKey();
        String privateKey = esignPublicInit.getPrivateKey();
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setItsmApiUrl(apisUrl);
        projectConfig.setProjectId(projectId);
        projectConfig.setProjectSecret(projectSecret);
        HttpConnectionConfig httpConfig = new HttpConnectionConfig();
        if (httpType.equalsIgnoreCase(HttpType.HTTP.type())) {
            httpConfig.setHttpType(HttpType.HTTP);
        }
        httpConfig.setProxyIp(proxyIp);
        httpConfig.setProxyPort(proxyPort);
        httpConfig.setRetry(retry);
        SignatureConfig signConfig = new SignatureConfig();
        if (algorithm.equalsIgnoreCase(AlgorithmType.RSA.type())) {
            signConfig.setAlgorithm(AlgorithmType.RSA);
            signConfig.setPrivateKey(privateKey);
            signConfig.setEsignPublicKey(esignPublicKey);
        }
        Result result = SDK.init(projectConfig, httpConfig, signConfig);
        if ("成功".equals(result.getMsg()) && 0 == result.getErrCode()) {
            JsdUserSealDo afUserSealDo = afESdkService.selectUserSealByUserId(-1l);//浙江阿拉丁电子商务股份有限公司
            Map map1 = new HashMap();
            map1.put("name", "浙江阿拉丁电子商务股份有限公司");
            map1.put("organCode", "341932957");
            map1.put("userId", -1l);
            createCompanySeal(afUserSealDo, map1);
            JsdUserSealDo mxSeal = afESdkService.selectUserSealByUserId(-2l);//浙江名信信息科技有限公司
            map1.put("name", "浙江名信信息科技有限公司");
            map1.put("organCode", "MA27WPFA7");
            map1.put("userId", -2l);
            createCompanySeal(mxSeal, map1);
            JsdUserSealDo mhSeal = afESdkService.selectUserSealByUserId(-3l);//浙江名恒投资管理有限公司
            map1.put("name", "浙江名恒投资管理有限公司");
            map1.put("organCode", "095715972");
            map1.put("userId", -3l);
            createCompanySeal(mhSeal, map1);
            JsdUserSealDo cxSeal = afESdkService.selectUserSealByUserId(-4l);//浙江楚橡信息科技有限公司
            map1.put("name", "浙江楚橡信息科技有限公司");
            map1.put("organCode", "328203207");
            map1.put("userId", -4l);
            createCompanySeal(cxSeal, map1);
            JsdUserSealDo jtSeal = afESdkService.selectUserSealByUserId(-5l);//金泰嘉鼎（深圳）资产管理有限公司
            map1.put("name", "金泰嘉鼎（深圳）资产管理有限公司");
            map1.put("organCode", "MA5DBLHE8");
            map1.put("userId", -5l);
            createCompanySeal(jtSeal, map1);
            JsdUserSealDo lySeal = afESdkService.selectUserSealByUserId(-6l);//杭州绿游网络科技有限公司
            map1.put("name", "杭州绿游网络科技有限公司");
            map1.put("organCode", "067894205");
            map1.put("userId", -6l);
            createCompanySeal(lySeal, map1);
            JsdUserSealDo qhSeal = afESdkService.selectUserSealByUserId(-7l);//深圳前海资生管家互联网金融服务有限公司
            map1.put("name", "深圳前海资生管家互联网金融服务有限公司");
            map1.put("organCode", "335417385");
            map1.put("userId", -7l);
            createCompanySeal(qhSeal, map1);
            JsdUserSealDo lxSeal = afESdkService.selectUserSealByUserId(-8l);//深圳前海资生管家互联网金融服务有限公司
            map1.put("name", "杭州朗下网络科技有限公司");
            map1.put("organCode", "MA2CCCMG0");
            map1.put("userId", -8l);
            createCompanySeal(lxSeal, map1);
            JsdUserSealDo tgSeal = afESdkService.selectUserSealByUserId(-9l);//深圳前海资生管家互联网金融服务有限公司
            map1.put("name", "浙江弹个指网络科技有限公司");
            map1.put("organCode", "MA2CCJE40");
            map1.put("userId", -9l);
            createCompanySeal(tgSeal, map1);

        }
        logger.info("初始化执行完成...projectId =>{},projectSecret=>{}", projectId, projectSecret);
    }

    @SuppressWarnings("rawtypes")
	private void createCompanySeal(JsdUserSealDo afUserSealDo, Map map) {
        if (null == afUserSealDo || null == afUserSealDo.getUserAccountId()) {// 账户标识
            String accountId = addOrganize(map);
            AddSealResult addSealResult = new AddSealResult();
            if (null != accountId) {
                addSealResult = afESdkService.createOrganizeSeal(accountId,
                        "STAR", "RED", "", "");
            } else {
                logger.error("company seal error =>{}", addSealResult.getMsg());
                throw new BizException(BizExceptionCode.COMPANY_SIGN_ACCOUNT_CREATE_FAILED);
            }
            JsdUserSealDo afUserSealDo1 = new JsdUserSealDo();
            afUserSealDo1.setUserAccountId(accountId);
            afUserSealDo1.setUserType("1");
            afUserSealDo1.setUserId((Long) map.get("userId"));
            afUserSealDo1.setUserName((String) map.get("name"));
            if (null != addSealResult.getSealData() || "" != addSealResult.getSealData()) {
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
            } else {
                logger.error("company seal error =>{}", addSealResult.getMsg());
                throw new BizException(BizExceptionCode.COMPANY_SIGN_ACCOUNT_CREATE_FAILED);
            }
            jsdUserSealService.insertUserSeal(afUserSealDo1);
        } else if (null == afUserSealDo.getUserSeal() || "" == afUserSealDo.getUserSeal()) {//公司的印章base64
            AddSealResult addSealResult = afESdkService.createOrganizeSeal(afUserSealDo.getUserAccountId(),
                    "STAR", "RED", "", "");
            JsdUserSealDo afUserSealDo1 = new JsdUserSealDo();
            afUserSealDo1.setId(afUserSealDo1.getId());
            afUserSealDo1.setUserId((Long) map.get("userId"));
            afUserSealDo1.setUserName((String) map.get("name"));
            if (null != addSealResult.getSealData() || "" != addSealResult.getSealData()) {
                afUserSealDo1.setUserSeal(addSealResult.getSealData());
            } else {
                logger.error("company seal error =>{}", addSealResult.getMsg());
                throw new BizException(BizExceptionCode.COMPANY_SIGN_ACCOUNT_CREATE_FAILED);
            }
            jsdUserSealService.updateUserSealByUserId(afUserSealDo1);
        }
    }

    @SuppressWarnings("rawtypes")
	public String addOrganize(Map map) {
        String name = map.get("name").toString();
        int organType = 0;
        String regType = "0";
        String organCode = map.get("organCode").toString();
        int userType = 0;
        String agentName = "";
        String agentIdNo = "";
        String legalName = "";
        String legalIdNo = "";
        int legalArea = 0;
        OrganRegType organRegType = null;
        if ("0".equalsIgnoreCase(regType)) {
            organRegType = OrganRegType.NORMAL;
        } else if ("1".equalsIgnoreCase(regType)) {
            organRegType = OrganRegType.MERGE;
        } else if ("2".equalsIgnoreCase(regType)) {
            organRegType = OrganRegType.REGCODE;
        }
        OrganizeBean org = new OrganizeBean();
        org.setName(name)
                .setOrganType(organType).setRegType(organRegType)
                .setOrganCode(organCode).setUserType(userType)
                .setAgentName(agentName).setAgentIdNo(agentIdNo)
                .setLegalName(legalName).setLegalIdNo(legalIdNo)
                .setLegalArea(legalArea);
        AddAccountResult r = SERVICE.addAccount(org);
        if (150016 == r.getErrCode() || r.getMsg().contains("账户已存在")) {
            if ("浙江楚橡信息科技有限公司".equals(name)) {
                JsdUserSealDo mhSeal = afESdkService.selectUserSealByUserId(-4l);
                SERVICE.deleteAccount(mhSeal.getUserAccountId());
                r = SERVICE.addAccount(org);
                return r.getAccountId();
            }
            GetAccountProfileResult getAccountProfileResult = SERVICE.getAccountInfoByIdNo(legalIdNo, 11);
            return getAccountProfileResult.getAccountInfo().getAccountUid();
        }
//        Tools.response(req, resp, r);
        return r.getAccountId();
    }

}
