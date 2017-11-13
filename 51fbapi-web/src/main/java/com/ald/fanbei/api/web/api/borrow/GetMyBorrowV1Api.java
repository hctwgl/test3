package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetMyBorrowV1Api 
* @Description: 用户获取账单主页面的api——账单二期
* @author yuyue
* @date 2017年11月13日 上午10:51:12 
*
 */
@Component("getMyBorrowV1Api")
public class GetMyBorrowV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			if (userId == null) {
				logger.info("getMyBorrowV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.info("getMyBorrowV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			
			AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(userId);
			if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.YES.getCode())) {
				// 获取用户额度
				AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
				if(userAccount == null || userAccount.getRid() == null) {
					logger.error("getMyBorrowV1Api error ; userAccount is null and userId = " + userId);
					resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
					return resp;
				}
				BigDecimal auAmount = userAccount.getAuAmount();
				BigDecimal amount = BigDecimalUtil.subtract(auAmount, userAccount.getUsedAmount());
				// 获取逾期账单月数量
				int overduedMonth = afBorrowBillService.getOverduedMonthByUserId(userId);
				BigDecimal outMoney = afBorrowBillService.getUserOutMoney(userId);
				BigDecimal notOutMoeny = afBorrowBillService.getUserNotOutMoney(userId);
				Date lastPayDay = null;
				if (overduedMonth < 1) {
					// 当用户没有逾期的时候获取最后还款日
					lastPayDay = afBorrowBillService.getLastPayDayByUserId(userId);
				}
				map.put("auAmount", auAmount);
				map.put("amount", amount);
				map.put("overduedMonth", overduedMonth);
				map.put("outMoney", outMoney);
				map.put("notOutMoeny", notOutMoeny);
				map.put("lastPayDay", DateUtil.formatMonthAndDay(lastPayDay));
			}
		} catch (Exception e) {
			logger.error("getMyBorrowV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
		
		return resp;
	}

}
