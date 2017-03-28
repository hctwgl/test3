/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfShopVo;

/**
 * 
 * @类描述：获取菠萝觅优惠券列表
 * @author xiaotianjian 2017年3月28日下午3:40:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandCouponListApi")
public class GetBrandCouponListApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		Integer type = NumberUtil.objToIntDefault(requestDataVo.getParams().get("type"), null);
		Integer pageSize = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageSize"), null);
		
		resp.addResponseData("shopList", null);
		resp.addResponseData("pageNo", pageNo);
		return resp;
	}
	
	private AfShopVo parseDoToVo(AfShopDo shopInfo) {
		AfShopVo vo = new AfShopVo();
		vo.setRid(shopInfo.getRid());
		vo.setName(shopInfo.getName());
		vo.setRebateAmount(shopInfo.getCommissionAmount());
		vo.setRebateUnit(shopInfo.getCommissionUnit());
		vo.setIcon(shopInfo.getIcon());
		return vo;
	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("userId", "68885");
//		map.put("type", "1");
//		map.put("pageIndex", "1");
//		map.put("pageSize", "10");
//		String str = HttpUtil.httpPost("http://91ala.test.otosaas.com/promotion/get_coupon_list", null);
//		System.out.println(str);
	}

}
