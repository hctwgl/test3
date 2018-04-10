package com.ald.fanbei.api.web.apph5.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBrandService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBrandDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsVo1;
/**
 * @类描述 爱尚街分类 品牌
 * @author liutengyuan 
 * @date 2018年4月10日
 */
@Controller
@RequestMapping("/category")
public class AppH5BrandController extends BaseController {
	
	@Resource
	private AfBrandService afBrandService;
	@Resource
	private AfGoodsService afGoodsService;
	
	@RequestMapping(value="/brandResult",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String brandResult(HttpServletRequest request ,HttpServletResponse response){
		Long brandId = NumberUtil.objToLong(request.getParameter("brandId"));
		int pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
		if (brandId == null){
			return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getErrorMsg(), "", null).toString();
		}
		// 1 query info of the brand
		AfBrandDo brandDo = afBrandService.getById(brandId);
		// 2 query goods of the brand with volume of top5
		List<AfGoodsDo> starGoodsList = afGoodsService.getGoodsListByBrandIdAndVolume(brandId);
		List<AfGoodsDo> goodsList = afGoodsService.getGoodsListByBrandId(brandId);
		// handle the goods data 
		Map<String,List<AfGoodsVo1>> goodsInfo = handleRelateData(goodsList);
		return "";
	}
	
	
	
	



	private Map<String, List<AfGoodsVo1>> handleRelateData(
			List<AfGoodsDo> goodsList) {
		Map<String, List<AfGoodsVo1>> map = new  ConcurrentHashMap<String, List<AfGoodsVo1>>();
		
		return null;
	}







	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
