package com.ald.fanbei.api.web.third.controller;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionUpdateResqBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类现描述：和资产方平台对接入口
 * @author chengkang 2017年11月27日 下午16:45:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/edspay")
public class EdspayController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	CollectionSystemUtil collectionSystemUtil;
	
	@Resource
	AfBorrowCashService borrowCashService;
	
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Resource
	AfIdNumberService idNumberService;
	
	/**
	 * 资产方债权订单回传接口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/giveBackCreditInfo" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionOperatorNotifyRespBo giveBackCreditInfo(HttpServletRequest request, HttpServletResponse response) {
		String data = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("deal offlineRepayment begin,sign=" + sign + ",data=" + data + ",timestamp=" + timestamp);
		CollectionOperatorNotifyRespBo notifyRespBo = collectionSystemUtil.offlineRepaymentNotify(timestamp, data, sign);
		return notifyRespBo;
	}
	
	/**
	 * 资产方债权订单获取接口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/getBatchCreditInfo"}, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo getBatchCreditInfo(HttpServletRequest request, HttpServletResponse response){
		String borrowNo = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign1 = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("findBorrowCashByBorrowNo data="+borrowNo+",timestamp="+timestamp+",sign1="+sign1+"");
		
		Map<String,String> map=new HashMap<String,String>();
		CollectionUpdateResqBo updteBo=new CollectionUpdateResqBo();
		try{
			AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
			if(afBorrowCashDo==null) {
				logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null" );
				updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
				return updteBo;
			}
			String sign2=DigestUtil.MD5(afBorrowCashDo.getBorrowNo());
			if (StringUtil.equals(sign1, sign2)) {// 验签成功
				map.put("consumer_no", afBorrowCashDo.getUserId()+"");
				map.put("borrow_id",afBorrowCashDo.getRid()+"");
				map.put("borrow_no",afBorrowCashDo.getBorrowNo());
				map.put("card_name", afBorrowCashDo.getCardName());
				map.put("card_number", afBorrowCashDo.getCardNumber());
				map.put("gmt_arrival",  DateUtil.formatDateTime(afBorrowCashDo.getGmtArrival()));
				map.put("type", afBorrowCashDo.getType());
				map.put("amount",afBorrowCashDo.getAmount().multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("rate_amount", afBorrowCashDo.getRateAmount().multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("overdue_amount",afBorrowCashDo.getOverdueAmount().multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("repay_amount", ((afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount().add(afBorrowCashDo.getOverdueAmount().add(afBorrowCashDo.getSumRate().add(afBorrowCashDo.getSumOverdue()))))).setScale(2, RoundingMode.HALF_UP)).multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("rest_amount", ((afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount().add(afBorrowCashDo.getOverdueAmount().add(afBorrowCashDo.getSumRate().add(afBorrowCashDo.getSumOverdue()))))).subtract(afBorrowCashDo.getRepayAmount()).setScale(2, RoundingMode.HALF_UP)).multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("overdue_day",afBorrowCashDo.getOverdueDay()+"");
				map.put("renewal_num",afBorrowCashDo.getRenewalNum()+"");
				map.put("repay_amount_sum",afBorrowCashDo.getRepayAmount().multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				map.put("status",afBorrowCashDo.getStatus());
				map.put("gmt_plan_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtPlanRepayment()));
				map.put("majiabao_name", StringUtil.null2Str(afBorrowCashDo.getMajiabaoName()));
				if (StringUtil.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
					map.put("gmt_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtModified()) + "");
				}else{
					map.put("gmt_repayment", "");
				}
				String jsonString = JsonUtil.toJSONString(map);
				updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
				updteBo.setData(jsonString);
				return updteBo;
			}else{
				updteBo.setCode(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getMsg());
				return updteBo;
			}
		} catch(Exception e){
			logger.error("error message " + e);
			updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
			return updteBo;
		}
	}
	
}
