package com.ald.fanbei.api.web.application;

import com.ald.fanbei.api.biz.service.AfESdkService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.dal.domain.AfUserSealDo;
import com.timevale.esign.sdk.tech.bean.OrganizeBean;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import com.timevale.esign.sdk.tech.bean.result.Result;
import com.timevale.esign.sdk.tech.impl.constants.OrganRegType;
import com.timevale.esign.sdk.tech.service.AccountService;
import com.timevale.esign.sdk.tech.service.EsignsdkService;
import com.timevale.esign.sdk.tech.service.SealService;
import com.timevale.esign.sdk.tech.service.factory.AccountServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.EsignsdkServiceFactory;
import com.timevale.esign.sdk.tech.service.factory.SealServiceFactory;
import com.timevale.tech.sdk.bean.HttpConnectionConfig;
import com.timevale.tech.sdk.bean.ProjectConfig;
import com.timevale.tech.sdk.bean.SignatureConfig;
import com.timevale.tech.sdk.constants.AlgorithmType;
import com.timevale.tech.sdk.constants.HttpType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author honghzengpei 2017/10/20 14:38
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SpringInit implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SpringInit.class);
    private EsignsdkService SDK = EsignsdkServiceFactory.instance();
    private AccountService SERVICE = AccountServiceFactory.instance();
    private SealService SEAL = SealServiceFactory.instance();
    @Resource
    private AfResourceService afResourceService;
    @Resource
    private AfESdkService afESdkService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getSource();
        Map<String,String> map = new HashMap<>();
        map.put("projectId","1111563517");
        map.put("projectSecret","95439b0863c241c63a861b87d1e647b7");
        map.put("apisUrl","http://121.40.164.61:8080/tgmonitor/rest/app!getAPIInfo2");
        map.put("proxyIp","");
        map.put("proxyPort","");
        map.put("httpType","https");
        map.put("retry","5");
        map.put("algorithm","HMAC-SHA256");
        map.put("esignPublicKey","");
        map.put("privateKey","");
        String projectId = map.get("projectId");
        String projectSecret = map.get("projectSecret");
        String apisUrl = map.get("apisUrl");
        String proxyIp = map.get("proxyIp");
        String port = map.get("proxyPort");
        int proxyPort = 0;
        if(null != port && !port.equals("")){
            proxyPort = Integer.valueOf(port);
        }
        String httpType = map.get("httpType");
        int retry = Integer.valueOf(map.get("retry"));
        String algorithm = map.get("algorithm");
        String esignPublicKey = map.get("esignPublicKey");
        String privateKey = map.get("privateKey");
        ProjectConfig projectConfig = new ProjectConfig();
        projectConfig.setItsmApiUrl(apisUrl);
        projectConfig.setProjectId(projectId);
        projectConfig.setProjectSecret(projectSecret);
        HttpConnectionConfig httpConfig = new HttpConnectionConfig();
        if(httpType.equalsIgnoreCase(HttpType.HTTP.type())){
            httpConfig.setHttpType(HttpType.HTTP);
        }
        httpConfig.setProxyIp(proxyIp);
        httpConfig.setProxyPort(proxyPort);
        httpConfig.setRetry(retry);
        SignatureConfig signConfig = new SignatureConfig();
        if(algorithm.equalsIgnoreCase(AlgorithmType.RSA.type())){
            signConfig.setAlgorithm(AlgorithmType.RSA);
            signConfig.setPrivateKey(privateKey);
            signConfig.setEsignPublicKey(esignPublicKey);
        }
        Result result = SDK.init(projectConfig, httpConfig, signConfig);
        if ("成功".equals(result.getMsg()) && 0 == result.getErrCode()){
            AfUserSealDo afUserSealDo = afESdkService.selectByUserType("1");
            if (null == afUserSealDo || null == afUserSealDo.getUserAccountId()){// 账户标识
                String accountId = addOrganize();
                AddSealResult addSealResult = new AddSealResult();
                if(null != accountId){
                    addSealResult = afESdkService.createSealOrganize(accountId,
                            "STAR", "RED", "横向文内容", "下弦文内容");
                }else {
                    logger.error("e签宝创建公司账户失败:");
                }
                AfUserSealDo afUserSealDo1 = new AfUserSealDo();
                afUserSealDo1.setUserAccountId(accountId);
                afUserSealDo1.setUserType("1");
                if (null != addSealResult.getSealData() || "" != addSealResult.getSealData()){
                    afUserSealDo1.setUserSeal("data:image/png;base64," + addSealResult.getSealData());
                }else {
                    logger.error("account error:",addSealResult.getMsg());
                }
                afESdkService.insertUserSeal(afUserSealDo1);
            }else if (null == afUserSealDo.getUserSeal() || "" == afUserSealDo.getUserSeal()){//公司的印章base64
                AddSealResult addSealResult = afESdkService.createSealOrganize(afUserSealDo.getUserAccountId(),
                        "STAR", "RED", "横向文内容", "下弦文内容");
                AfUserSealDo afUserSealDo1 = new AfUserSealDo();
                afUserSealDo1.setId(afUserSealDo1.getId());
                if (null != addSealResult.getSealData() || "" != addSealResult.getSealData()){
                    afUserSealDo1.setUserSeal("data:image/png;base64," + addSealResult.getSealData());
                }else {
                    logger.error("company seal error:",addSealResult.getMsg());
                }
                afESdkService.updateUserSealByUserId(afUserSealDo1);
            }
        }
        logger.info("初始化执行完成...");
    }

    public String addOrganize() {
        String mobile = "15712345678";
        String email = "286415074@qq.com";
        String name = "测试个人";
        int organType = 0;
        String regType = "0";
        String organCode = "05651644-4";
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
        if (150016 == r.getErrCode() || r.getMsg().contains("账户已存在")){
            GetAccountProfileResult getAccountProfileResult = SERVICE.getAccountInfoByIdNo(legalIdNo,11);
            return getAccountProfileResult.getAccountInfo().getAccountUid();
        }
//        Tools.response(req, resp, r);
        return r.getAccountId();
    }

}
