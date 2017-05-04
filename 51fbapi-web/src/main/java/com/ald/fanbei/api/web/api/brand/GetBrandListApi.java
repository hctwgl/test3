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
//		//TODO DELETE
//		AfShopVo lvmama = new AfShopVo();
//		lvmama.setRid(4l);
//		lvmama.setName("驴妈妈");
//		lvmama.setRebateAmount(new BigDecimal("4.00"));
//		lvmama.setRebateUnit("PERCENTAGE");
//		lvmama.setType("JIPIAO");
//		lvmama.setIcon("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/online/1210844b0574300d.jpg");
//		
//		
//		AfShopVo xishiqu = new AfShopVo();
//		xishiqu.setRid(5l);
//		xishiqu.setName("西十区");
//		xishiqu.setRebateAmount(new BigDecimal("4.00"));
//		xishiqu.setRebateUnit("PERCENTAGE");
//		xishiqu.setType("PIAOWU");
//		xishiqu.setIcon("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/online/31bed7771a561817.jpg");
//		
//		AfShopVo jiayouka = new AfShopVo();
//		jiayouka.setRid(6l);
//		jiayouka.setName("加油卡");
//		jiayouka.setRebateAmount(new BigDecimal("4.00"));
//		jiayouka.setRebateUnit("PERCENTAGE");
//		jiayouka.setType("JIAYOUKA");
//		jiayouka.setIcon("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/online/13bb56e96390e73b.jpg");
//		
//		AfShopVo huafei = new AfShopVo();
//		huafei.setRid(7l);
//		huafei.setName("话费");
//		huafei.setRebateAmount(new BigDecimal("4.00"));
//		huafei.setRebateUnit("PERCENTAGE");
//		huafei.setType("JIAYOUKA");
//		huafei.setIcon("http://51fanbei.oss-cn-hangzhou.aliyuncs.com/online/13bb56e96390e73b.jpg");
//		
//		resultList.add(lvmama);
//		resultList.add(xishiqu);
//		resultList.add(jiayouka);
//		resultList.add(huafei);
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
		vo.setType(shopInfo.getType());
		vo.setShopUrl(shopInfo.getShopUrl());
		return vo;
	}

}
