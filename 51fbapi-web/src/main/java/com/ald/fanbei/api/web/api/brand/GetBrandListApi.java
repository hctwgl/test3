/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfShopVo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月26日下午11:17:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandListApi")
public class GetBrandListApi implements ApiHandle {

	@Resource
	AfShopService afShopService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		AfShopQuery query = new AfShopQuery();
		query.setFull(false);
		query.setPageNo(pageNo);
		List<AfShopDo> shopList = afShopService.getShopList(query);
		List<AfShopVo> resultList = new ArrayList<AfShopVo>();
		if (CollectionUtil.isNotEmpty(shopList)) {
			resultList = CollectionConverterUtil.convertToListFromList(shopList, new Converter<AfShopDo, AfShopVo>() {
				@Override
				public AfShopVo convert(AfShopDo source) {
					return parseDoToVo(source);
				}
			});
		}
		resp.addResponseData("shopList", resultList);
		resp.addResponseData("pageNo", pageNo);
		return resp;
	}
	
	private AfShopVo parseDoToVo(AfShopDo shopInfo) {
		AfShopVo vo = new AfShopVo();
		vo.setRid(shopInfo.getRid());
		vo.setName(shopInfo.getName());
		vo.setRebateAmount(shopInfo.getRebateAmount());
		vo.setRebateUnit(shopInfo.getRebateUnit());
		vo.setIcon(shopInfo.getIcon());
		return vo;
	}

}
