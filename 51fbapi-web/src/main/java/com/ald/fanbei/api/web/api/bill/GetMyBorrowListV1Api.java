package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.lang.ObjectUtils;
import org.jsoup.helper.DataUtil;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.mysql.fabric.xmlrpc.base.Data;
import com.timevale.esign.sdk.tech.service.impl.l;

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
	private AfUserService afUserService;
	
	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfUserOutDayDao afUserOutDayDao;
	
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
			AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
			query.setUserId(userId);
			query.setStatus(BorrowBillStatus.NO.getCode());
			if (StringUtil.isEmpty(status)) {
				status = "outBill";
			}
			if (StringUtil.equals("nowBill", status)) {
				// 本月已出
				query.setIsOut(1);
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.DATE, 1);
		        calendar.set(Calendar.HOUR_OF_DAY, 0);
		        calendar.set(Calendar.MINUTE, 0);
		        calendar.set(Calendar.SECOND, 0);
		        calendar.set(Calendar.MILLISECOND, 0);
		        Date strDate = calendar.getTime();
		        calendar.add(Calendar.MONTH, 1);
		        calendar.add(Calendar.SECOND, -1);
		        Date endDate = calendar.getTime();
		        query.setOutDayStr(strDate);
		        query.setOutDayEnd(endDate);
			}else if (StringUtil.equals("outBill", status)) {
				// 所有已出
				query.setIsOut(1);
			}else if (StringUtil.equals("overdueBill", status)){
				// 逾期账单
				query.setIsOut(1);
				query.setOverdueStatus("Y");
			}else if (StringUtil.equals("notOutBill", status)) {
				// 未出账单
				query.setIsOut(0);
			}
			BigDecimal money = afBorrowBillService.getUserBillMoneyByQuery(query);
			List<AfBorrowBillDo> billList = afBorrowBillService.getUserBillListByQuery(query);

			Map<Integer, List<AfBorrowBillDo>> yearMap = new HashMap<Integer, List<AfBorrowBillDo>>();
			if (billList != null && billList.size() > 0) {
				for (AfBorrowBillDo afBorrowBillDo : billList) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(afBorrowBillDo.getGmtOutDay());
					afBorrowBillDo.setBillMonth(calendar.get(Calendar.MONTH) + 1);
					afBorrowBillDo.setBillYear(calendar.get(Calendar.YEAR));
					if (StringUtil.equals("notOutBill", status)) {
						afBorrowBillDo.setIsOut(0);
					}else {
						afBorrowBillDo.setIsOut(1);
						if (new BigDecimal(0).compareTo(afBorrowBillDo.getOverdueInterestAmount()) != 0) {
							afBorrowBillDo.setOverdueStatus("Y");
						}else {
							afBorrowBillDo.setOverdueStatus("N");
							afBorrowBillDo.setGmtPayTime(afBorrowBillService.getLastPayDayByUserId(userId));
						}
					}
					if (yearMap.containsKey(afBorrowBillDo.getBillYear())) {
						List<AfBorrowBillDo> monthList = yearMap.get(afBorrowBillDo.getBillYear());
						monthList.add(afBorrowBillDo);
						yearMap.put(afBorrowBillDo.getBillYear(), monthList);
					}else {
						List<AfBorrowBillDo> monthList = new ArrayList<AfBorrowBillDo>();
						monthList.add(afBorrowBillDo);
						yearMap.put(afBorrowBillDo.getBillYear(), monthList);
					}
				}
			}

			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(Integer key : yearMap.keySet()) {
				Map<String, Object> respMaP = new HashMap<String, Object>();
				respMaP.put("year", key);
				respMaP.put("bills", yearMap.get(key));
				list.add(respMaP);
			}
			
			Collections.sort(list,new Comparator<Map<String, Object>>(){
				public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
					if((Integer)arg0.get("year") > (Integer)arg1.get("year")) return 1;
					if((Integer)arg0.get("year") < (Integer)arg1.get("year")) return -1;
					return 0;
				}
			});
			
			map.put("money", money);
			map.put("billList", list);
			resp.setResponseData(map);
		} catch (Exception e) {
			logger.error("getMyBorrowListV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
		return resp;
	}

}
