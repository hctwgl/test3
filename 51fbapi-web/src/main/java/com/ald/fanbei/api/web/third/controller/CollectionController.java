package com.ald.fanbei.api.web.third.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.ibatis.annotations.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionUpdateResqBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类现描述：和催收平台互调
 * @author chengkang 2017年8月5日 下午16:59:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/collection")
public class CollectionController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	CollectionSystemUtil collectionSystemUtil;
	
	@Resource
	AfBorrowCashService borrowCashService;
	/**
	 * 用户通过催收平台还款，经财务审核通过后，系统自动调用此接口向51返呗推送,返呗记录线下还款信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/offlineRepayment" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionOperatorNotifyRespBo offlineRepayment (HttpServletRequest request, HttpServletResponse response) {
		String data = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("deal offlineRepayment begin,sign=" + sign + ",data=" + data + ",timestamp=" + timestamp);
		CollectionOperatorNotifyRespBo notifyRespBo = collectionSystemUtil.offlineRepaymentNotify(timestamp, data, sign);
		return notifyRespBo;
	}
	@RequestMapping(value = { "/findBorrowCashByBorrowId"})
	@ResponseBody
	public CollectionUpdateResqBo findBorrowCashByBorrowId(HttpServletRequest request, HttpServletResponse response){
		String borrowNo = ObjectUtils.toString(request.getParameter("borrowNo"));
		Map<String,String> map=new HashMap();
		CollectionUpdateResqBo updteBo=new CollectionUpdateResqBo();
		try{
		AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo("jq2017033015092200001");
		if(afBorrowCashDo==null) {
			logger.error("afBorrowCashDo is null" );
			updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
		}
		map.put("borrowNo",afBorrowCashDo.getBorrowNo());
		map.put("amount",afBorrowCashDo.getAmount()+"");
		map.put("overdueAmount",afBorrowCashDo.getOverdueAmount()+"");
		map.put("overdueDay",afBorrowCashDo.getOverdueDay()+"");
		map.put("renewalNum",afBorrowCashDo.getRenewalNum()+"");
		map.put("sumRenewalPoundage",afBorrowCashDo.getSumRenewalPoundage()+"");
		map.put("repayAmount",afBorrowCashDo.getRepayAmount()+"");
		map.put("status",afBorrowCashDo.getStatus());
		
		String jsonString = JsonUtil.toJSONString(map);
		updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
		updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
		updteBo.setSign(DigestUtil.MD5(jsonString));
		updteBo.setData(jsonString);
		} catch(Exception e){
			logger.error("error message " + e);
		}
		return updteBo;
	}
}
