/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.RiskErrorCode;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.BoluomeConfirmOrderVo;

/**
 * 
 * @类描述：获取菠萝觅确认订单信息
 * @author xiaotianjian 2017年3月25日下午9:33:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmOrderApi")
public class GetConfirmOrderApi implements ApiHandle {
	
	private static final String SUCCESS_CODE = "0000";

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService; 
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfBorrowCashService afBorrowCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> params = requestDataVo.getParams();
		//第三方订单编号
		String orderId = ObjectUtils.toString(params.get("orderId"), null);
		String plantform = ObjectUtils.toString(params.get("plantform"), null);
		
		if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(plantform)) {
			logger.error("orderId or plantform is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(plantform, orderId);
		if (orderInfo ==  null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		Long userId = orderInfo.getUserId();
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserBankcardDo bankInfo = afUserBankcardService.getUserMainBankcardByUserId(userId);
		BoluomeConfirmOrderVo vo = buildConfirmOrderVo(orderInfo,userDto, authDo, bankInfo,context);
		//小于368版本，可用额度设置为0
		if (context.getAppVersion() <= 368 && isVirtualGoods(orderInfo)) {
			vo.setUseableAmount(BigDecimal.ZERO);
		} else {
			dealWithVirtualCodeGt368(vo, orderInfo, userDto);
		}
		resp.setResponseData(vo);
		return resp;
	}
	
	/**
	 * 处理大于368的版本
	 * @param vo
	 * @param orderInfo
	 */
	private void dealWithVirtualCodeGt368(BoluomeConfirmOrderVo vo, AfOrderDo orderInfo, AfUserAccountDto userDto) {
		Map<String, Object> virtualMap = afOrderService.getVirtualCodeAndAmount(orderInfo);
		//风控逾期订单处理
		RiskQueryOverdueOrderRespBo resp = riskUtil.queryOverdueOrder(orderInfo.getUserId() + StringUtil.EMPTY);
		String rejectCode = resp.getRejectCode();
		if (StringUtil.isNotBlank(rejectCode)) {
			RiskErrorCode erorrCode = RiskErrorCode.findRoleTypeByCode(rejectCode);
			switch (erorrCode) {
			case OVERDUE_BORROW:
				String borrowNo = resp.getBorrowNo();
				AfBorrowDo borrowInfo = afBorrowService.getBorrowInfoByBorrowNo(borrowNo);
				Long billId = afBorrowBillService.getOverduedAndNotRepayBillId(borrowInfo.getRid());
				vo.setBillId(billId);
				vo.setOverduedCode(erorrCode.getCode());
				break;
			case OVERDUE_BORROW_CASH:
				AfBorrowCashDo cashInfo = afBorrowCashService.getNowTransedBorrowCashByUserId(userDto.getUserId());
				vo.setOverduedCode(erorrCode.getCode());
				vo.setJfbAmount(userDto.getJfbAmount());
				vo.setUserRebateAmount(userDto.getRebateAmount());
				BigDecimal allAmount = BigDecimalUtil.add(cashInfo.getAmount(), cashInfo.getSumOverdue(),
						cashInfo.getOverdueAmount(), cashInfo.getRateAmount(), cashInfo.getSumRate());
				BigDecimal repaymentAmount = BigDecimalUtil.subtract(allAmount, cashInfo.getRepayAmount());
				vo.setRepaymentAmount(repaymentAmount);
				vo.setBorrowId(cashInfo.getRid());
				break;
			default:
				break;
			}
		} else {
			vo.setOverduedCode(SUCCESS_CODE);
		}
		
		if (afOrderService.isVirtualGoods(virtualMap)) {
			String virtualCode = afOrderService.getVirtualCode(virtualMap);
			BigDecimal totalVirtualAmount = afOrderService.getVirtualAmount(virtualMap);
			BigDecimal leftAmount = afUserVirtualAccountService.getCurrentMonthLeftAmount(orderInfo.getUserId(), virtualCode, totalVirtualAmount);
			vo.setVirtualGoodsUsableAmount(leftAmount);
			vo.setIsVirtualGoods(YesNoStatus.YES.getCode());
		} else {
			vo.setIsVirtualGoods(YesNoStatus.NO.getCode());
			vo.setVirtualGoodsUsableAmount(BigDecimal.ZERO);
		}
	}
	
	private BoluomeConfirmOrderVo buildConfirmOrderVo(AfOrderDo orderInfo, AfUserAccountDto userDto, AfUserAuthDo authDo, AfUserBankcardDo bankInfo, FanbeiContext context){
		BoluomeConfirmOrderVo vo = new BoluomeConfirmOrderVo();
		vo.setRid(orderInfo.getRid());
		vo.setGoodsName(orderInfo.getGoodsName());
		vo.setSaleAmount(orderInfo.getSaleAmount());
		vo.setRebateAmount(orderInfo.getRebateAmount());
		vo.setGmtPayEnd(orderInfo.getGmtPayEnd());
		vo.setCurrentTime(new Date());
		vo.setBankcardStatus(authDo.getBankcardStatus());
    	vo.setRealName(userDto.getRealName());
        if(!StringUtil.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())){
        	vo.setBankId(bankInfo.getRid());
        	vo.setBankName(bankInfo.getBankName());
        	vo.setBankCode(bankInfo.getBankCode());
        	vo.setBankIcon(bankInfo.getBankIcon());
        	vo.setIsValid(bankInfo.getIsValid());
        }
		vo.setTotalAmount(userDto.getAuAmount());
		vo.setUseableAmount(userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount()));
		vo.setRealNameScore(authDo.getRealnameScore());
		vo.setAllowConsume(afUserAuthService.getConsumeStatus(orderInfo.getUserId(),context.getAppVersion()));
		vo.setFaceStatus(authDo.getFacesStatus());
		vo.setIdNumber(Base64.encodeString(userDto.getIdNumber()));
		
		return vo;
	}
	
	/**
	 * 51判断菠萝觅订单是否为虚拟商品
	 * @param afOrderInfo
	 * @return
	 */
	private boolean isVirtualGoods(AfOrderDo afOrderInfo) {
		if (afOrderInfo == null) {
			return false;
		}
		String orderType = afOrderInfo.getOrderType();
		//如果不是菠萝觅订单,暂时放过
		if (!OrderType.BOLUOME.getCode().equals(orderType)) {
			return false;
		}
		
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(AfResourceType.VIRTUAL_GOODS_SERVICE_PROVIDER.getCode());
		String serviceProvider = resourceInfo.getValue();
		if (StringUtil.isNotBlank(serviceProvider)) {
			List<String> serviceProviderList = CollectionConverterUtil.convertToListFromArray(serviceProvider.split(","), new Converter<String, String>() {
				@Override
				public String convert(String source) {
					return source;
				}
			});
			return serviceProviderList.contains(afOrderInfo.getServiceProvider());
		}
		return false;
	}

}
