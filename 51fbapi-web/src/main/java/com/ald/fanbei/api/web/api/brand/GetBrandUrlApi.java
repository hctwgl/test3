/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取品牌Url
 * @author xiaotianjian 2017年3月23日上午11:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandUrlApi")
public class GetBrandUrlApi implements ApiHandle {

	@Resource
	AfShopService afShopService;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	BoluomeUtil boluomeUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
//		AfOrderDo orderInfo = afOrderDao.getOrderById(90179l);
//		BoluomePushPayResponseBo res =  boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getActualAmount());
		
		 Map<String, Object> params = requestDataVo.getParams();
		Map<String, String> buildParams = new HashMap<String, String>();

		Long shopId = NumberUtil.objToLongDefault(params.get("shopId"), null);
		
		if (shopId == null) {
			logger.error("shopId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfShopDo shopInfo = afShopService.getShopById(shopId);
		if (shopInfo ==  null) {
			logger.error("shopId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		String shopUrl = shopInfo.getShopUrl() + "/" + shopInfo.getType().toLowerCase() + "?";
		
		buildParams.put(BoluomeCore.CUSTOMER_USER_ID, context.getUserId()+StringUtils.EMPTY);
		buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, context.getMobile());
		buildParams.put(BoluomeCore.TIME_STAMP, requestDataVo.getSystem().get(Constants.REQ_SYS_NODE_TIME) + StringUtils.EMPTY);
		
		String sign =  BoluomeCore.buildSignStr(buildParams);
		buildParams.put(BoluomeCore.SIGN, sign);
		String paramsStr = BoluomeCore.createLinkString(buildParams);
		
		resp.addResponseData("shopUrl", shopUrl + paramsStr);
		return resp;
	}

}
