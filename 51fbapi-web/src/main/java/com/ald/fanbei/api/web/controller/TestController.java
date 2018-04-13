package com.ald.fanbei.api.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.*;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataData;
import com.ald.fanbei.api.common.kdniao.KdniaoTrackQueryAPI;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.AuthGxbRespBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BrandCouponResponseBo;
import com.ald.fanbei.api.biz.bo.InterestFreeJsonBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.third.util.RiskRequestProxy;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.huichaopay.HuichaoUtility;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AfUserDto;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.domain.XItem;

import redis.clients.jedis.BinaryClient;


@Controller
public class TestController {
    Logger logger = LoggerFactory.getLogger(TestController.class);

    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    SmsUtil smsUtil;

    @Resource
    AfOrderService afOrderService;
    @Resource
    CouponSceneRuleEnginer authRealnameRuleEngine;
    @Resource
    CouponSceneRuleEnginer signinRuleEngine;
    @Resource
    JpushService jpushService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfAuthContactsService afAuthContactsService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfContactsOldService afContactsOldService;
    @Resource
    RiskUtil riskUtil;
    @Resource
    UpsUtil upsUtil;
    @Resource
    AfUserDao afUserDao;
    @Resource
    AfUserBankcardDao afUserBankcardDao;
    @Resource
    AfOrderRefundDao afOrderRefundDao;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
    @Resource
    AfOrderDao afOrderDao;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    RiskTrackerService riskTrackerService;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfBorrowDao afBorrowDao;
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfUserVirtualAccountService afUserVirtualAccountService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    private TaobaoApiUtil taobaoApiUtil;
    @Autowired
    RiskRequestProxy requestProxy;
    @Autowired
    AppOpenLogDao appOpenLogDao;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    JdbcTemplate loanJdbcTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private KafkaSync kafkaSync;
    @Autowired
    private AppOpenLogService appOpenLogService;
    @RequestMapping("/compensate")
    @ResponseBody
    public String compensate() {
        try {
            RiskTrackerDo riskTrackerDo = new RiskTrackerDo();
            List<RiskTrackerDo> riskTrackerDoList = riskTrackerService.getListByCommonCondition(riskTrackerDo);
            for (RiskTrackerDo item : riskTrackerDoList) {
                HashMap reqBo1 = JSON.parseObject(item.getParams(), HashMap.class);
                //String data1 = getUrlParamsByMap(reqBo1);
//                String reqResult1 = HttpUtil.post(item.getUrl(), reqBo1);
//                if (StringUtil.isBlank(reqResult1)) {
//                    throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
//                }
                if (item.getUrl().indexOf("updateOpenId") != -1 && item.getTrackId().indexOf("success_") == -1) {//未处理过得提额
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        if (StringUtil.isBlank(reqResult)) {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                        RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
                        if (riskResp != null && "0000".equals(riskResp.getCode())) {
                            item.setTrackId("success_" + item.getTrackId());
                            riskTrackerService.updateById(item);
                        } else {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("updateOpenId compensate exception:", e);
                    }

                } else if (item.getUrl().indexOf("raiseQuota") != -1 && item.getTrackId().indexOf("success_") == -1) {//未处理过得提额
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        if (StringUtil.isBlank(reqResult)) {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                        RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
                        if (riskResp != null && "0000".equals(riskResp.getCode())) {
                            riskResp.setSuccess(true);
                            JSONObject dataObj = JSON.parseObject(riskResp.getData());
                            BigDecimal au_amount = new BigDecimal(dataObj.getString("amount"));
                            Long consumerNum = Long.parseLong(reqBo.get("consumerNo").toString());

                            AfUserAccountDo accountDo = new AfUserAccountDo();
                            accountDo.setUserId(consumerNum);
                            accountDo.setAuAmount(au_amount);
                            afUserAccountService.updateUserAccount(accountDo);
                            item.setTrackId("success_" + item.getTrackId());
                            riskTrackerService.updateById(item);
                        } else {
                            throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
                        }
                    } catch (Exception e) {
                        logger.error("raiseQuota compensate exception:", e);
                    }

                } else if (item.getUrl().indexOf("/overdueOrder.htm") != -1 && item.getTrackId().indexOf("success_") == -1) {
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        logger.info("overdueOrder.htm compensate  overdueOrder:" + reqResult);
                        item.setTrackId("success_" + item.getTrackId());
                        riskTrackerService.updateById(item);
                    } catch (Exception e) {
                        logger.error("overdueOrder compensate exception:", e);
                    }

                } else if (item.getUrl().indexOf("/repayment.htm") != -1 && item.getTrackId().indexOf("success_") == -1) {
                    try {
                        HashMap reqBo = JSON.parseObject(item.getParams(), HashMap.class);
                        String data = getUrlParamsByMap(reqBo);
                        String reqResult = HttpUtil.doHttpsPostIgnoreCertUrlencoded(item.getUrl(), data);
                        logger.info("repayment.htm compensate  repayment:" + reqResult);
                        item.setTrackId("success_" + item.getTrackId());
                        riskTrackerService.updateById(item);
                    } catch (Exception e) {
                        logger.error("repayment compensate exception:", e);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("compensate error:", e);
        }
        return "调用处理中^";
    }

    @RequestMapping("/loanTest")
    @ResponseBody
    public String loanTest() {
        try{
            Thread.sleep(50000);
        }catch (Exception e){

        }

       Integer data= loanJdbcTemplate.queryForObject("SELECT COUNT(1) from af_borrow_cash a left join af_user b on a.user_id=b.id where b.user_name='"+"13165995223"+"' and a.`status` in ('TRANSED','TRANSEDING')",Integer.class);
        return String.valueOf(data) ;

    }
    @RequestMapping("/kafkaTest")
    @ResponseBody
    public String kafkaTest() {
       kafkaSync.syncEvent(13989455786l,"sync_directory",true);
        kafkaSync.syncEvent(18637963069l,"sync_directory",true);
        kafkaSync.syncEvent(18637963176l,"sync_directory",true);

        return "调用处理中^";

    }

    @RequestMapping("/cuishou")
    @ResponseBody
    public String cuishou() {
        KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
        String requestData = api.getOrderTracesByJson("SF", "058029296755");

        KdniaoReqDataData kdniaoReqData = JSON.parseObject(requestData, KdniaoReqDataData.class);
        System.out.print(requestData);

//        jpushService.strongRiskSuccess("15990182307");
//        //AfRepaymentBorrowCashDo existItem = afRepaymentBorrowCashService.getRepaymentBorrowCashByTradeNo(1302389l, "20170727200040011100260068825762");
//        ExecutorService pool = Executors.newFixedThreadPool(16);
//        for (int i = 0; i < 1000; i++) {
//            pool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(new Date());
//                    System.out.println("---" + repayNo);
//                }
//            });
//        }


        // riskUtil.syncOpenId(1302389,"268811897276756002554870029");
        return "调用处理中^";

    }
    @RequestMapping("/kafka")
    @ResponseBody
    public String testKafka() throws Exception{
    	
    	Long userId = 18637962178L;
		String appId = ConfigProperties.get(Constants.AUTH_GXB_APPID);
		String appSecurity = ConfigProperties.get(Constants.AUTH_GXB_APPSECURITY);
		//String appId = AesUtil.decrypt(ConfigProperties.get(Constants.AUTH_GXB_APPID),ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		//String appSecurity = AesUtil.decrypt(ConfigProperties.get(Constants.AUTH_GXB_APPSECURITY),ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String fanbeitest = ConfigProperties.get("fanbeitest.test.user");
		String inveloment = ConfigProperties.get("fbadmin.inveloment.type");
		//logger.info("appId:"+appId);
		//logger.info("appSecurity:"+appSecurity);
		logger.info("fanbeitest:"+fanbeitest);
		logger.info("inveloment:"+inveloment);
		String sequenceNo=userId+"gxb"+System.currentTimeMillis();
		String authItem="ecommerce";
		String timestamp=DateUtil.getCurrSecondTimeStamp()+"";
		AfUserDto afUserDto= afUserService.getUserInfoByUserId(userId);
		String name=afUserDto.getRealName();
		String phone=afUserDto.getUserName();
	    String idcard=afUserDto.getIdNumber();
	    HashMap<String, Object> map=new HashMap<String, Object>();
	    map.put("appId",appId);
	    map.put("sign", DigestUtils.md5Hex(appId+appSecurity+authItem+timestamp+sequenceNo));
	    map.put("sequenceNo",sequenceNo);
	    map.put("authItem",authItem);
	    map.put("timestamp",timestamp);
	    map.put("name",name);
	    map.put("phone",phone);
	    map.put("idcard",idcard);
	    try {
	    	logger.info("mapInfo:"+map.toString());
	    	String respResult = HttpUtil.doHttpsPostIgnoreCertJSON("https://prod.gxb.io/crawler/auth/v2/get_auth_token", JSON.toJSONString(map));
	    	if (StringUtil.isBlank(respResult)) {
	    		logger.error("getAuthToken respResult is null");
			}else{
				AuthGxbRespBo respInfo = JSONObject.parseObject(respResult, AuthGxbRespBo.class);
				if ("1".equals(respInfo.getRetCode())) {
					JSONObject data = JSON.parseObject(respInfo.getData());
					String token=data.getString("token");
					logger.info("getAuthToken resp success, token="+token+",respInfo"+respInfo.getRetMsg());
					String riskUrl = ConfigProperties.get(Constants.CONFKEY_RISK_URL);
				    //String returnUrl = riskUrl + "/tpp/gxbdata/alipay/notify.htm";
					//String returnUrl = "http://btestarc.51fanbei.com/tpp/gxbdata/alipay/notify.htm";
					String returnUrl = riskUtil.getUrl()+"/tpp/gxbdata/alipay/notify.htm";	
				    String urlFull = "https://prod.gxb.io/v2/auth?returnUrl="+returnUrl+"&token="+token;
				    logger.info("url=" + urlFull+"userId="+userId);
				    return "success";
				}else {
					//三方处理错误
					logger.error("getAuthToken resp fail,errorCode="+respInfo.getRetCode()+",errorInfo"+respInfo.getRetMsg());
				}
			}
		} catch (Exception e) {
			logger.error("error = " + e);
		}
    	
    	
        /*String appId="gxb099547a1a9ea2e48";
        String appSecurity="a8b2a9708cb4487cacdb568fadef19cd";
        String sequenceNo="12345678gxb00001";//用户的id 中间gxb 末尾自增
        String authItem="ecommerce";
        String timestamp="1499363705428";
        String name="任春雷";
        String phone="15990182307";
        String idcard="340621198906108755";
        HashMap map=new HashMap();
        map.put("appId",appId);
        map.put("sign", DigestUtils.md5Hex(appId+appSecurity+authItem+timestamp+sequenceNo));
        map.put("sequenceNo",sequenceNo);
        map.put("authItem",authItem);
        map.put("timestamp",timestamp);
        map.put("name",name);
        map.put("phone",phone);
        map.put("idcard",idcard);

    	Long userId = 18637962178L;
		String appId = ConfigProperties.get(Constants.AUTH_GXB_APPID);
		String appSecurity = ConfigProperties.get(Constants.AUTH_GXB_APPSECURITY);
		String fanbeitest = ConfigProperties.get("fanbeitest.test.user");
		String inveloment = ConfigProperties.get("fbadmin.inveloment.type");
		logger.info("appId:"+appId);
		logger.info("appSecurity:"+appSecurity);
		logger.info("fanbeitest:"+fanbeitest);
		logger.info("inveloment:"+inveloment);
        logger.error("testKafka------");
        try{
            // HashMap hashMap= kafkaSync.getUserSummarySync(13989455976l);
            kafkaTemplate.send(ConfigProperties.get(KafkaConstants.SYNC_TOPIC) ,KafkaConstants.SYNC_BORROW_CASH,"18637962835");
            HashMap hashMap= mongoTemplate.findOne(Query.query(Criteria.where("_id").is("18637962835")),HashMap.class,"UserDataSummary");


        String reqResult = HttpUtil.doHttpsPostIgnoreCertJSON("https://prod.gxb.io/crawler/auth/v2/get_auth_token", JSON.toJSONString(map));*/


        //endregion
     /*   System.out.println(reqResult);

        return reqResult;*/
	    return "fail";
    }
    public void testTrans(){
        transactionTemplate.execute(new TransactionCallback<String>() {

            @Override
            public String doInTransaction(TransactionStatus status) {
                AppOpenLogDo appOpenLogDo=new AppOpenLogDo();
                appOpenLogDo.setUuid("test");
                appOpenLogDo.setAppVersion("403");
                appOpenLogDo.setGmtCreate(new Date());
                appOpenLogDo.setPhoneType("ios");
                appOpenLogDo.setUserName("renchunlei");
                appOpenLogService.saveRecord(appOpenLogDo);
                AppOpenLogDo query=new AppOpenLogDo();
                query.setAppVersion("403");
                List<AppOpenLogDo> appOpenLogDos= appOpenLogService.getListByCommonCondition(query);

                return "";
            }
        });
    }
    @RequestMapping("/address")
    @ResponseBody
    public String testAddress() throws Exception{
        List<String> s=new ArrayList<>();
        HashMap<String,Integer> score=new HashMap<>();
        s.add("	重庆市重庆市涪陵区珍溪镇西桥村5组	");
        s.add("	湖北省武汉市武昌区南湖花园军威苑3栋2单元1602	");
        s.add("	上海市上海市闵行区桂林路929号	");
        s.add("	江西省景德镇市珠山区加州印象3栋	");
        s.add("	广东省深圳市龙岗区南湾街道锦航酒店附近代收点	");
        s.add("	辽宁省沈阳市大东区天润广场3号楼1301	");
        s.add("	甘肃省平凉市华亭县华亭煤业大柳煤矿	");
        s.add("	浙江省台州市椒江区洪家街道前高桥小区55-4号	");
        s.add("	上海市上海市闵行区上海上海市闵行区七宝镇上海上海市闵行区七宝镇闵行区糟宝路3366号七宝万科广场L2层221单元	");
        s.add("	上海市上海市宝山区联杨路1078弄3号楼1701	");
        s.add("	江西省赣州市寻乌县长宁镇金茂花园401	");
        s.add("	北京市北京市平谷区北京市平谷区金海湖镇黑水湾村南大街60号	");
        s.add("	广东省深圳市龙岗区布吉上水径街道第278号美利达自行车厂	");
        s.add("	辽宁省大连市中山区人民路66号	");
        s.add("	浙江省温州市鹿城区七都镇板桥北路35号");
        s.add("	湖南省湘西土家族苗族自治州吉首市湘泉城市花园长景阁二单元1108	");
        s.add("	浙江省宁波市江东区百丈东路1188号	");
        s.add("	广东省茂名市茂南区高凉南路111号1梯402	");
        s.add("	河北省邯郸市邯山区陵园路立新胡同11号楼一单元8号	");
        s.add("	贵州省黔西南布依族苗族自治州兴义市水木清华1单元	");
        s.add("	云南省临沧市凤庆县客运站进口烟酒副食百货店	");
        s.add("	山东省泰安市宁阳县堽城镇中心医院家属院	");
        s.add("	云南省昆明市官渡区华潮水产批发市场五号门	");
        s.add("	江苏省南京市浦口区旭日爱上城星岛园9栋704	");
        s.add("	重庆市重庆市渝中区中山三路129号腾宇地产	");
        s.add("	辽宁省大连市金州区新青年汇三期三单元0916	");
        s.add("	广西壮族自治区桂林市七星区建干里32号	");
        s.add("	重庆市重庆市沙坪坝区洪逸新村3单元6-2	");
        s.add("	四川省南充市蓬安县金街宋氏佳厨	");
        s.add("	江西省赣州市崇义县步行街日泰皮鞋	");
        s.add("	云南省普洱市思茅区云南省普洱市思茅区宁洱大道64号新四中对面迪诺牛肉面馆	");
        s.add("	贵州省贵阳市云岩区三桥新街54号（美凤超市）	");
        s.add("	江苏省扬州市仪征市江苏省仪征市青山镇钢管厂宿舍二号楼603	");
        s.add("	重庆市重庆市开县开县大进镇	");
        s.add("	江苏省常州市金坛市尧塘镇东巷9号	");
        s.add("	广东省深圳市华繁路工业区工业楼鑫富艺有限公司	");
        s.add("	山东省潍坊市坊子区坊子区南流镇前苏村144号	");
        s.add("	重庆市重庆市九龙坡区杨家坪步行街珠江路24号6-1	");
        s.add("	北京市北京市西城区中央大学城菜鸟驿站	");
        s.add("	广东省云浮市云安区镇安镇沿江路适心百货商店	");
        s.add("	四川省成都市锦江区工农院街60号	");
        s.add("	黑龙江省齐齐哈尔市铁锋区鑫鑫花园53号楼2号门市	");
        s.add("	陕西省渭南市韩城市中环广场红鑫中央公园营销中心	");
        s.add("	江苏省盐城市阜宁县益林镇健康路178号	");
        s.add("	江苏省淮安市涟水县高沟镇三口小区	");
        s.add("	广东省清远市清城区清远市清新区太平镇龙湾工业园中骏森驰汽車配件有限公司	");
        s.add("	湖北省荆州市石首市小河口镇天鹅村一组	");
        s.add("	山东省青岛市平度市青岛市平度市仁兆镇	");
        s.add("	广东省汕尾市陆丰市汕尾市陆丰市甲东镇雨亭村	");

        for(String item :s){
           int scoreItem= SmartAddressEngine.getScore(item.trim());
            score.put(item,scoreItem);
        }



        return "测试kafka";
    }

    @RequestMapping("/clearredis")
    @ResponseBody
    public String clearredis(String key) {
        bizCacheUtil.delCache(key);
        return "redis 清除成功";

    }

    @RequestMapping("/transed")
    @ResponseBody
    public String transed() {
        //AfRepaymentBorrowCashDo existItem = afRepaymentBorrowCashService.getRepaymentBorrowCashByTradeNo(1302389l, "20170727200040011100260068825762");
        ExecutorService pool = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 100; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        transactionTemplate.execute(new TransactionCallback<Integer>() {
                            @Override
                            public Integer doInTransaction(TransactionStatus transactionStatus) {
                                AfBorrowDo borrowDo = new AfBorrowDo();
                                AppOpenLogDo appOpenLogDo = new AppOpenLogDo();
                                appOpenLogDo.setRid(1l);
                                appOpenLogDo.setAppVersion("123:" + new Date().getTime());
                                appOpenLogDao.updateById(appOpenLogDo);
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return 1;
                            }
                        });
                    } catch (Exception e) {
                        logger.info("error:", e);
                    }

                }
            });
        }


        // riskUtil.syncOpenId(1302389,"268811897276756002554870029");
        return "调用处理中^";

    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByHashMap(HashMap<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 新h5页面处理，针对前端开发新的h5页面时请求的处理
     *
     * @param model
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"/h5/app/*_new", "/h5/app/sys/*_new", "/h5/app/goods/*_new", "/h5/app/mine/*_new", "/h5/app/order/*_new"}, method = RequestMethod.GET)
    public String newVmPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String returnUrl = request.getRequestURI().replace("/h5/", "");
        return returnUrl;
    }

    /**
     * 新h5页面处理，针对前端开发新的h5页面时请求的处理
     *
     * @param model
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"testPush"}, method = RequestMethod.GET)
    public String testPush(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userName = request.getParameter("userName");
//		jpushService.refundMobileError(userName, new Date());
//		jpushService.repayRenewalSuccess(userName);
//		jpushService.repayRenewalFail(userName);
//		jpushService.chargeMobileError(userName, userName, new Date());
//		jpushService.gameShareSuccess(userName);
//		jpushService.chargeMobileSucc(userName, userName, new Date());
        jpushService.strongRiskSuccess(userName);
        jpushService.strongRiskFail(userName);
        jpushService.mobileRiskFail(userName);
        jpushService.mobileRiskSuccess(userName);
        return "";
    }

//	@RequestMapping(value = { "/test1" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String goodsRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		afOrderService.dealMobileChargeOrder("MB17040100045", "222000");
//		riskUtil.modify("73772", "胡潮永", "13958004662", "330624198509136450", "", "", "星耀城", "");
//		Map<String, Object> inputData = new HashMap<String, Object>();
//		inputData.put("userId", 11l);
//		inputData.put("seriesCount", 5);
//		signinRuleEngine.executeRule(inputData);
//
//		jpushService.chargeMobileSucc("13607665702", "13607665702", new Date());
//
//		String reportId = TongdunUtil.applyPreloan("362525198601022112", "陈金虎", "15958119936", "410228573@qq.com");
//		// ER2017012122013411346564
//		TongdunResultBo result = TongdunUtil.queryPreloan("ER2017012121595110613362");
//
//		System.out.println("-----reportId---" + 11 + ",result=" + result);
//		smsUtil.sendRegistVerifyCode("15958119936");
//
//		SmsUtil.sendSms("15958119936", "验证码:1234");
//		afOrderService.createOrderTrade("{'buyer_id':'AAGtxNL8AClXeBuXBPILbV-s','paid_fee':'138.00','shop_title':'佐祥车品旗舰店','is_eticket':false,'create_order_time':'2017-02-17 14:36:28','order_id':'3065189213875206','order_status':'7','seller_nick':'佐祥车品旗舰店','auction_infos':[{'detail_order_id':'3065189213875206','auction_id':'AAEnxNL_AClXeBuXBIxwBj6s','real_pay':'138.00','auction_pict_url':'i1/2208256900/TB2uxTDXNXkpuFjy0FiXXbUfFXa_!!2208256900.jpg','auction_title':'汽车载氧吧空气净化雾霾器负离子杀菌香薰除甲醛异味全自动过滤','auction_amount':'1'}]}");
//		return "succ";
//	}

//	@RequestMapping(value = { "/test2" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String batchRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		riskUtil.batchRegister(5, "13958004662");
//		return "succ";
//	}


//	@RequestMapping(value = { "/test3" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	public String addressListRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		int count = afUserAccountService.getUserAccountCountWithHasRealName();
//		int pageCount = (int) Math.ceil(count / 10) + 1;
//		for (int j = 1; j <= pageCount; j++) {
//			AfUserAccountQuery query = new AfUserAccountQuery();
//			query.setPageNo(j);
//			query.setPageSize(10);
//			List<AfUserAccountDto> list = afUserAccountService.getUserAndAccountListWithHasRealName(query);
//			for (int i = 0; i < list.size(); i++) {
//				List<AfAuthContactsDo> contacts = afAuthContactsService.getContactsByUserId(list.get(i).getUserId());
//				riskUtil.addressListPrimaries(list.get(i).getUserId().toString(), contacts);
//			}
//		}
//		return "succ";
//	}
//	/**
//	 * 同步通讯录
//	 * @return
//	 */
//	@RequestMapping(value = { "/SyncAddressList/toRiskManagement" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String SyncAddressListRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		int count = afUserAuthService.getUserAuthCountWithIvs_statusIsY();
//		int pageCount = (int) Math.ceil(count / 10) + 1;
//		for (int j = 1; j <= pageCount; j++) {
//			AfUserAuthQuery query = new AfUserAuthQuery();
//			query.setPageNo(j);
//			query.setPageSize(10);
//			List<AfUserAuthDo> list = afUserAuthService.getUserAuthListWithIvs_statusIsY(query);
//			for (int i = 0; i < list.size(); i++) {
//				AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
//				if (null != afContactsOldDo) {
//					String moblieBook = afContactsOldDo.getMobileBook();
//					String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"")+1,moblieBook.lastIndexOf("\""));
//					 
//					JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
//					List<AfAuthContactsDo> contacts = new ArrayList<AfAuthContactsDo>();
//					for (Object object : moblieBookJsons) {
//						JSONObject json = JSONObject.parseObject(object.toString());
//						AfAuthContactsDo afAuthContactsDo = new AfAuthContactsDo();
//						afAuthContactsDo.setFriendNick(json.getString("name"));
//						afAuthContactsDo.setFriendPhone(json.getString("phone_number"));
//						contacts.add(afAuthContactsDo);
//					}
//					riskUtil.addressListPrimaries(list.get(i).getUserId().toString(), contacts);
//				}
//			}
//		}
//		return "succ";
//	}
//	/**
//	 * 同步一条通讯录
//	 * @return
//	 */
//	@RequestMapping(value = { "/SyncOneAddress" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String SyncAddressRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//			
//		Long userId = (long) 68424;
//		AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(userId);
//		String moblieBook = afContactsOldDo.getMobileBook();
//		String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"")+1,moblieBook.lastIndexOf("\""));
//		 
//		JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
//		List<AfAuthContactsDo> contacts = new ArrayList<AfAuthContactsDo>();
//		for (Object object : moblieBookJsons) {
//			JSONObject json = JSONObject.parseObject(object.toString());
//			AfAuthContactsDo afAuthContactsDo = new AfAuthContactsDo();
//			afAuthContactsDo.setFriendNick(json.getString("name"));
//			afAuthContactsDo.setFriendPhone(json.getString("phone_number"));
//			contacts.add(afAuthContactsDo);
//		}
//		RiskAddressListRespBo riskAddressListRespBo = riskUtil.addressListPrimaries(userId.toString(), contacts);
//		return JSONObject.toJSONString(riskAddressListRespBo);
//	}

    /**
     * 用户通讯录集合同步
     *
     * @return
     * @author fumeiai
     */
    @RequestMapping(value = {"/SyncUserAddressList/toRiskManagement"}, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String SyncUserAddressList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("------toRiskManagement-----");
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        int count = afUserAuthService.getUserAuthCountWithIvs_statusIsY();
        int pageCount = (int) Math.ceil(count / 10) + 1;
        logger.info("------toRiskManagement--count---" + count + ",pageCount=" + pageCount);
        for (int j = 1; j <= pageCount; j++) {
            AfUserAuthQuery query = new AfUserAuthQuery();
            query.setPageNo(j);
            query.setPageSize(120);
            List<AfUserAuthDo> list = afUserAuthService.getUserAuthListWithIvs_statusIsY(query);
            logger.info("j=" + j + ",size=" + list.size());
            for (int i = 0; i < list.size(); i++) {
                try {
                    AfContactsOldDo afContactsOldDo = afContactsOldService.getAfContactsByUserId(list.get(i).getUserId());
                    logger.info("i=" + i + "," + afContactsOldDo != null ? afContactsOldDo.toString() : "");
                    if (null != afContactsOldDo) {
                        String moblieBook = afContactsOldDo.getMobileBook();
                        String formatMoblieBook = moblieBook.substring(moblieBook.indexOf("\"") + 1, moblieBook.lastIndexOf("\""));

                        JSONArray moblieBookJsons = JSONArray.parseArray(formatMoblieBook);
                        StringBuffer data = new StringBuffer();
                        for (Object object : moblieBookJsons) {
                            JSONObject json = JSONObject.parseObject(object.toString());
                            data.append(json.getString("name") + ":");
                            data.append(json.getString("phone_number") + ",");
                        }
                        logger.info("i=" + i + "," + data.toString());

                        riskUtil.addressListPrimaries(afContactsOldDo.getUid().toString(), data.toString().substring(0, data.toString().length() - 1));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("init error=" + list.get(i).getUserId());
                }
            }
        }
        return "succ";
    }

    @RequestMapping(value = {"/wxRefund"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public String wxRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = JSONObject.parseObject(body);
        String orderNo = json.getString("orderNo");
        String payTradeNo = json.getString("payTradeNo");
        BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
        BigDecimal totalAmount = NumberUtil.objToBigDecimalDefault(json.getString("totalAmount"), null);
        logger.info("wxRefund begin wxRefund is orderNo = {}, payTradeNo = {}, refundAmount = {}, refundAmount = {}", new Object[]{orderNo, payTradeNo, refundAmount, totalAmount});
        if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(payTradeNo) || refundAmount == null || totalAmount == null) {
            return "";
        }
        String refundResult = UpsUtil.wxRefund(orderNo, payTradeNo, refundAmount, totalAmount);
        logger.info("wxRefund refundResult = {}", refundResult);
        System.out.println(refundResult);
        return "succ";
    }


    /**
     * 银行卡退款
     * @author fumeiai
     * @return
     */
    //处理菠萝觅或者代买没有生成账单的订单，重新生成账单。
//	@RequestMapping(value = { "/dealWithBoluomeBorrow" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
//	@ResponseBody
//	public String dealWithBoluomeBorrow(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
//		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
//		response.setContentType("application/json;charset=utf-8");
//		List<AfOrderDo> orderList = afOrderDao.getNoBorrowOrder();
//		if (CollectionUtils.isNotEmpty(orderList)) {
//			for (AfOrderDo orderInfo : orderList) {
//				AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
//				afBorrowService.dealAgentPayConsumeRisk(userAccountInfo, orderInfo.getActualAmount(),
//						orderInfo.getGoodsName(), orderInfo.getNper(), orderInfo.getRid(),
//						orderInfo.getOrderNo(), null);
//			}
//		}
//		return "succ";
//	}
//	
//

    /**
     * 银行卡退款
     *
     * @return
     * @author fumeiai
     */
    @RequestMapping(value = {"/bankRefund"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String bankRefund(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        JSONObject json = JSONObject.parseObject(body);
//		BigDecimal refundAmount = NumberUtil.objToBigDecimalDefault(json.getString("refundAmount"), null);
        String payTradeNo = json.getString("payTradeNo");
        AfRepaymentBorrowCashDo afRepaymentBorrowCashDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNoWithStatusY(payTradeNo);
        String message = "succ!";
        if (null != afRepaymentBorrowCashDo) {
            BigDecimal refundAmount = afRepaymentBorrowCashDo.getActualAmount();
//			BigDecimal refundAmount = new BigDecimal(0.01).setScale(2, BigDecimal.ROUND_HALF_UP);
            AfUserBankcardDo card = afUserBankcardDao.getUserBankcardByCardNo(afRepaymentBorrowCashDo.getCardNumber());
            AfUserDo userDo = afUserDao.getUserById(card.getUserId());
            UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(refundAmount, userDo.getRealName(), card.getCardNumber(), card.getUserId() + "",
                    card.getMobile(), card.getBankName(), card.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", OrderType.MOBILE.getCode(), "");
            String refundNo = generatorClusterNo.getRefundNo(new Date());
            if (!upsResult.isSuccess()) {
                AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "",
                        OrderRefundStatus.FAIL, PayType.BANK, card.getCardNumber(), card.getBankName(), "用户现金借中重复还款后的退款", RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo());
                afOrderRefundDao.addOrderRefund(afOrderRefundDo);
                message = "Fail!";
            } else {
                AfOrderRefundDo afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo(refundNo, refundAmount, refundAmount, afRepaymentBorrowCashDo.getUserId(), 0l, "",
                        OrderRefundStatus.FINISH, PayType.BANK, card.getCardNumber(), card.getBankName(), "用户现金借中重复还款后的退款", RefundSource.PLANT_FORM.getCode(), upsResult.getOrderNo());
                AfRepaymentBorrowCashDo repaymentBorrowCashDo = new AfRepaymentBorrowCashDo();
                repaymentBorrowCashDo.setRid(afRepaymentBorrowCashDo.getRid());
                repaymentBorrowCashDo.setStatus("R");
                afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repaymentBorrowCashDo);
                afOrderRefundDao.addOrderRefund(afOrderRefundDo);
            }
        } else {
            message = "There is no trade can refund!";
        }

        return message;
    }

    // TongdunUtil

    @RequestMapping(value = {"/wxRefundMobile"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String wxRefundMobile(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        String message = "succ!";
        try {
            JSONObject json = JSONObject.parseObject(body);
            String orderId = json.getString("orderId");
            String scret = json.getString("scret");
            if (!"zsdERfds2123".equals(scret)) {
                throw new RuntimeException("秘钥不对");
            }
            AfOrderDo order = afOrderDao.getOrderById(Long.valueOf(orderId));
            String refundNo = generatorClusterNo.getRefundNo(new Date());
            String refundResult = UpsUtil.wxRefund(order.getOrderNo(), order.getPayTradeNo(), order.getActualAmount(), order.getActualAmount());
            if (!"SUCCESS".equals(refundResult)) {
                afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FAIL, PayType.WECHAT, "", null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
                throw new FanbeiException("reund error", FanbeiExceptionCode.REFUND_ERR);
            } else {
                afOrderRefundDao.addOrderRefund(BuildInfoUtil.buildOrderRefundDo(refundNo, order.getActualAmount(), order.getActualAmount(), order.getUserId(), order.getRid(), order.getOrderNo(), OrderRefundStatus.FINISH, PayType.WECHAT, "", null, "充值失败微信退款", RefundSource.PLANT_FORM.getCode(), order.getPayTradeNo()));
            }
        } catch (Exception e) {
            logger.info("wxRefund error:", e);
            message = "There is no trade can refund!";
        }
        return message;
    }

    @RequestMapping(value = {"/changeShopName"}, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String changeShopName(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        String message = "succ!";
        try {
            JSONObject json = JSONObject.parseObject(body);
            String pageNo = json.getString("pageNo");

            String scret = json.getString("scret");
            if (!"zsdERfds2123".equals(scret)) {
                throw new RuntimeException("秘钥不对");
            }
            List<AfOrderDo> list = afOrderDao.getNotShopNameByAgentBuyOrder(Long.valueOf(pageNo));
            List<String> orderNumIdsList = CollectionConverterUtil.convertToListFromList(list,
                    new Converter<AfOrderDo, String>() {
                        @Override
                        public String convert(AfOrderDo source) {
                            return source.getNumId();
                        }
                    });
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("numIid", StringUtil.turnListToStr(orderNumIdsList));
            List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();

            for (XItem xItem : nTbkItemList) {
                String orderType = xItem.getMall() ? "TMALL" : "TAOBAO";
                String nick = xItem.getNick();
                if (xItem.getOpenId() != 0) {
                    for (AfOrderDo orderDo : list) {
                        if (StringUtils.equals(xItem.getOpenId() + "", orderDo.getNumId())) {
                            AfOrderDo orderN = new AfOrderDo();
                            orderN.setRid(orderDo.getRid());
                            orderN.setShopName(nick);
                            orderN.setSecType(orderType);
                            afOrderDao.updateOrder(orderN);
                        }
                    }
                }

            }

        } catch (Exception e) {
            logger.info("changeShopName error:", e);
            message = "There is  changeShopName ";
        }
        return message;
    }

    /**
     * app中微信支付回调接口
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/allowcateBrandCoupon"}, method = RequestMethod.POST)
    @ResponseBody
    public String allowcateBrandCoupon(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String brandUrl = request.getParameter("brandUrl");

            BufferedReader reader = request.getReader();
            String line = null;
            while ((line = reader.readLine()) != null) {
                pickBrandCoupon(line, brandUrl);
            }
        } catch (Exception e) {
            logger.error("allowcateBrandCoupon", e);
            return "fail";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    @RequestMapping(value = {"/jPushByType"}, method = RequestMethod.GET)
    @ResponseBody
    public String jPushByType(int jumpType, String type, String userName) {
        PrintWriter out = null;
        try {
            jpushService.jPushByType(jumpType, type, userName);
            ;
        } catch (Exception e) {
            logger.error("allowcateBrandCoupon", e);
            return "fail";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }


    @RequestMapping(value = {"/jPushCoupon"}, method = RequestMethod.GET)
    @ResponseBody
    public String jPushCoupon(String type, String userName) {
        PrintWriter out = null;
        try {
            jpushService.jPushCoupon(type, userName);
        } catch (Exception e) {
            logger.error("allowcateBrandCoupon", e);
            return "fail";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }


    @RequestMapping(value = {"/testJPush"})
    @ResponseBody
    public String testJPush(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String userName = request.getParameter("userName");
            jpushService.pushHeaderImage(userName);
            ;
        } catch (Exception e) {
            logger.error("allowcateBrandCoupon", e);
            return "fail";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    @RequestMapping(value = {"/testAllJPush"})
    @ResponseBody
    public String testAllJPush(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            jpushService.pushAllHeaderImage();
        } catch (Exception e) {
            logger.error("allowcateBrandCoupon", e);
            return "fail";
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return "success";
    }

    @RequestMapping(value = {"/testOrderPay"}, method = RequestMethod.POST)
    @ResponseBody
    public void testOrderPay(HttpServletRequest request, HttpServletResponse response) {
        long orderId = 198649;
        AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
        boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount(), orderInfo.getSecType());
    }

    private void pickBrandCoupon(String userName, String brandUrl) {
        AfUserDo userInfo = afUserDao.getUserByUserName(userName);
        if (userInfo == null) {
            logger.info(userName + "userName dosn't exist");
            return;
        }
        PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
        bo.setUser_id(userInfo.getRid() + StringUtil.EMPTY);
        String resultString = HttpUtil.doHttpPostJsonParam(brandUrl, JSONObject.toJSONString(bo));
        logger.info("userName = " + userName + " brandUrl = " + brandUrl);
        logger.info("allowcateBrandCoupon pickBrandCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
    }

    @RequestMapping(value = {"/testRiskQueryOverdueOrder"}, method = RequestMethod.POST)
    @ResponseBody
    public String testRiskQueryOverdueOrder(HttpServletRequest request, HttpServletResponse response) {
        RiskQueryOverdueOrderRespBo resp = riskUtil.queryOverdueOrder("68885");
        System.out.println(resp);
        return "success";
    }

    @RequestMapping(value = {"/test11"}, method = RequestMethod.POST)
    @ResponseBody
    public String bo(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", "100000000123");
        params.put("appKey", "2607839913");
        params.put("timestamp", "1500628924755");
        System.out.println(BoluomeCore.builSign(params));

//		String identity = System.currentTimeMillis() + StringUtil.EMPTY;
//		String orderNo = riskUtil.getOrderNo("over", identity.substring(identity.length() - 4, identity.length()));
//		List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
//		RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
//		bo.setBorrowNo("jk2017071020281800843");
//		bo.setOverdueDays(0);
//		bo.setOverdueTimes(1);
//		boList.add(bo);
//		logger.info("dealWithSynchronizeOverduedOrder begin orderNo = {} , boList = {}", orderNo, boList);
//		riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
//		logger.info("dealWithSynchronizeOverduedOrder completed");
        return "success";
    }

    @RequestMapping(value = {"/dealWithBorrow"}, method = RequestMethod.POST)
    @ResponseBody
    public String dealWithBorrow(HttpServletRequest request, HttpServletResponse response) {
        List<AfOrderDo> list = afOrderDao.get20170801ExceptionOrder();
        for (AfOrderDo orderInfo : list) {
            AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
            String name = orderInfo.getGoodsName();

            AfBorrowDo borrow = buildAgentPayBorrow(name, BorrowType.TOCONSUME, orderInfo.getUserId(), orderInfo.getActualAmount(),
                    orderInfo.getNper(), BorrowStatus.APPLY.getCode(), orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson());

            Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
            String virtualCode = getVirtualCode(virtualMap);
            //是虚拟商品
            if (StringUtils.isNotBlank(virtualCode)) {
                AfUserVirtualAccountDo virtualAccountInfo = BuildInfoUtil.buildUserVirtualAccountDo(orderInfo.getUserId(), orderInfo.getActualAmount(), orderInfo.getActualAmount(),
                        orderInfo.getRid(), orderInfo.getOrderNo(), virtualCode);
                //增加虚拟商品记录
                afUserVirtualAccountService.saveRecord(virtualAccountInfo);
            }

            orderInfo.setPayStatus(PayStatus.PAYED.getCode());
            orderInfo.setStatus(OrderStatus.PAID.getCode());
            orderInfo.setPayType(PayType.AGENT_PAY.getCode());
            // 新增借款信息
            afBorrowDao.addBorrow(borrow);
            // 在风控审批通过后额度不变生成账单
            afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(), userAccountInfo.getUserName(), orderInfo.getActualAmount(), PayType.AGENT_PAY.getCode(), orderInfo.getOrderType());
            // 修改用户账户信息
            AfUserAccountDo account = new AfUserAccountDo();
            account.setUsedAmount(orderInfo.getActualAmount());
            account.setUserId(userAccountInfo.getUserId());
            afUserAccountDao.updateUserAccount(account);

            logger.info("updateOrder orderInfo = {}", orderInfo);
            afOrderDao.updateOrder(orderInfo);
        }

        return "success";
    }

    //3.7.6初始化借钱缓存，用于app端高亮显示
    @RequestMapping(value = {"/initBorrowCache"}, method = RequestMethod.GET)
    @ResponseBody
    public void initBorrowCache() {
        logger.info("initBorrowCache,start");
        List<String> ids = afBorrowCashService.getBorrowedUserIds();
        if (ids != null) {
            bizCacheUtil.saveRedistSet(Constants.HAVE_BORROWED, ids);
        }
        logger.info("initBorrowCache,end");
    }


    @RequestMapping(value = {"/boluomeCoupon"}, method = RequestMethod.GET)
    @ResponseBody
    public void boluomeCoupon() {
        List<BrandCouponResponseBo> list = boluomeUtil.getUserCouponList(68885L, 1, 1, 20);
        logger.info("boluomeCoupon,start");
        List<BrandActivityCouponResponseBo> list1 = boluomeUtil.getActivityCouponList("https://dev-api.otosaas.com/bss/v1/apps/157/campaigns/775/give");
        logger.info("boluomeCoupon,end");
        System.out.println(boluomeUtil.isUserHasCoupon("https://dev-api.otosaas.com/bss/v1/apps/157/campaigns/775/give", 68885L, 1));

    }

    @Resource
    AfDeGoodsService afDeGoodsService;
    @Resource
    AfUserService afUserService;

    @RequestMapping(value = "/activity/de/goods/test", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public H5CommonResponse getGoodsList(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            JSONObject json = JSONObject.parseObject(body);
            String userName = json.getString("userName");
            AfUserDo user = afUserService.getUserByUserName(userName);
            Long userId = user.getRid();

            List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
            data.put("goodsList", userDeGoodsList);

            data.put("endTime", System.currentTimeMillis() / 1000 + 10000);
            data.put("totalCount", "100");

            return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
        } catch (Exception e) {
            logger.error("/activity/de/goods error = {}", e);
            return H5CommonResponse.getNewInstance(false, "获取砍价商品列表失败");
        }
    }

    public String getVirtualCode(Map<String, Object> resultMap) {
        if (resultMap == null) {
            return null;
        }
        if (resultMap.get(Constants.VIRTUAL_CODE) == null) {
            return null;
        }
        return resultMap.get(Constants.VIRTUAL_CODE).toString();
    }

    /**
     * @param name             分期名称
     * @param type             分期类型
     * @param userId           用户id
     * @param amount           分期金额
     * @param nper             分期期数
     * @param perAmount        每期金额
     * @param status           状态
     * @param orderId          订单id
     * @param orderNo          订单编号
     * @param borrowRate       借款利率等参数
     * @param interestFreeJson 分期规则
     * @return
     */
    private AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal amount, int nper, String status, Long orderId, String orderNo, String borrowRate, String interestFreeJson) {

        Integer freeNper = 0;
        List<InterestFreeJsonBo> interestFreeList = StringUtils.isEmpty(interestFreeJson) ? null : JSONObject.parseArray(interestFreeJson, InterestFreeJsonBo.class);
        if (CollectionUtils.isNotEmpty(interestFreeList)) {
            for (InterestFreeJsonBo bo : interestFreeList) {
                if (bo.getNper().equals(nper)) {
                    freeNper = bo.getFreeNper();
                    break;
                }
            }
        }
        //拿到日利率快照Bo
        BorrowRateBo borrowRateBo = BorrowRateBoUtil.parseToBoFromDataTableStr(borrowRate);
        //每期本金
        BigDecimal principleAmount = amount.divide(new BigDecimal(nper), 2, RoundingMode.DOWN);
        //每期利息
        BigDecimal interestAmount = amount.multiply(borrowRateBo.getRate()).divide(Constants.DECIMAL_MONTH_OF_YEAR, 2, RoundingMode.CEILING);
        //每期手续费
        BigDecimal poundageAmount = BigDecimalUtil.getPerPoundage(amount, nper, borrowRateBo.getPoundageRate(), borrowRateBo.getRangeBegin(), borrowRateBo.getRangeEnd(), freeNper);

        BigDecimal perAmount = BigDecimalUtil.add(principleAmount, interestAmount, poundageAmount);

        Date currDate = new Date();
        AfBorrowDo borrow = new AfBorrowDo();
        borrow.setGmtCreate(currDate);
        borrow.setAmount(amount);
        borrow.setType(type.getCode());
        borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
        borrow.setStatus(status);// 默认转账成功
        borrow.setName(name);
        borrow.setUserId(userId);
        borrow.setNper(nper);
        borrow.setNperAmount(perAmount);
        borrow.setCardNumber(StringUtils.EMPTY);
        borrow.setCardName("代付");
        borrow.setRemark(name);
        borrow.setOrderId(orderId);
        borrow.setOrderNo(orderNo);
        borrow.setBorrowRate(borrowRate);
        borrow.setCalculateMethod(BorrowCalculateMethod.DENG_BEN_DENG_XI.getCode());
        borrow.setFreeNper(freeNper);
        return borrow;
    }

    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;

    @Resource
    AfYibaoOrderDao afYiBaoOrderDao;
    @Resource
    private AfRepaymentDetalDao afRepaymentDetalDao;

    @Resource
    HuichaoUtility huichaoUtility;

    /**
     *
     */
    @RequestMapping(value = {"/testYiBao"}, method = RequestMethod.GET)
    public void testAddYiBao() {
//		String appid =  AesUtil.decrypt("Ehw14/ML0cbFSiBoVFC1mu6iw/dWDLmwlSlJTCWC/veLZVdz4LtvQ7My3Rfnuzwq/du56FGZDN1TRs9yv/Zn+4N2RXBD1dBHtugwJDhi3Bs=","Cw5bM6x@6sH$2dlw^3JueH");
//		String puk = AesUtil.decrypt("QRaDVWb2pC9by28Rxt8sMQ==","Cw5bM6x@6sH$2dlw^3JueH");
//		String pik = AesUtil.decrypt("fnzwqgFMW/RyuzDKRkH9uP/XN6RgBH5QkvtGwHR2gVs=","Cw5bM6x@6sH$2dlw^3JueH");
//
//		AesUtil.encryptToBase64(appid,"Cw5bM6x@6sH$2dlw^3JueH");

//		Map m = yiBaoUtility.createOrder(BigDecimal.TEN,"hk"+String.valueOf (new Date().getTime()/1000),20158l,PayOrderSource.BORROWCASH);
//		String token =  m.get("token").toString();
//		String d= yiBaoUtility.getCashier(token,20158l);
        //Map<String,String> addda = yiBaoUtility.getYiBaoOrder("hq2017090815262700180","1001201709080000000015990156");
//		String e="";
        //huichaoUtility.getHuiCaoOrder("1509598766744");

        huichaoUtility.getOrderStatus("xj2017112411401400326");

        //HashMap srce =  huichaoUtility.createOrderZFB("zfaaaabcdef1","1",13989456178L, PayOrderSource.RENEWAL_PAY);
        //afYiBaoOrderDao.updateYiBaoOrderStatusByOrderNo("adfasdfadsf11111",1);
        String e = "";
    }


    private List<HashMap> getListByStatus(List<AfBorrowBillDo> list, int status) {
        List<HashMap> _list = new ArrayList<HashMap>();
        for (AfBorrowBillDo afBorrowBillDo : list) {
            HashMap map = getBillByStatus(afBorrowBillDo, status);
            if (map != null && map.size() > 0) {
                _list.add(map);
            }
        }
        return _list;
    }

    /**
     * @param afBorrowDo
     * @param status     1 己出，2 逾期，3 未出
     * @return
     */
    private HashMap getBillByStatus(AfBorrowBillDo afBorrowBillDo, int status) {

        HashMap map = new HashMap();
        if (status == 1) {
            if (afBorrowBillDo.getOverdueStatus().equals("Y")) {
                return map;
            }
            if (isOut(afBorrowBillDo.getBillYear(), afBorrowBillDo.getBillMonth())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        } else if (status == 2) {
            if (afBorrowBillDo.getOverdueStatus().equals("Y")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        } else if (status == 3) {
            if (!isOut(afBorrowBillDo.getBillYear(), afBorrowBillDo.getBillMonth())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date", date);
                map.put("name", afBorrowBillDo.getName());
                map.put("totalAmount", afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        return map;
    }


    private boolean isOut(int year, int month) {
        Date d = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR, year);
        c1.set(Calendar.MONTH, month - 1);
        c1.set(Calendar.DAY_OF_MONTH, 10);
        c1.set(Calendar.HOUR_OF_DAY, 0);
        c1.set(Calendar.MINUTE, 0);
        c1.set(Calendar.SECOND, 0);
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String a = s.format(c1.getTime());
        boolean flag = c1.getTime().before(d);
        return flag;
    }


    @Resource
    AfUserOutDayDao afUserOutDayDao;

    /**
     * 帐单日处理
     *
     * @param userId
     */
    public void addOutDay(long userId) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            int out_day = calendar.get(Calendar.DAY_OF_MONTH);
            List<Integer> _nodays = new ArrayList<>();
            _nodays.add(19);
            _nodays.add(20);
            List<Integer> _nodays1 = new ArrayList<>();
            _nodays1.add(29);
            _nodays1.add(30);
            _nodays1.add(31);
            AfUserOutDayDo afUserOutDayDo = new AfUserOutDayDo();
            int pay_day = 0;
            if (_nodays.contains(out_day)) {
                out_day = 21;
                pay_day = 1;

            } else if (_nodays1.contains(out_day)) {
                out_day = 1;
                pay_day = 11;
            } else {
                pay_day = out_day + 10;
                if (pay_day > 30) pay_day = pay_day - 30;

            }
            afUserOutDayDo.setUserId(userId);
            afUserOutDayDo.setOutDay(out_day);
            afUserOutDayDo.setPayDay(pay_day);
            afUserOutDayDao.addserOutDay(afUserOutDayDo);
        } catch (Exception e) {
            logger.info("add out_day error:", e);
            logger.error("add out_day error", e);
        }
    }


}
