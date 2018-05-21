package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.biz.util.SmartAddressEngine;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.ApplyLegalBorrowCashBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushType;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.service.impl.ApplyLegalBorrowCashServiceImpl;
import com.ald.fanbei.api.biz.service.impl.ApplyLegalBorrowCashServiceImpl.ApplyCheckBo;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BeanUtil;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowCashDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.common.util.LogUtil;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	@Resource
	AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfAssetSideInfoService afAssetSideInfoService;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	@Resource
	AfUserService afUserService;
	@Resource
	JpushService jpushService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	NumberWordFormat numberWordFormat;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	ApplyLegalBorrowCashServiceImpl applyLegalBorrowCashServiceImpl;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfBorrowCashPushService afBorrowCashPushService;
	// [end]
	@Override
	public ApiHandleResponse process(final RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		final Long userId = context.getUserId();
		String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
		// 业务加锁处理
		applyLegalBorrowCashService.checkLock(lockKey);
		try {
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
			final ApplyLegalBorrowCashBo paramBo =  new ApplyLegalBorrowCashBo();
	
			BeanUtil.copyProperties(paramBo,param);
			paramBo.setIpAddress(CommonUtil.getIpAddr(request));
			paramBo.setAppName(getAppType(requestDataVo));
			// 获取用户账户和认证信息
			final AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
			final AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
	
			// 获取后台配置借款利率信息
			final AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,
					Constants.BORROW_CASH_INFO_LEGAL_NEW);
			// 获取主卡信息
			final AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
			
			final ApplyCheckBo applyCheckBo = new ApplyCheckBo();
			
			transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					// 业务逻辑校验
					applyLegalBorrowCashService.checkBusi(accountDo, authDo, rateInfoDo, mainCard, paramBo);
					
					applyCheckBo.afBorrowCashDo = applyLegalBorrowCashService.buildBorrowCashDo(
							mainCard, userId, rateInfoDo, paramBo);
					// 用户借钱时app来源区分
					String appName = getAppName(requestDataVo);
					applyCheckBo.afBorrowCashDo.setMajiabaoName(appName);
					// 搭售商品订单
					applyCheckBo.afBorrowLegalOrderDo = applyLegalBorrowCashService.buildBorrowLegalOrder(userId,
							paramBo);
					// 数据库中新增借钱记录
					applyCheckBo.borrowId = applyLegalBorrowCashService.addBorrowRecord(applyCheckBo.afBorrowCashDo,applyCheckBo.afBorrowLegalOrderDo);
					return 1l;
				}
			});
			
			final AfBorrowCashDo afBorrowCashDo = applyCheckBo.afBorrowCashDo;
			final AfBorrowLegalOrderDo afBorrowLegalOrderDo = applyCheckBo.afBorrowLegalOrderDo;
			Long borrowId = applyCheckBo.borrowId;
			
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
				// 判断是否在风控白名单里面
				List<String> whiteIdsList = new ArrayList<String>();
				final int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
				AfUserDo afUserDo = afUserService.getUserById(userId);
				// 判断是否在风控白名单里面
				AfResourceDo whiteListInfo = afResourceService.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
				if (whiteListInfo != null) {
					whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","),
						new Converter<String, String>() {
							@Override
							public String convert(String source) {
								return source.trim();
							}
						});
				}
				Date currDate = new Date();
				if((verifyBo.isSuccess()&&StringUtils.equals("10", verifyBo.getResult()))||whiteIdsList.contains(afUserDo.getUserName())) {
					//风控审核通过,根据开关判断是否推送钱包打款
					Boolean flag=true;
					//新增白名单逻辑
					AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
					if (pushWhiteResource != null) {
						//白名单开启
						String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
						Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
						if(!Arrays.asList(whiteUserIds).contains(userId)){
							//不在白名单不推送
							flag=false;
						}
					}
					AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
					AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
					//浙商是否维护中逻辑
					Boolean bankIsMaintaining = bankIsMaintaining(assetPushResource);
					if (StringUtil.equals(assetPushType.getBorrowCash(), YesNoStatus.YES.getCode())
						&&(StringUtil.equals(afBorrowCashDo.getMajiabaoName(), "www")||StringUtil.equals(afBorrowCashDo.getMajiabaoName(), ""))
						&&StringUtil.equals(YesNoStatus.NO.getCode(), assetPushResource.getValue3())&&flag&&!bankIsMaintaining) {
						//开关开启，非马甲包的现金贷推送
						AfBorrowCashDto afBorrowCashDto= applyLegalBorrowCashService.getBorrowCashInfoById(afBorrowCashDo.getRid());
						List<EdspayGetCreditRespBo> borrowCashInfos= new ArrayList<EdspayGetCreditRespBo>();
						EdspayGetCreditRespBo borrowCashInfo =new EdspayGetCreditRespBo();
						borrowCashInfo.setDebtType(0);
						borrowCashInfo.setOrderNo(afBorrowCashDto.getOrderNo());
						//获取借款利率配置
						AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
						String minBorrowTime = afResourceDo.getTypeDesc().split(",")[0];
						String maxBorrowTime = afResourceDo.getTypeDesc().split(",")[1];
						Integer timeLimit = NumberUtil.objToIntDefault(afBorrowCashDto.getType(), null);
						JSONObject jsonObject=new JSONObject();
						if (afResourceDo!=null &&  afResourceDo.getValue2() != null) {
							JSONArray array= JSONObject.parseArray(afResourceDo.getValue2());
							for (int i = 0; i < array.size(); i++) {
								if (StringUtils.equals((String)array.getJSONObject(i).get("borrowTag"), AfResourceSecType.INTEREST_RATE.getCode())) {
									jsonObject = array.getJSONObject(i);
									break;
								}
							}
						}
						BigDecimal borrowRate=BigDecimal.ZERO;
						if (StringUtils.equals(afBorrowCashDto.getType(), minBorrowTime)) {
							borrowRate=new BigDecimal((String)jsonObject.get("borrowFirstType"));
						}else{
							borrowRate=new BigDecimal((String)jsonObject.get("borrowSecondType")) ;
						}
						borrowCashInfo.setUserId(afBorrowCashDto.getUserId());
						borrowCashInfo.setName(afBorrowCashDto.getName());
						borrowCashInfo.setCardId(afBorrowCashDto.getCardId());
						borrowCashInfo.setMobile(afBorrowCashDto.getMobile());
						borrowCashInfo.setBankNo(afBorrowCashDto.getBankNo());
						borrowCashInfo.setAcctName("");
						borrowCashInfo.setMoney(afBorrowCashDto.getMoney());
						borrowCashInfo.setApr(borrowRate);
						borrowCashInfo.setTimeLimit(timeLimit);
						borrowCashInfo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(afBorrowCashDto.getLoanStartTime()));
						if (StringUtil.isNotBlank(afBorrowCashDto.getBorrowRemark())) {
							borrowCashInfo.setPurpose(afBorrowCashDto.getBorrowRemark());
						}else {
							borrowCashInfo.setPurpose("个人消费");
						}
						borrowCashInfo.setRepaymentStatus(0);
						borrowCashInfo.setRepayName(afBorrowCashDto.getName());
						borrowCashInfo.setRepayAcct(afBorrowCashDto.getBankNo());
						borrowCashInfo.setRepayAcctBankNo("");
						borrowCashInfo.setRepayAcctType(0);
						if (StringUtil.equals(afBorrowCashDto.getCardName(),"浙商银行")) {
							borrowCashInfo.setIsRepayAcctOtherBank(0);
						}else{
							borrowCashInfo.setIsRepayAcctOtherBank(1);
						}
						//获取资产方的分润利率
						AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
						borrowCashInfo.setManageFee(afAssetSideInfoDo.getAnnualRate());
						if (StringUtil.isNotBlank(afBorrowCashDto.getRefundRemark())) {
							borrowCashInfo.setRepaymentSource(afBorrowCashDto.getRefundRemark());
						}else {
							borrowCashInfo.setRepaymentSource("工资收入");
						}
						borrowCashInfo.setIsPeriod(0);
						borrowCashInfo.setTotalPeriod(1);
						borrowCashInfo.setLoanerType(0);
						//借款人平台逾期信息
						AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(afBorrowCashDto.getUserId());
						borrowCashInfo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
						borrowCashInfo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
						//还款计划
						List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
						RepaymentPlan repaymentPlan = new RepaymentPlan();
						repaymentPlan.setRepaymentNo(afBorrowCashDto.getOrderNo());
						repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(DateUtil.addDays(afBorrowCashDto.getLoanStartTime(), timeLimit-1)));
						repaymentPlan.setRepaymentDays(timeLimit.longValue());
						repaymentPlan.setRepaymentAmount(afBorrowCashDto.getMoney());
						repaymentPlan.setRepaymentInterest(BigDecimalUtil.multiply(afBorrowCashDto.getMoney(), new BigDecimal(borrowRate.doubleValue()*timeLimit / 36000d)));
						repaymentPlan.setRepaymentPeriod(0);
						repaymentPlans.add(repaymentPlan);
						borrowCashInfo.setRepaymentPlans(repaymentPlans);
						borrowCashInfo.setIsCur(0);
						borrowCashInfos.add(borrowCashInfo);

						//债权实时推送
						boolean result = assetSideEdspayUtil.borrowCashCurPush(borrowCashInfos, afAssetSideInfoDo.getAssetSideFlag(),Constants.ASSET_SIDE_FANBEI_FLAG);
						if (result) {
							logger.info("borrowCashCurPush suceess,orderNo="+borrowCashInfo.getOrderNo());
							//老ups逻辑
							final AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
							delegateBorrowCashDo.setRid(afBorrowCashDo.getRid());
							jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
							String bankNumber = mainCard.getCardNumber();
							String lastBank = bankNumber.substring(bankNumber.length() - 4);
							smsUtil.sendBorrowCashCode(afUserDo.getUserName(), lastBank);
							String title = "恭喜您，审核通过啦！";
							String msgContent = "您的借款审核通过，请留意您尾号&bankCardNo的银行卡资金变动，请注意按时还款，保持良好的信用记录。";
							msgContent = msgContent.replace("&bankCardNo", lastBank);
							jpushService.pushUtil(title, msgContent, afUserDo.getUserName());
							// 审核通过
							delegateBorrowCashDo.setGmtArrival(currDate);
							delegateBorrowCashDo.setStatus(AfBorrowCashStatus.transeding.getCode());
							afBorrowLegalOrderDo.setStatus(BorrowLegalOrderStatus.UNPAID.getCode());
							delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.AGREE.getCode());
							Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
							Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(delegateBorrowCashDo.getGmtArrival());
							Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
							delegateBorrowCashDo.setGmtPlanRepayment(repaymentDay);
							
							transactionTemplate.execute(new TransactionCallback<String>() {
								@Override
								public String doInTransaction(TransactionStatus status) {
									// 更新借款状态
									afBorrowCashService.updateBorrowCash(delegateBorrowCashDo);
									// 更新订单状态
									afBorrowLegalOrderService.updateById(afBorrowLegalOrderDo);
									applyLegalBorrowCashServiceImpl.addTodayTotalAmount(currentDay, afBorrowCashDo.getAmount());
									return "success";
								}
							});
							//记录push表
							AfBorrowCashPushDo borrowCashPush = buildBorrowCashPush(afBorrowCashDo.getRid(),borrowCashInfo.getApr(), borrowCashInfo.getManageFee());
							afBorrowCashPushService.saveOrUpdate(borrowCashPush);
						}
					}else{
						// 不需推送或者马甲包的债权，提交ups进行打款处理
						applyLegalBorrowCashService.delegatePay(verifyBo.getConsumerNo(), verifyBo.getOrderNo(),
								verifyBo.getResult(), afBorrowLegalOrderDo, mainCard, afBorrowCashDo);
					}
					// 增加借款埋点信息
					doMaidianLog(request, afBorrowCashDo, requestDataVo, context);
					//百度智能地址
					try {
						//smartAddressEngine.setScoreAsyn(afBorrowLegalOrderDo.getAddress(),borrowId,afBorrowLegalOrderDo.getOrderNo());
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
					AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype("extend_koudai");
					try{
						if(afResourceDo!=null&&afResourceDo.getValue().equals("Y")&&afResourceDo.getValue4().contains(appType)){
							jpushService.dealBorrowCashApplyFailForKoudai(afUserDo.getUserName(), currDate,afResourceDo.getValue1());
                            if (afResourceDo.getValue3().contains(afUserDo.getUserName().substring(0, 3))) {
                                smsUtil.sendMarketingSmsToDhstForEC(afUserDo.getUserName(), afResourceDo.getValue2());
                            }
						}else{
							jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
						}
					}catch (Exception e){
						logger.error("push legal borrow cash error", e);
					}
					Integer countUnpayOverDue= afBorrowBillService.countNotPayOverdueBill(afUserDo.getRid());
					if(countUnpayOverDue>0){
						throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR_BORROW);
					}
					throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
				}
				return resp;
			} catch (Exception e) {
				logger.error("apply legal borrow cash error", e);
				AfBorrowCashDo delegateBorrowCashDo = new AfBorrowCashDo();
				delegateBorrowCashDo.setRid(borrowId);
				// 关闭借款
				delegateBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				// 关闭搭售商品订单
				afBorrowLegalOrderDo.setStatus(AfBorrowLegalOrderCashStatus.CLOSED.getCode());
				// 更新风控审核状态为拒绝
				delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				// 如果属于非爱上街自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
				if (e instanceof FanbeiException) {
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.REFUSE.getCode());
				} else {
					logger.error("apply legal borrow cash exist unexpected exception,cause:{}" , e.getCause());
					delegateBorrowCashDo.setReviewStatus(RiskReviewStatus.APPLY.getCode());
					delegateBorrowCashDo.setReviewDetails("弱风控认证存在捕获外异常");
				}
				applyLegalBorrowCashService.updateBorrowStatus(delegateBorrowCashDo,afBorrowLegalOrderDo);
				Integer countUnpayOverDue= afBorrowBillService.countNotPayOverdueBill(userId);
				if(countUnpayOverDue>0){
					throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR_BORROW);
				}
				throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
			}
		} finally {
			bizCacheUtil.delCache(lockKey);
		}
	}

	private AfBorrowCashPushDo buildBorrowCashPush(Long rid, BigDecimal apr,BigDecimal manageFee) {
		AfBorrowCashPushDo borrowCashPush =new AfBorrowCashPushDo();
		Date now = new Date();
		borrowCashPush.setGmtCreate(now);
		borrowCashPush.setGmtModified(now);
		borrowCashPush.setBorrowCashId(rid);
		borrowCashPush.setBorrowRate(apr);
		borrowCashPush.setProfitRate(manageFee);
		return borrowCashPush;
	}

	private Boolean bankIsMaintaining(AfResourceDo assetPushResource) {
		Boolean bankIsMaintaining=false;
		if (null != assetPushResource && StringUtil.isNotBlank(assetPushResource.getValue4())) {
			String[] split = assetPushResource.getValue4().split(",");
			String maintainStart = split[0];
			String maintainEnd = split[1];
			Date maintainStartDate =DateUtil.parseDate(maintainStart,DateUtil.DATE_TIME_SHORT);
			Date gmtCreateEndDate =DateUtil.parseDate(maintainEnd,DateUtil.DATE_TIME_SHORT);
			 bankIsMaintaining = DateUtil.isBetweenDateRange(new Date(),maintainStartDate,gmtCreateEndDate);
			
		}
		return bankIsMaintaining;
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

	/**
	 * 获取资产方开户行信息
	 * @param assetSideFlag
	 * @return
	 */
	public List<FanbeiBorrowBankInfoBo> getAssetSideBankInfo() {
		List<FanbeiBorrowBankInfoBo> bankInfoList = new ArrayList<FanbeiBorrowBankInfoBo>();
		try {
			List<AfResourceDo> bankInfoLists = afResourceService.getConfigsByTypesAndSecType(AfResourceType.ASSET_SIDE_CONFIG.getCode(), AfResourceSecType.ASSET_SIDE_CONFIG_BANK_INFOS.getCode());
			if(bankInfoLists==null){
				return bankInfoList;
			}

			for (AfResourceDo afResourceDo : bankInfoLists) {
				bankInfoList.add(JSON.toJavaObject(JSON.parseObject(afResourceDo.getValue()), FanbeiBorrowBankInfoBo.class));
			}
		} catch (Exception e) {
			logger.error("getAssetSideBankInfo error,e=",e);
		}
		return bankInfoList;
	}

	/**
	 * 获取随机开户行对象
	 * @return
	 */
	public FanbeiBorrowBankInfoBo getAssetSideBankInfo(List<FanbeiBorrowBankInfoBo> bankInfoList) {
		if(bankInfoList == null || bankInfoList.size() == 0){
			return null;
		}
		Collections.shuffle(bankInfoList);
		return bankInfoList.get(0);
	}

}
