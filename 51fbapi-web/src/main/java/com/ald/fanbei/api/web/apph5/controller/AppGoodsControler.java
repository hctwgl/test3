/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.H5ItemModelType;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月24日上午10:34:44
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/goods/")
public class AppGoodsControler extends BaseController {

	@Resource
	AfModelH5ItemService afModelH5ItemService;

	@RequestMapping(value = { "goodsList" }, method = RequestMethod.GET)
	public void goodsList(HttpServletRequest request, ModelMap model) throws IOException {
		Long modelId = NumberUtil.objToLongDefault(request.getParameter("modelId"), 1);

		List<Object> bannerList = getH5ItemBannerObjectWith(afModelH5ItemService
				.getModelH5ItemListByModelIdAndModelType(modelId, H5ItemModelType.BANNELl.getCode()));
		List<AfModelH5ItemDo> categoryDbList = afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(modelId,
				H5ItemModelType.CATEGORY.getCode());
		List<AfTypeCountDto> sortCountList = afModelH5ItemService
				.getModelH5ItemGoodsCountListCountByModelIdAndSort(modelId);
		List<Object> categoryList = getH5ItemCategoryListObjectWithAfModelH5ItemDoListAndSortCount(categoryDbList,
				sortCountList);
		model.put("bannerList", bannerList);
		model.put("categoryList", categoryList);
		Integer pageCount = 20;// 每一页显示20条数据
		Integer sort = 0;
		if (categoryDbList.size() > 0) {
			AfModelH5ItemDo afModelH5ItemDo = categoryDbList.get(0);
			sort =afModelH5ItemDo.getSort();			
		}
		List<AfModelH5ItemDo> list = afModelH5ItemService.getModelH5ItemGoodsListCountByModelIdAndSort(modelId,
				sort, 0, pageCount);
		List<Object> goodsList =getH5ItemGoodsListObjectWithAfModelH5ItemDoList(list);
		model.put("goodsList", goodsList);
		logger.info(JSON.toJSONString(model));
	}
	
	   /**
     * 获取主题下面的商品
     * @param model
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
	@RequestMapping(value = "categoryGoodsList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String categoryGoodsList(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			Long modelId = NumberUtil.objToLongDefault(request.getParameter("modelId"), 1);
			Integer pageCurrent = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
			Integer sort = NumberUtil.objToIntDefault(request.getParameter("type"), 0);

			Integer pageCount = 20;// 每一页显示20条数据
			List<AfModelH5ItemDo> list = afModelH5ItemService.getModelH5ItemGoodsListCountByModelIdAndSort(modelId,
					sort, (pageCurrent-1)*pageCount, pageCount*pageCurrent);
			List<Object> goodsList =getH5ItemGoodsListObjectWithAfModelH5ItemDoList(list);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("goodsList", goodsList);

    		return H5CommonResponse.getNewInstance(true, "查询成功", "", data).toString();

		} catch (Exception e) {
    		return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}
		

    }
	
	private List<Object> getH5ItemGoodsListObjectWithAfModelH5ItemDoList(List<AfModelH5ItemDo> goodslist) {
		List<Object> list = new ArrayList<Object>();
		for (AfModelH5ItemDo afModelH5ItemDo : goodslist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageIcon", afModelH5ItemDo.getItemIcon());
			data.put("type", afModelH5ItemDo.getItemType());
			data.put("content", afModelH5ItemDo.getItemValue());
			data.put("secSort", afModelH5ItemDo.getSecSort());
			list.add(data);

		}
		
		return list;
	}

	private Map<String, Object> modelH5ItemCategorySortCountWith(List<AfTypeCountDto> sortCountList) {
		Map<String, Object> data = new HashMap<String, Object>();
		for (AfTypeCountDto afTypeCountDto : sortCountList) {
			String sort = ObjectUtils.toString(afTypeCountDto.getSort(), "0");
			data.put(sort, afTypeCountDto.getCount());
			
		}
		return data;
	}

	private List<Object> getH5ItemCategoryListObjectWithAfModelH5ItemDoListAndSortCount(
		List<AfModelH5ItemDo> categoryList, List<AfTypeCountDto> sortCountList) {

		Map<String, Object> data = modelH5ItemCategorySortCountWith(sortCountList);

		List<Object> list = new ArrayList<Object>();
		int pageCount = 20; 
		for (AfModelH5ItemDo afModelH5ItemDo : categoryList) {
			Map<String, Object> itemData = new HashMap<String, Object>();
			itemData.put("imageIcon", afModelH5ItemDo.getItemIcon());
			itemData.put("imageIcon2", afModelH5ItemDo.getItemType());
			itemData.put("name", afModelH5ItemDo.getItemValue());
			itemData.put("sort", afModelH5ItemDo.getSort());
			itemData.put("type", afModelH5ItemDo.getSort());

			String sort = ObjectUtils.toString(afModelH5ItemDo.getSort(), "0");

			int count =NumberUtil.objToIntDefault(data.get(sort), 0) ;
			Integer pageTotal = count/pageCount;
			Integer pageM = count%pageCount;
			itemData.put("pageTotal",pageM==0?pageTotal:(pageTotal+1) );
			list.add(itemData);

		}

		return list;
	}

	private List<Object> getH5ItemBannerObjectWith(List<AfModelH5ItemDo> bannerList) {
		List<Object> list = new ArrayList<Object>();
		for (AfModelH5ItemDo afModelH5ItemDo : bannerList) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageIcon", afModelH5ItemDo.getItemIcon());
			data.put("type", afModelH5ItemDo.getItemType());
			data.put("content", afModelH5ItemDo.getItemValue());
			data.put("sort", afModelH5ItemDo.getSecSort());
			list.add(data);
		}

		return list;
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
