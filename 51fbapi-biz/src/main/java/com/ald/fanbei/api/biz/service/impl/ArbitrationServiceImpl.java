package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfLenderInfoDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.ArbitrationRespBo;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ArbitrationStatus;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfArbitrationDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：在线仲裁系统ServiceImpl
 * @author fanmanfu
 * @version 创建时间：2018年4月13日 上午11:08:59
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("ArbitrationService")
public class ArbitrationServiceImpl extends BaseService implements
	ArbitrationService {
    @Resource
    AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfUserService afUserService;
	@Resource
	AfContractPdfService afContractPdfService;
    @Resource
    AfArbitrationDao afArbitrationDao;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfIdNumberService afIdNumberService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfBorrowLegalOrderDao afBorrowLegalOrderDao;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
    @Resource
    AfBorrowCashOverdueService afBorrowCashOverdueService;
    @Resource
    AfFundSideBorrowCashService afFundSideBorrowCashService;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;

    @Override
    public ArbitrationRespBo getOrderInfo(String borrowNo) {

	ArbitrationRespBo resp = new ArbitrationRespBo();
	Map<String, Object> result = new HashMap<String, Object>();

	try {
	    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
		    .getBorrowCashInfoByBorrowNo(borrowNo);
	    if (afBorrowCashDo == null) {
		logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
		resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
		resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

		return resp;
	    }

	    // 搭售V1
	    if (afBorrowLegalOrderCashDao.tuchByBorrowId(afBorrowCashDo
		    .getRid()) != null) {
		logger.info("order is borrowCashV1,borrowNo= " + borrowNo);
		resp.setErrCode(ArbitrationStatus.BORROWCASHV1.getCode());
		resp.setErrMsg(ArbitrationStatus.BORROWCASHV1.getName());

		return resp;
	    }// 搭售V2
	    else if (afBorrowLegalOrderService.isV2BorrowCash(afBorrowCashDo
		    .getRid())) {
		AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderDao
			.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo
				.getRid());
		result.put("amtCase", (BigDecimalUtil.add(
			afBorrowCashDo.getAmount(),
			afBorrowCashDo.getRateAmount(),
			afBorrowCashDo.getPoundage(),
				new BigDecimal(0.015).multiply(new BigDecimal(afBorrowCashDo.getOverdueDay()-1))
						.multiply(afBorrowCashDo.getAmount()).divide(new BigDecimal(360),2,RoundingMode.HALF_UP))
			.subtract(afBorrowCashDo.getRepayAmount()))
			.multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 标的金额:实际借款金额+利息+服务费+罚息+其他金额-已还金额
	    }
	    // 旧版借款
	    else {
		/*
		 * logger.info("order is oldborrowCash,borrowNo= "+borrowNo);
		 * resp.setErrCode(ArbitrationStatus.OLDBORROWCASH.getCode());
		 * resp.setErrMsg(ArbitrationStatus.OLDBORROWCASH.getName());
		 * 
		 * return resp;
		 */
		result.put("amtCase", (BigDecimalUtil.add(
			afBorrowCashDo.getAmount(),
			afBorrowCashDo.getRateAmount(),
			afBorrowCashDo.getPoundage(),
			afBorrowCashDo.getOverdueAmount(),
			afBorrowCashDo.getSumOverdue(),
			afBorrowCashDo.getSumRate()).subtract(afBorrowCashDo
			.getRepayAmount()))
			.multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 标的金额:实际借款金额+利息+服务费+罚息+其他金额-已还金额

	    }

	    AfUserAccountDo userAccountDo = afUserAccountService
		    .getUserAccountByUserId(afBorrowCashDo.getUserId());

	    AfResourceDo lenderDo = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.borrowRate.getCode(),
			    AfResourceSecType.borrowCashLenderForCash.getCode());

	    AfResourceDo resource = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.ARBITRATION_TYPE.getCode(),
			    AfResourceType.ARBITRATION_SEC_TYPE.getCode());

	    Map<String, Object> json = (Map<String, Object>) JSONObject
		    .parse(resource.getValue3());
	    String productId = StringUtil.null2Str(json.get("productId"));
	    String platName = StringUtil.null2Str(json.get("platName"));
	    Integer serviceType = Integer.parseInt(StringUtil.null2Str(json
		    .get("serviceType")));
	    String negotiatePhone = StringUtil.null2Str(json
		    .get("negotiatePhone"));

		AfContractPdfDo afContractPdfDo= afContractPdfService.getContractPdfDoByTypeAndTypeId(afBorrowCashDo.getRid(),(byte)1);
		Long pdfId=afContractPdfDo.getId();
		List<AfLenderInfoDto> lenders= afContractPdfService.selectLenders(pdfId);
		String lender="";
		logger.info("pdfId:"+pdfId+",lenders:"+JSON.toJSONString(lenders));
		for (AfLenderInfoDto lenderInfoDto:lenders) {
			lender=lender+lenderInfoDto.getUserName()+",";
		}
		lender=lender.substring(0,lender.lastIndexOf(","));
		result.put("lender", lender);// 出借人
	    result.put("amtCapital", afBorrowCashDo.getArrivalAmount()
		    .multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 实际打款金额
	    result.put("serviceType", serviceType); // 服务费预扣标识符
	    result.put("applicateDate", DateUtil
		    .formatDateForPatternWithHyhen(afBorrowCashDo
			    .getGmtCreate())); // 借款申请日期
	    result.put("signedDate", DateUtil
		    .formatDateForPatternWithHyhen(afBorrowCashDo
			    .getGmtCreate())); // 借款协议签订日期
	    result.put(
		    "amtLimit",
		    userAccountDo.getAuAmount().multiply(
			    BigDecimalUtil.ONE_HUNDRED).intValue()); // 可借款总额度
	    result.put("productId", productId); // 产品编码
	    result.put("platName", platName); // 所属平台
	    result.put("negotiatePhone", negotiatePhone); // 调解协商电话

	    resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
	    resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
	    resp.setResult(result);
	} catch (Exception e) {
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
		    + ",e= " + e);
	}
	return resp;
    }

    @Override
    public ArbitrationRespBo getFundInfo(String borrowNo) {

	ArbitrationRespBo resp = new ArbitrationRespBo();
	Map<String, Object> result = new HashMap<String, Object>();

	try {
	    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
		    .getBorrowCashInfoByBorrowNo(borrowNo);
	    if (afBorrowCashDo == null) {
            logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
            resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
            resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

            return resp;
	    }

	    AfUserAccountDo userAccountDo = afUserAccountService
		    .getUserAccountByUserId(afBorrowCashDo.getUserId());

	    AfResourceDo resource = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.ARBITRATION_TYPE.getCode(),
			    AfResourceType.ARBITRATION_SEC_TYPE.getCode());

	    Map<String, Object> json = (Map<String, Object>) JSONObject
		    .parse(resource.getValue3());
	    int countPeriods = Integer.parseInt(StringUtil.null2Str(json
		    .get("countPeriods")));// 获取期数

	    // 搭售V1
	    if (afBorrowLegalOrderCashDao.tuchByBorrowId(afBorrowCashDo
		    .getRid()) != null) {
            logger.info("order is borrowCashV1,borrowNo= " + borrowNo);
            resp.setErrCode(ArbitrationStatus.BORROWCASHV1.getCode());
            resp.setErrMsg(ArbitrationStatus.BORROWCASHV1.getName());

		    return resp;
	    }// 搭售V2
	    else if (afBorrowLegalOrderService.isV2BorrowCash(afBorrowCashDo
		    .getRid())) {

            // 从配置文件中获取利率，服务费利率,逾期费率start
            AfResourceDo afResourceDo = afResourceService
                .getConfigByTypesAndSecType(ResourceType.BORROW_RATE
                    .getCode(),
                    AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW
                        .getCode());
            String oneDay = "";
            String twoDay = "";
            if (null != afResourceDo) {
                oneDay = afResourceDo.getTypeDesc().split(",")[0];
                twoDay = afResourceDo.getTypeDesc().split(",")[1];
            }
            JSONArray array = JSONObject.parseArray(afResourceDo
                .getValue2());
            double interestRate = 0; // 利率
            double serviceRate = 0; // 手续费率
            double overdueRate = 0; // 逾期费率
            for (int i = 0; i < array.size(); i++) {
                JSONObject info = array.getJSONObject(i);
                String borrowTag = info.getString("borrowTag");
                if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils
                    .equals(oneDay, afBorrowCashDo.getType())) {
                    interestRate = info.getDouble("borrowFirstType");
                } else if (StringUtils.equals(twoDay,
                    afBorrowCashDo.getType())) {
                    interestRate = info.getDouble("borrowSecondType");
                }
                } else if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils
                    .equals(oneDay, afBorrowCashDo.getType())) {
                    serviceRate = info.getDouble("borrowFirstType");
                } else if (StringUtils.equals(twoDay,
                    afBorrowCashDo.getType())) {
                    serviceRate = info.getDouble("borrowSecondType");
                }
                } else if (StringUtils.equals("OVERDUE_RATE", borrowTag)) {
                if (StringUtils
                    .equals(oneDay, afBorrowCashDo.getType())) {
                    overdueRate = info.getDouble("borrowFirstType");
                } else if (StringUtils.equals(twoDay,
                    afBorrowCashDo.getType())) {
                    overdueRate = info.getDouble("borrowSecondType");
                }
                }
            }
		   // end

            result.put("rateInterest",
					afBorrowCashDo.getRateAmount().multiply(new BigDecimal(360) ).divide(afBorrowCashDo.getAmount().multiply(new BigDecimal( afBorrowCashDo.getType())),2,RoundingMode.HALF_UP));// 利息利率
            result.put("rateService",0);
            result.put("rateOverdue",new BigDecimal(overdueRate).divide(new BigDecimal(36000),6,RoundingMode.HALF_UP));
	    } else { // 旧版借款
            AfResourceDo afResourceDo = afResourceService
                .getConfigByTypesAndSecType(ResourceType.BORROW_RATE
                    .getCode(),
                    AfResourceSecType.BORROW_CASH_OVERDUE_POUNDAGE
                        .getCode());

            result.put("rateInterest", afBorrowCashDo.getBaseBankRate());// 利息利率
            result.put("rateService", 0);// 服务费利率
            result.put("rateOverdue",
                new BigDecimal(afResourceDo.getValue()));// 逾期利率
	    }

	    result.put(
		    "amtBorrowed",
		    userAccountDo.getAuAmount().multiply(
			    BigDecimalUtil.ONE_HUNDRED).intValue());// 授信金额
	    result.put("amtCapital", afBorrowCashDo.getAmount().multiply(
		    BigDecimalUtil.ONE_HUNDRED).intValue());// 实际借款本金
		result.put("amtInterest", afBorrowCashDo.getRateAmount().multiply( BigDecimalUtil.ONE_HUNDRED).intValue());// 利息
		if("N".equals(afBorrowCashDo.getOverdueStatus())) {
		    result.put("amtPenalty", afBorrowCashDo.getOverdueAmount().multiply( BigDecimalUtil.ONE_HUNDRED).intValue());// 罚息
	    } else {
		    result.put("amtPenalty",new BigDecimal(0.015).multiply(new BigDecimal(afBorrowCashDo.getOverdueDay()-1))
					.multiply(afBorrowCashDo.getAmount()).divide(new BigDecimal(360),2,RoundingMode.HALF_UP).multiply(BigDecimalUtil.ONE_HUNDRED).intValue());
	    }
	    result.put("amtService",  afBorrowCashDo.getAmount().subtract(afBorrowCashDo.getArrivalAmount()).multiply( BigDecimalUtil.ONE_HUNDRED).intValue());// 服务费

	    result.put("amtOther",afBorrowCashDo.getPoundage().multiply( BigDecimalUtil.ONE_HUNDRED).intValue());// 其他费用
	    result.put("amtRefund", afBorrowCashDo.getRepayAmount().multiply( BigDecimalUtil.ONE_HUNDRED).intValue());// 已还费用
	    result.put("rateRefund", "");// 分期还款利率
	    result.put("periodsType", ArbitrationStatus.PERIODSTYPE.getCode());// 期数类型
	    result.put("countPeriods", countPeriods);// 期数
	    result.put("borrowStartDate", DateUtil
		    .formatDateForPatternWithHyhen(afBorrowCashDo
			    .getGmtArrival()));// 借款开始日期
	    result.put("borrowEndDate", DateUtil
				.formatDateForPatternWithHyhen(DateUtil.addDays(afBorrowCashDo
				.getGmtPlanRepayment(),1)));// 借款结束日期
	    result.put("daysBorrowed", afBorrowCashDo.getType());// 借款天数
	    result.put("violateStartDate", DateUtil
				.formatDateForPatternWithHyhen(DateUtil.addDays(afBorrowCashDo
                .getGmtPlanRepayment(),2)));// 违约金开始计算日期
	    result.put("violateEndDate", "");// 违约金结束计算日期
	    result.put("dayOverdue", afBorrowCashDo.getOverdueDay()-1);// 逾期天数
	    result.put("debtDate",DateUtil.formatDate( DateUtil.addDays( afBorrowCashDo.getGmtPlanRepayment(),2),"yyyy-MM-dd") );// 债转日期

	    resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
	    resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
	    resp.setResult(result);

	} catch (Exception e) {
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
		    + ",e= " + e);
	}
	return resp;
    }

    @Override
    public ArbitrationRespBo getLitiGants(String borrowNo, String type) {
		ArbitrationRespBo resp = new ArbitrationRespBo();
		List result = new ArrayList();
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
				.getBorrowCashInfoByBorrowNo(borrowNo);
			if (afBorrowCashDo == null) {
				logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
				resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
				resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

				return resp;
			}

			if(StringUtil.isBlank(type)) {
			   resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
				resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
				logger.info("type is null");
				return resp;
			}

			AfResourceDo resource = afResourceService
				.getConfigByTypesAndSecType(
					AfResourceType.ARBITRATION_TYPE.getCode(),
					AfResourceType.ARBITRATION_SEC_TYPE.getCode());

			Map<String, Object> json = (Map<String, Object>) JSONObject
				.parse(resource.getValue3());
			String regTime = StringUtil.null2Str(json.get("regTime"));	// 获取注册时间
			String productId = StringUtil.null2Str(json.get("productId"));	// 获取平台id

			if(ArbitrationStatus.ONE.getCode().equals(type)) {
				AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
				AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(afBorrowCashDo.getUserId());
				AfIdNumberDo afIdNumberDo = afIdNumberService.selectUserIdNumberByUserId(afBorrowCashDo.getUserId());

				map.put("platId", afUserDo.getRid());	//用户平台id
				map.put("ltype", Integer.parseInt(ArbitrationStatus.ONE.getCode()));	//当事人类型
				map.put("name", afIdNumberDo.getName());	//姓名
				map.put("idtype", Integer.parseInt(ArbitrationStatus.ZERO.getCode()));	//用户证件类型  0身份证1营业执照
				map.put("idcard", afIdNumberDo.getCitizenId());	//证件号
				map.put("type", Integer.parseInt(ArbitrationStatus.ZERO.getCode()));	//类型
				map.put("nation", afIdNumberDo.getNation());	//民族
				map.put("legalPerson", "");//法定代表人
				map.put("position", "");	//法定代表人职务
				map.put("sex", afIdNumberDo.getGender().equals("女")?Integer.parseInt(ArbitrationStatus.ZERO.getCode()):Integer.parseInt(ArbitrationStatus.ONE.getCode()));	//性别
				map.put("phone", userAccountDo.getUserName());	//联系电话
				map.put("email", "");	//电子邮箱
				map.put("idAddress", afIdNumberDo.getAddress());	//证件地址
				map.put("address", "");	//通讯地址
				map.put("img_01", afIdNumberDo.getIdFrontUrl());	//身份证正面照
				map.put("img_02", afIdNumberDo.getIdBehindUrl());	//身份证反面照
				map.put("regTime", afUserDo.getGmtCreate());	//注册时间
				result.add(map);

			} else if(ArbitrationStatus.ZERO.getCode().equals(type)) {
				String name=StringUtil.null2Str(json.get("name"));
				String legalPerson=StringUtil.null2Str(json.get("legalPerson"));
				String position=StringUtil.null2Str(json.get("position"));
				String sex=StringUtil.null2Str(json.get("sex"));
				String phone=StringUtil.null2Str(json.get("phone"));
				String idAddress=StringUtil.null2Str(json.get("idAddress"));
				String img_01=StringUtil.null2Str(json.get("img_01"));
				String idcard=StringUtil.null2Str(json.get("idcard"));
				map.put("platId", "");	//用户平台id
				map.put("ltype", Integer.parseInt(ArbitrationStatus.ZERO.getCode()));	//当事人类型
				map.put("name", name);	//姓名
				map.put("idtype", Integer.parseInt(ArbitrationStatus.ONE.getCode()));	//用户证件类型
				map.put("idcard", idcard);	//证件号
				map.put("type", Integer.parseInt(ArbitrationStatus.ONE.getCode()));	//类型
				map.put("nation", "");	//民族
				map.put("legalPerson", legalPerson);	//法定代表人
				map.put("position", position);	//法定代表人职务
				map.put("sex", sex);	//性别
				map.put("phone", phone);	//联系电话
				map.put("email", "law@edspay.com");	//电子邮箱
				map.put("idAddress", idAddress);	//证件地址
				map.put("address", "");	//通讯地址
				map.put("img_01", img_01);	//身份证正面照
				map.put("img_02", "");	//身份证反面照
				map.put("regTime", "");	//注册时间
				result.add(map);
			}





			resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
			resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
			resp.setResult(result);
		} catch (Exception e) {
			resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
			resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
			logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
				+ ",e= " + e);
		}
		return resp;
	
    }

    @Override
    public ArbitrationRespBo getCreditAgreement(String borrowNo) {

	ArbitrationRespBo resp = new ArbitrationRespBo();
	List result = new ArrayList();

	try {
	    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
		    .getBorrowCashInfoByBorrowNo(borrowNo);
	    if (afBorrowCashDo == null) {
		logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
		resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
		resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

		return resp;
	    }

	    Map<String, Object> borrowMap = new HashMap<String, Object>();

	    AfUserAccountDo userAccountDo = afUserAccountService
		    .getUserAccountByUserId(afBorrowCashDo.getUserId());

	    AfResourceDo resource = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.ARBITRATION_TYPE.getCode(),
			    AfResourceType.ARBITRATION_SEC_TYPE.getCode());

	    Map<String, Object> json = (Map<String, Object>) JSONObject
		    .parse(resource.getValue3());
	    int protocolType = Integer.parseInt(StringUtil.null2Str(json
		    .get("protocolType")));// 获取类型
	    int pageSize = Integer.parseInt(StringUtil.null2Str(json
		    .get("pageSize")));// 获取协议页数
	    String protocolURL = StringUtil.null2Str(json.get("protocolURL")); // 获取Url
	    String signOffer = StringUtil.null2Str(json.get("signOffer")); // 获取电子签提供商
	    String provedObject = StringUtil.null2Str(json.get("provedObject")); // 获取协议名称

	    // Map params = new HashMap<>();
	    /*
	     * params.put("borrowNo", afBorrowCashDo.getBorrowNo());
	     * params.put("type", "1"); String data =
	     * JsonUtil.toJSONString(params); String timestamp =
	     * DateUtil.formatDate(new Date()); String sign =
	     * DigestUtil.MD5(data); Map<String,String> paramsT = new
	     * HashMap<>(); paramsT.put("data",data); paramsT.put("sign",sign);
	     * paramsT.put("timestamp",timestamp); String
	     * param="data="+data+"&sign="+sign+"&timestamp="+timestamp;
	     */
	    String param = "userName=" + userAccountDo.getUserName() + "&type="
		    + afBorrowCashDo.getType() + "&borrowId="
		    + afBorrowCashDo.getRid() + "&borrowAmount="
		    + afBorrowCashDo.getAmount();

		borrowMap.put("type", 1); // 协议类型 0 网站注册协议，1 借款协议，2 借款咨询服务协议
		borrowMap.put("signOffer", signOffer); // 电子签提供商
		AfArbitrationDo arbitrationDo =getByBorrowNo(borrowNo);
		borrowMap.put("agreeUrl", arbitrationDo.getValue1()); // 协议地址
		borrowMap.put("agreeNo", afBorrowCashDo.getBorrowNo()); // 协议编号
		borrowMap.put("provedObject", "借款协议"); // 证明对象
		borrowMap.put("pageSize", 7); // 协议页数·

	    result.add(borrowMap);

		Map<String, Object> panformMap = new HashMap<String, Object>();
		panformMap.put("type", 2); // 协议类型 0 网站注册协议，1 借款协议，2 借款咨询服务协议
		panformMap.put("signOffer", signOffer); // 电子签提供商
		panformMap.put("agreeUrl", arbitrationDo.getValue2()); // 协议地址
		panformMap.put("agreeNo", "pl"+afBorrowCashDo.getBorrowNo()); // 协议编号
		panformMap.put("provedObject", "借款咨询服务协议"); // 证明对象
		panformMap.put("pageSize", 5); // 协议页数·

		result.add(panformMap);
		Map<String, Object> lenderMap = new HashMap<String, Object>();
		lenderMap.put("type", 3); // 协议类型 0 网站注册协议，1 借款协议，2 借款咨询服务协议
		lenderMap.put("signOffer", signOffer); // 电子签提供商
		lenderMap.put("agreeUrl", arbitrationDo.getValue3()); // 协议地址
		lenderMap.put("agreeNo", "rc"+afBorrowCashDo.getBorrowNo()); // 协议编号
		lenderMap.put("provedObject", "收据"); // 证明对象
		lenderMap.put("pageSize", 1); // 协议页数·

		result.add(lenderMap);

	    resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
	    resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
	    resp.setResult(result);

	} catch (Exception e) {
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
		    + ",e= " + e);
	}
	return resp;
    }

    @Override
    public ArbitrationRespBo getCreditInfo(String borrowNo) {
	ArbitrationRespBo resp = new ArbitrationRespBo();
	List result = new ArrayList();

	try {
	    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
		    .getBorrowCashInfoByBorrowNo(borrowNo);
	    if (afBorrowCashDo == null) {
		logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
		resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
		resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

		return resp;
	    }

	    Map<String, Object> map = new HashMap<String, Object>();

	    AfResourceDo resource = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.ARBITRATION_TYPE.getCode(),
			    AfResourceType.ARBITRATION_SEC_TYPE.getCode());

	    Map<String, Object> json = (Map<String, Object>) JSONObject
		    .parse(resource.getValue3());
	    int countPeriods = Integer.parseInt(StringUtil.null2Str(json
		    .get("countPeriods")));// 获取期数

	    map.put("platId", ""); // 平台id
	    map.put("borrowNo", afBorrowCashDo.getBorrowNo()); // 借款编号
	    map.put("amtBorrow",
		    afBorrowCashDo.getAmount().multiply(
			    BigDecimalUtil.ONE_HUNDRED).intValue()); // 借款金额
	    map.put("borrowDate", DateUtil
		    .formatDateForPatternWithHyhen(afBorrowCashDo
			    .getGmtCreate())); // 借款时间
	    map.put("bankNo", afBorrowCashDo.getCardNumber()); // 借款银行卡号
	    map.put("billStatus",
		    afBorrowCashDo.getStatus().equals(ArbitrationStatus.TRANSED
					.getCode() ) ? ArbitrationStatus.TRANSED.getDec()
			    : (afBorrowCashDo.getStatus().equals(ArbitrationStatus.FINSH
					.getCode()) ? ArbitrationStatus.FINSH
				    .getDec() : ArbitrationStatus.CLOSED
				    .getDec())); // 借款状态
	    map.put("refundStatus", ""); // 还款状态
	    map.put("daysInstalment", countPeriods); // 分期数
	    map.put("loadDate", DateUtil
		    .formatDateForPatternWithHyhen(afBorrowCashDo
			    .getGmtPlanRepayment())); // 借款到期时间

	    result.add(map);

	    resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
	    resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
	    resp.setResult(result);

	} catch (Exception e) {
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
		    + ",e= " + e);
	}
	return resp;
    }

    @Override
    public ArbitrationRespBo getRefundInfo(String borrowNo) {
	ArbitrationRespBo resp = new ArbitrationRespBo();
	List result = new ArrayList();

	try {
	    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
		    .getBorrowCashInfoByBorrowNo(borrowNo);
	    if (afBorrowCashDo == null) {
		logger.info("afBorrowCashDo not exist,borrowNo= " + borrowNo);
		resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
		resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());

		return resp;
	    }

	    List<AfRepaymentBorrowCashDo> list = afRepaymentBorrowCashService
		    .getRepaymentBorrowCashByBorrowId(afBorrowCashDo.getRid());
	    if(list.size()<1 || list == null) {
	    	logger.info("AfRepaymentBorrowCashDo not exist,borrowNo= " + borrowNo);
	    	resp.setErrCode(ArbitrationStatus.ORDERNOTEXIST.getCode());
			resp.setErrMsg(ArbitrationStatus.ORDERNOTEXIST.getName());
			return resp;
	    }
	    
	    AfResourceDo resource = afResourceService
		    .getConfigByTypesAndSecType(
			    AfResourceType.ARBITRATION_TYPE.getCode(),
			    AfResourceType.ARBITRATION_SEC_TYPE.getCode());

	    Map<String, Object> json = (Map<String, Object>) JSONObject
		    .parse(resource.getValue3());
	    int countPeriods = Integer.parseInt(StringUtil.null2Str(json
		    .get("countPeriods")));// 获取期数

	    for (AfRepaymentBorrowCashDo afRepaymentBorrowCashDo : list) {

		Map<String, Object> map = new HashMap<String, Object>();

		// 搭售V1
		if (afBorrowLegalOrderCashDao.tuchByBorrowId(afBorrowCashDo
			.getRid()) != null) {
		    logger.info("order is borrowCashV1,borrowNo= " + borrowNo);
		    resp.setErrCode(ArbitrationStatus.BORROWCASHV1.getCode());
		    resp.setErrMsg(ArbitrationStatus.BORROWCASHV1.getName());

		    return resp;
		}// 搭售V2
		else if (afBorrowLegalOrderService
			.isV2BorrowCash(afBorrowCashDo.getRid())) {
		    map.put("amtRefund", (BigDecimalUtil.add(
			    afBorrowCashDo.getAmount(),
			    afBorrowCashDo.getOverdueAmount(),
			    afBorrowCashDo.getSumOverdue(),
			    afBorrowCashDo.getRateAmount(),
			    afBorrowCashDo.getSumRate(),
			    afBorrowCashDo.getPoundage(),
			    afBorrowCashDo.getSumRenewalPoundage())
			    .subtract(afBorrowCashDo.getRepayAmount()))
			    .multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 需还金额
		}
		// 旧版借款
		else {
		    map.put("amtRefund", (BigDecimalUtil.add(
			    afBorrowCashDo.getAmount(),
			    afBorrowCashDo.getOverdueAmount(),
			    afBorrowCashDo.getSumOverdue(),
			    afBorrowCashDo.getRateAmount(),
			    afBorrowCashDo.getSumRate())
			    .subtract(afBorrowCashDo.getRepayAmount()))
			    .multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 需还金额

		}

		map.put("platId", ""); // 平台id
		map.put("refundNo", afRepaymentBorrowCashDo.getRepayNo()); // 还款编号
		map.put("refundDate", DateUtil
			.formatDateForPatternWithHyhen(afRepaymentBorrowCashDo
				.getGmtCreate())); // 还款时间
		map.put("expireDate", DateUtil
			.formatDateForPatternWithHyhen(afBorrowCashDo
				.getGmtPlanRepayment())); // 到期时间
		map.put("daysInstalment", countPeriods); // 当前期数

		map.put("amtActural", afRepaymentBorrowCashDo.getActualAmount().multiply(BigDecimalUtil.ONE_HUNDRED).intValue()); // 实际还款
		map.put("refundType", afRepaymentBorrowCashDo.getName()); // 还款方式
		if (afRepaymentBorrowCashDo.getCardNumber() == "") {
		    map.put("bankNo", afRepaymentBorrowCashDo.getCardNumber()); // 还款银行卡号
		} else {
		    map.put("bankNo", afBorrowCashDo.getCardNumber()); //
		}
		map.put("refundStatus",
			afRepaymentBorrowCashDo.getStatus() == "Y" ? ArbitrationStatus.REPAYSUCCESS
				.getDec() : ArbitrationStatus.REPAYFAILURE
				.getDec()); // 还款状态
		result.add(map);
	    }
	    resp.setErrCode(ArbitrationStatus.SUCCESS.getCode());
	    resp.setErrMsg(ArbitrationStatus.SUCCESS.getName());
	    resp.setResult(result);

	} catch (Exception e) {
	    resp.setErrCode(ArbitrationStatus.FAILURE.getCode());
	    resp.setErrMsg(ArbitrationStatus.FAILURE.getName());
	    logger.info("getOrderInfo is Exception,borrowNo= " + borrowNo
		    + ",e= " + e);
	}
	return resp;
    }

    @Override
    public ArbitrationRespBo getPayVoucher(String loanBillNo) {
		AfBorrowCashDo afBorrowCashDo = afBorrowCashDao
				.getBorrowCashInfoByBorrowNo(loanBillNo);

        ArbitrationRespBo arbitrationRespBo=new ArbitrationRespBo();
        arbitrationRespBo.setErrCode("0000");
        arbitrationRespBo.setErrMsg("");
        HashMap dataMap=new HashMap();
		AfArbitrationDo arbitrationDo=  getByBorrowNo(loanBillNo);
        dataMap.put("voucherUrl",arbitrationDo.getPayVoucher());
        dataMap.put("voucherNo","po"+loanBillNo);
        dataMap.put("voucherOffer","宝付");
        dataMap.put("provedObject","");
		dataMap.put("voucherDate",DateUtil.formatDate(afBorrowCashDo.getGmtArrival(),"yyyy-MM-dd"));

        List dataList=new ArrayList();
        dataList.add(dataMap);
        arbitrationRespBo.setResult(dataList);
        return arbitrationRespBo;
    }

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

    @Override
    public int saveRecord(AfArbitrationDo afArbitrationDo) {
	return afArbitrationDao.saveRecord(afArbitrationDo);
    }

    @Override
    public int updateByloanBillNo(AfArbitrationDo afArbitrationDo) {
	return afArbitrationDao.updateByloanBillNo(afArbitrationDo);
    }

    @Override
    public AfArbitrationDo getByBorrowNo(String borrowNo) {
    	AfArbitrationDo query=new AfArbitrationDo();
    	query.setLoanBillNo(borrowNo);
        return afArbitrationDao.getByCommonCondition(query);
    }

}

