package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.SmartAddressEngine;
import com.ald.fanbei.api.common.enums.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushType;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.ApplyLegalBorrowCashService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.util.LogUtil;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;
import com.alibaba.fastjson.JSON;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱V2
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyLegalBorrowCashV2Api")
@Validator("applyLegalBorrowCashParam")
public class ApplyLegalBorrowCashV2Api extends GetBorrowCashBase implements ApiHandle {

	// [start] 依赖注入
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	ApplyLegalBorrowCashService applyLegalBorrowCashService;
	@Resource
	SmartAddressEngine smartAddressEngine;

	// [end]
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Long userId = context.getUserId();
		// 获取客户端请求参数
		ApplyLegalBorrowCashParam param = (ApplyLegalBorrowCashParam) requestDataVo.getParamObj();
		try{
			AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("enabled_type_borrow");//是否允许这种类型的借款
			if(afResourceDo!=null&&afResourceDo.getValue().equals(YesNoStatus.YES.getCode())&&afResourceDo.getValue1().contains(param.getType())){
				throw new FanbeiException(afResourceDo.getValue2(),true);
			}
		}catch (FanbeiException e){
			throw e;

		}catch (Exception e){
			logger.error("enabled_type_borrow error",e);
		}
		ApplyLegalBorrowCashBo paramBo =  new ApplyLegalBorrowCashBo();

		BeanUtil.copyProperties(paramBo,param);
		paramBo.setIpAddress(CommonUtil.getIpAddr(request));
		paramBo.setAppName(getAppType(requestDataVo));
		// 获取用户账户和认证信息
		AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);

		// 获取后台配置借款利率信息
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
				Constants.BORROW_CASH_INFO_LEGAL_NEW);
		// 获取主卡信息
		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		// 业务逻辑校验
		applyLegalBorrowCashService.checkBusi(accountDo, authDo, rateInfoDo, mainCard, paramBo);
		try {
			// 业务加锁处理
			applyLegalBorrowCashService.checkLock(lockKey);
			
			AfBorrowCashDo afBorrowCashDo = applyLegalBorrowCashService.buildBorrowCashDo(
					mainCard, userId, rateInfoDo, paramBo);
			// 用户借钱时app来源区分
			String appName = getAppName(requestDataVo);
			afBorrowCashDo.setMajiabaoName(appName);
			// 搭售商品订单
			AfBorrowLegalOrderDo afBorrowLegalOrderDo = applyLegalBorrowCashService.buildBorrowLegalOrder(userId,
					paramBo);
			// 数据库中新增借钱记录
			Long borrowId = applyLegalBorrowCashService.addBorrowRecord(afBorrowCashDo,afBorrowLegalOrderDo);
			// 生成借款信息失败
			applyLegalBorrowCashService.checkGenRecordError(borrowId);
			
			// 借过款的放入缓存，借钱按钮不需要高亮显示
			bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(userId));
			
			String appType = getAppType(requestDataVo);
			String ipAddress = CommonUtil.getIpAddr(request);
			
			try {
				String cardNo = mainCard.getCardNumber();
				String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
				// 更新风控审核状态未待审核
				applyLegalBorrowCashService.updateBorrowStatus2Apply(borrowId,riskOrderNo);
				
				// 提交风控审核
				RiskVerifyRespBo verifyBo = applyLegalBorrowCashService.submitRiskReview(borrowId,appType,ipAddress,paramBo,
						accountDo,userId,afBorrowCashDo,riskOrderNo);
				if (verifyBo.isSuccess()) {
					//风控审核通过,根据开关判断是否推送钱包打款
					AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
					AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
					if (StringUtil.equals(assetPushType.getBorrowCash(), YesNoStatus.YES.getCode())) {
/*						FanbeiBorrowBankInfoBo fanbeiBorrowBankInfoBo = assetPackageSystemUtil.getAssetSideBankInfo(assetPackageSystemUtil.getAssetSideBankInfo());
						EdspayGetCreditRespBo borrowCashInfo =new EdspayGetCreditRespBo();
						borrowCashInfo.setDebtType(0);
						borrowCashInfo.setOrderNo(orderNo);
						
						Integer timeLimit = NumberUtil.objToIntDefault(afAssetPackageDetailDto.getType(), null);
						AfAssetPackageRepaymentType repayTypeEnum = AfAssetPackageRepaymentType.findEnumByCode(afAssetPackageDo.getRepaymentMethod());
						if (StringUtils.equals(afAssetPackageDetailDto.getType(), minBorrowTime)) {
							borrowRate=new BigDecimal((String)jsonObject.get("borrowFirstType"));
						}else{
							borrowRate=new BigDecimal((String)jsonObject.get("borrowSecondType")) ;
						}
						borrowCashInfo borrowCashInfo = new borrowCashInfo();
						borrowCashInfo.setDebtType(0);
						borrowCashInfo.setOrderNo(afAssetPackageDetailDto.getOrderNo());
						borrowCashInfo.setUserId(afAssetPackageDetailDto.getUserId());
						borrowCashInfo.setName(afAssetPackageDetailDto.getName());
						borrowCashInfo.setCardId(afAssetPackageDetailDto.getCardId());
						borrowCashInfo.setMobile(afAssetPackageDetailDto.getMobile());
						borrowCashInfo.setBankNo(afAssetPackageDetailDto.getBankNo());
						borrowCashInfo.setAcctName(fanbeiBorrowBankInfoBo.getAcctName());
						borrowCashInfo.setMoney(afAssetPackageDetailDto.getMoney());
						borrowCashInfo.setApr(borrowRate);
						borrowCashInfo.setTimeLimit(timeLimit);
						borrowCashInfo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afAssetPackageDetailDto.getLoanStartTime()));
						if (StringUtil.isNotBlank(afAssetPackageDetailDto.getBorrowRemark())) {
							borrowCashInfo.setPurpose(afAssetPackageDetailDto.getBorrowRemark());
						}else {
							borrowCashInfo.setPurpose("个人消费");
						}
						borrowCashInfo.setRepaymentStatus(0);
						borrowCashInfo.setRepayName(fanbeiBorrowBankInfoBo.getRepayName());
						borrowCashInfo.setRepayAcct(fanbeiBorrowBankInfoBo.getRepayAcct());
						borrowCashInfo.setRepayAcctBankNo(fanbeiBorrowBankInfoBo.getRepayAcctBankNo());
						borrowCashInfo.setRepayAcctType(fanbeiBorrowBankInfoBo.getRepayAcctType());
						borrowCashInfo.setIsRepayAcctOtherBank(fanbeiBorrowBankInfoBo.getIsRepayAcctOtherBank());
						borrowCashInfo.setManageFee(afAssetSideInfoDo.getAnnualRate());
						if (StringUtil.isNotBlank(afAssetPackageDetailDto.getRefundRemark())) {
							borrowCashInfo.setRepaymentSource(afAssetPackageDetailDto.getRefundRemark());
						}else {
							borrowCashInfo.setRepaymentSource("工资收入");
						}
						borrowCashInfo.setIsPeriod(0);
						borrowCashInfo.setTotalPeriod(1);
						borrowCashInfo.setLoanerType(0);
						//用户平台逾期信息
						AfUserBorrowCashOverdueInfoDto currOverdueInfo = afBorrowCashService.getOverdueInfoByUserId(afAssetPackageDetailDto.getUserId());
						borrowCashInfo.setOverdueTimes(currOverdueInfo.getoverdueTimes());
						borrowCashInfo.setOverdueAmount(currOverdueInfo.getOverdueAmount());
						//还款计划
						List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
						RepaymentPlan repaymentPlan = new RepaymentPlan();
						repaymentPlan.setRepaymentNo(afAssetPackageDetailDto.getOrderNo());
						repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(DateUtil.addDays(afAssetPackageDetailDto.getLoanStartTime(), timeLimit)));
						repaymentPlan.setRepaymentDays(timeLimit);
						repaymentPlan.setRepaymentAmount(afAssetPackageDetailDto.getMoney());
						repaymentPlan.setRepaymentInterest(BigDecimalUtil.multiply(afAssetPackageDetailDto.getMoney(), new BigDecimal(afAssetSideInfoDo.getBorrowRate().doubleValue()*timeLimit / 36000d)));
						repaymentPlan.setRepaymentPeriod(0);
						repaymentPlans.add(repaymentPlan);
						borrowCashInfo.setRepaymentPlans(repaymentPlans);*/
					}
					// 风控审核通过，提交ups进行打款处理
					applyLegalBorrowCashService.delegatePay(verifyBo.getConsumerNo(), verifyBo.getOrderNo(),
							verifyBo.getResult(), afBorrowLegalOrderDo, mainCard, afBorrowCashDo);
					// 增加借款埋点信息
					doMaidianLog(request, afBorrowCashDo, requestDataVo, context);
					//百度智能地址
					try {
						smartAddressEngine.setScoreAsyn(afBorrowLegalOrderDo.getAddress(),borrowId,afBorrowLegalOrderDo.getOrderNo());
					}catch (Exception e){
						logger.info("smart address {}",e);
					}
				} else {
					// 风控拒绝，更新借款状态
					AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
					delegateBorrowCashDo.setRid(borrowId);
					delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
					// 更新订单状态为关闭
					afBorrowLegalOrderDo.setStatus(OrderStatus.CLOSED.getCode());
					afBorrowLegalOrderDo.setClosedDetail("risk refuse");
					afBorrowLegalOrderDo.setGmtClosed(new Date());
					applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				}
				return resp;
			} catch (Exception e) {
				logger.error("apply legal borrow cash error,error msg=>{}", e.getMessage());
				AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
				delegateBorrowCashDo.setRid(borrowId);
				// 关闭借款
				delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				// 关闭搭售商品订单
				afBorrowLegalOrderDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());
				// 更新风控审核状态为拒绝
				delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				// 如果属于非返呗自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
				if (e instanceof FanbeiException) {
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				} else {
					logger.error("apply legal borrow cash exist unexpected exception,cause:{}" , e.getCause());
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
					delegateBorrowCashDo.setReviewDetails("弱风控认证存在捕获外异常");
				}
				applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
		} finally {
			bizCacheUtil.delCache(lockKey);
		}
	}
	
	private void doMaidianLog(HttpServletRequest request, AfBorrowCashDo afBorrowCashDo, RequestDataVo requestDataVo,
			FanbeiContext context) {
		String ext1 = afBorrowCashDo.getBorrowNo();
		String ext2 =  ObjectUtils.toString(afBorrowCashDo.getUserId());
		String ext3 =  ObjectUtils.toString(afBorrowCashDo.getAmount());
		String ext4 =  ObjectUtils.toString(context.getAppVersion());
		LogUtil.doMaidianLog(request, afBorrowCashDo, requestDataVo, context, ext1, ext2, ext3, ext4);
		
	}

	private String getAppName(RequestDataVo requestDataVo) {
		String appName = requestDataVo.getId().substring(requestDataVo.getId().lastIndexOf("_") + 1,
				requestDataVo.getId().length());
		return appName;
	}

	private String getAppType(RequestDataVo requestDataVo) {
		return (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
	}

}
