/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ActivityType;
import com.ald.fanbei.api.common.enums.H5ItemModelType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserH5ItmeGoodsDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月24日上午10:34:44
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/goods/")
public class AppGoodsControler extends BaseController {
	String opennative = "/fanbei-web/opennative?name=";
	@Resource
	AfModelH5ItemService afModelH5ItemService;
	@Resource
	AfModelH5Service afModelH5Service;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;

	@RequestMapping(value = { "goodsListModel" }, method = RequestMethod.GET)
	public void goodsListModel(HttpServletRequest request, ModelMap model) throws IOException {
		Long modelId = NumberUtil.objToLongDefault(request.getParameter("modelId"), 1);

		FanbeiWebContext webContext = doWebCheckNoAjax(request, false);
		String userName = webContext.getUserName();
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		Integer appVersion = webContext.getAppVersion();
		// 394 版本才显示优惠券信息
		if(appVersion != null && appVersion >= 394) {
			// 根据modelId 取优惠券信息
			List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(modelId,
					ActivityType.H5_TEMPLATE.getCode());
			for(AfCouponDto couponDto : couponList) {
				// 判断用户是否领
				if(userDo == null) {
					couponDto.setUserAlready(0);
				} else {
					int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
					couponDto.setUserAlready(pickCount);
				}
			}
			model.put("couponList", couponList);
		}
		List<Object> bannerList = getH5ItemBannerObjectWith(afModelH5ItemService
				.getModelH5ItemListByModelIdAndModelType(modelId, H5ItemModelType.BANNER.getCode()));
		List<AfModelH5ItemDo> categoryDbList = afModelH5ItemService
				.getModelH5ItemCategoryListByModelIdAndModelType(modelId);
		List<AfTypeCountDto> sortCountList = afModelH5ItemService
				.getModelH5ItemGoodsCountListCountByModelIdAndSort(modelId);
		List<Object> categoryList = getH5ItemCategoryListObjectWithAfModelH5ItemDoListAndSortCount(categoryDbList,
				sortCountList);
		AfModelH5Do h5Do = afModelH5Service.selectMordelH5ById(modelId);
		model.put("modelName", h5Do.getName() == null ? "" : h5Do.getName());
		model.put("bannerList", bannerList);
		model.put("categoryList", categoryList);

		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
				+ H5OpenNativeType.GoodsInfo.getCode();
		model.put("notifyUrl", notifyUrl);

		Integer pageCount = 20;// 每一页显示20条数据
		String type = "0";

		if (categoryDbList.size() > 0) {
			AfModelH5ItemDo afModelH5ItemDo = categoryDbList.get(0);
			type = ObjectUtils.toString(afModelH5ItemDo.getRid(), "").toString();
		}
		List<AfUserH5ItmeGoodsDto> goodsDoList = afModelH5ItemService
				.getModelH5ItemGoodsListCountByModelIdAndCategory(modelId, type, 0, pageCount);
		//判断商品是否处于活动中
		List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
		List<Long> goodsIdList = new ArrayList<>();
		for (AfUserH5ItmeGoodsDto goodsDo : goodsDoList) {
			goodsIdList.add(Long.valueOf(goodsDo.getGoodsId()));
		}
		if(goodsIdList!=null&&goodsIdList.size()>0){
			activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
		}
		// 判断商品是否在优惠方案中
		List goodsList = new ArrayList();
		for (AfUserH5ItmeGoodsDto goodsDto : goodsDoList) {
			//改变活动价格
			for (AfSeckillActivityGoodsDo activityGoodsDo : activityGoodsDos) {
				if(activityGoodsDo.getGoodsId().toString().equals(goodsDto.getGoodsId())){
					goodsDto.setSaleAmount(activityGoodsDo.getSpecialPrice());
					BigDecimal secKillRebAmount = goodsDto.getSaleAmount().multiply(goodsDto.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
					if(goodsDto.getRebateAmount().compareTo(secKillRebAmount)>0){
						goodsDto.setRebateAmount(secKillRebAmount);
					}
					break;
				}
			}
			Map goodsInfoMap = CollectionConverterUtil.convertObjToMap(goodsDto);
			goodsInfoMap.put("goodsType", "0");
			String goodsId = goodsDto.getGoodsId();
			if (goodsId != null && !"".equals(goodsId)) {
				AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(Long.parseLong(goodsId));
				JSONArray interestFreeArray = null;
				if (null != afSchemeGoodsDo) {
					Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
					AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService.getById(interestFreeId);
					if (null != afInterestFreeRulesDo && StringUtils.isNotBlank(afInterestFreeRulesDo.getRuleJson())) {
						interestFreeArray = JSON.parseArray(afInterestFreeRulesDo.getRuleJson());
					}
				}
				// 获取借款分期配置信息
				AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
						Constants.RES_BORROW_CONSUME);

				JSONArray array = JSON.parseArray(resource.getValue());
				// 删除2分期
				if (array == null) {
					goodsList.add(goodsInfoMap);
					continue;
				}
				//removeSecondNper(array);
				List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
						BigDecimal.ONE.intValue(), goodsDto.getSaleAmount(), resource.getValue1(),
						resource.getValue2(),Long.parseLong(goodsId),"0");
				if (nperList != null) {
					goodsInfoMap.put("goodsType", "1");
					Map nperMap = nperList.get(nperList.size() - 1);
					goodsInfoMap.put("nperMap", nperMap);
				}

			}
			goodsList.add(goodsInfoMap);
		}

		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "", "", model));
		logger.info("list++++++====" + JSON.toJSONString(goodsList));

		model.put("goodsList", goodsList);
		model.put("typeCurrent", type);
		logger.info(JSON.toJSONString(model));
	}

	private void removeSecondNper(JSONArray array) {
		if (array == null) {
			return;
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
				it.remove();
				break;
			}
		}

	}

	/**
	 * 获取主题下面的商品
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "categoryGoodsList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String categoryGoodsList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long modelId = NumberUtil.objToLongDefault(request.getParameter("modelId"), 1);
			Integer pageCurrent = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
			String type = ObjectUtils.toString(request.getParameter("type"), "").toString();

			Integer pageCount = 20;// 每一页显示20条数据
			List<AfUserH5ItmeGoodsDto> goodsDoList = afModelH5ItemService
					.getModelH5ItemGoodsListCountByModelIdAndCategory(modelId, type, (pageCurrent - 1) * pageCount,
							pageCount);

			// 判断商品是否在优惠方案中
			List goodsList = new ArrayList();
			for (AfUserH5ItmeGoodsDto goodsDto : goodsDoList) {
				Map goodsInfoMap = CollectionConverterUtil.convertObjToMap(goodsDto);
				goodsInfoMap.put("goodsType", "0");
				String goodsId = goodsDto.getGoodsId();

				if (goodsId != null && !"".equals(goodsId)) {
					JSONArray interestFreeArray = null;
					AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService
							.getSchemeGoodsByGoodsId(Long.parseLong(goodsId));
					if (null != afSchemeGoodsDo) {
						Long interestFreeId = afSchemeGoodsDo.getInterestFreeId();
						AfInterestFreeRulesDo afInterestFreeRulesDo = afInterestFreeRulesService
								.getById(interestFreeId);
						if (null != afInterestFreeRulesDo
								&& StringUtils.isNotBlank(afInterestFreeRulesDo.getRuleJson())) {
							interestFreeArray = JSON.parseArray(afInterestFreeRulesDo.getRuleJson());
						}
					}
					// 获取借款分期配置信息
					AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
							Constants.RES_BORROW_CONSUME);
					JSONArray array = JSON.parseArray(resource.getValue());
					// 删除2分期
					if (array == null) {
						goodsList.add(goodsInfoMap);
						continue;
					}
					//removeSecondNper(array);

					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
							BigDecimal.ONE.intValue(), goodsDto.getSaleAmount(), resource.getValue1(),
							resource.getValue2(),Long.parseLong(goodsId),"0");
					if (nperList != null) {
						goodsInfoMap.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						goodsInfoMap.put("nperMap", nperMap);
					}

				}
				goodsList.add(goodsInfoMap);
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("goodsList", goodsList);

			return H5CommonResponse.getNewInstance(true, "查询成功", "", data).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}

	private List<Object> getH5ItemCategoryListObjectWithAfModelH5ItemDoListAndSortCount(
			List<AfModelH5ItemDo> categoryList, List<AfTypeCountDto> sortCountList) {

		List<Object> list = new ArrayList<Object>();
		int pageCount = 20;
		for (AfModelH5ItemDo afModelH5ItemDo : categoryList) {
			Map<String, Object> itemData = new HashMap<String, Object>();
			itemData.put("imageIcon", afModelH5ItemDo.getItemIcon());
			itemData.put("imageIcon2", afModelH5ItemDo.getItemValue2());
			itemData.put("name", afModelH5ItemDo.getItemValue());
			itemData.put("sort", afModelH5ItemDo.getSort());
			itemData.put("type", afModelH5ItemDo.getRid());

			String type = ObjectUtils.toString(afModelH5ItemDo.getItemType(), "0");

			int count = NumberUtil.objToIntDefault(type, 0);
			Integer pageTotal = count / pageCount;
			Integer pageM = count % pageCount;
			itemData.put("pageTotal", pageM == 0 ? pageTotal : (pageTotal + 1));
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
			data.put("sort", afModelH5ItemDo.getSort());
			list.add(data);
		}

		return list;
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
	}

}
