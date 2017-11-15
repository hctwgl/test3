package com.ald.fanbei.api.web.api.borrow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
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
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetMyBorrowListV1Api 
* @Description: 用户获取账单列表的api——账单二期
* @author yuyue
* @date 2017年11月15日 上午10:47:56 
*
 */
@Component("getMyBorrowListV1Api")
public class GetMyBorrowListV1Api implements ApiHandle{

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
			String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
			if (userId == null) {
				logger.info("getMyBorrowListV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.info("getMyBorrowListV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfBorrowBillQuery query = new AfBorrowBillQuery();
			if (StringUtil.equals("outBill", status)) {
				query.setIsOut(1);
				query.setUserId(userId);
				query.setStatus("N");
			}else if (StringUtil.equals("overdueBill", status)){
				query.setIsOut(1);
				query.setUserId(userId);
				query.setStatus("N");
				query.setOverdueStatus("Y");
			}else if (StringUtil.equals("notOutBill", status)) {
				query.setIsOut(0);
				query.setUserId(userId);
				query.setStatus("N");
			}
			BigDecimal money = afBorrowBillService.getUserBillMoneyByQuery(query);
			List<AfBorrowBillDo> billList = afBorrowBillService.getUserBillListByQuery(query);
			if (billList != null && billList.size() > 0) {
				for (AfBorrowBillDo afBorrowBillDo : billList) {
					if (afBorrowBillDo.getOverdueDays() > 0) {
						afBorrowBillDo.setOverdueStatus("Y");
					}
				}
			}
			map.put("money", money);
			map.put("billList", billList);
			resp.setResponseData(map);
		} catch (Exception e) {
			logger.error("getMyBorrowListV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
		return resp;
	}

}
