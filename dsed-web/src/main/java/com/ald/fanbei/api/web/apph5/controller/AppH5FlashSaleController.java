package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfSeckillActivityService;
import com.ald.fanbei.api.biz.service.AfUserGoodsSmsService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserGoodsSmsDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
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
 * @author :chefeipeng
 * @version ：2017年6月13日 下午5:02:28
 * @注意：本内容仅限于杭州喜马拉雅家居有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/activity")
public class AppH5FlashSaleController extends BaseController {
    String opennative = "/fanbei-web/opennative?name=";
    @Resource
    AfUserService afUserService;
    @Resource
    AfCouponService afCouponService;
    @Resource
    AfGoodsPriceService afGoodsPriceService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfActivityGoodsService afActivityGoodsService;

    @Resource
    AfResourceService afResourceService;
    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfSchemeGoodsService afSchemeGoodsService;
    @Resource
    AfInterestFreeRulesService afInterestFreeRulesService;
    @Resource
    AfUserGoodsSmsService afUserGoodsSmsService;

    @Autowired
    AfSeckillActivityService afSeckillActivityService;

    @RequestMapping(value = "/getFlashSaleGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String GetFlashSaleGoods(HttpServletRequest request, HttpServletResponse response) {
		try {
			FanbeiWebContext context = doWebCheck(request, false);

			Integer activityDay = 0;
			if (request.getParameter("activityDay") != null) {
				activityDay = Integer.parseInt(request.getParameter("activityDay").toString());
			}
			Integer pageNo = 1;
			if (request.getParameter("pageNo") != null) {
				pageNo = Integer.parseInt(request.getParameter("pageNo").toString());
			}

			H5CommonResponse resp = null;
			Map<String, Object> data = new HashMap<String, Object>();
			List<Object> topBannerList = new ArrayList<Object>();
			String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			int count = 0;
			// 正式环境和预发布环境区分，banner轮播图展示
			if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
				// 新版,旧版,banner图不一样
				String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
				topBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
			} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
				// 新版,旧版,banner图不一样
				String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
				topBannerList = getObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
			}
			data.put("BannerList", topBannerList);

			// 商品展示
			Long userId = null;
			if (context.getUserName() != null) {
				AfUserDo userDo = afUserService.getUserByUserName(context.getUserName());
				if (userDo != null)
					userId = userDo.getRid();
			}
			AfResourceDo afResourceHomeSecKillDo = afResourceService.getSingleResourceBytype("HOME_SECKILL_CONFIG");
			List<HomePageSecKillGoods> list = afSeckillActivityService.getHomePageSecKillGoods(userId, afResourceHomeSecKillDo.getValue(), activityDay, pageNo);

			if (list.size() > 0) {
				data.put("startTime", list.get(0).getActivityStart().getTime());
				data.put("endTime", list.get(0).getActivityEnd().getTime());
			}
			data.put("currentTime", new Date().getTime());

			List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
			// 获取借款分期配置信息
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
			JSONArray array = JSON.parseArray(resource.getValue());
			if (array == null) {
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
			}

			for (HomePageSecKillGoods homePageSecKillGoods : list) {
				Map<String, Object> goodsInfo = new HashMap<String, Object>();
				goodsInfo.put("goodsName", homePageSecKillGoods.getGoodName());
				goodsInfo.put("rebateAmount", homePageSecKillGoods.getRebateAmount());
				goodsInfo.put("saleAmount", homePageSecKillGoods.getSaleAmount());
				goodsInfo.put("priceAmount", homePageSecKillGoods.getPriceAmount());
				goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());
				goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());
				goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());
				goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());
				goodsInfo.put("goodsType", "0");
				goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());
				goodsInfo.put("volume", homePageSecKillGoods.getVolume());
				goodsInfo.put("total", homePageSecKillGoods.getTotal());
				goodsInfo.put("source", homePageSecKillGoods.getSource());
				goodsInfo.put("activityId", homePageSecKillGoods.getActivityId());
				// 如果是分期免息商品，则计算分期
				Long goodsId = homePageSecKillGoods.getGoodsId();
				JSONArray interestFreeArray = null;
				if (homePageSecKillGoods.getInterestFreeId() != null) {
					AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(homePageSecKillGoods.getInterestFreeId().longValue());
					String interestFreeJson = interestFreeRulesDo.getRuleJson();
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
				}

				List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
						homePageSecKillGoods.getActivityAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");
				if (nperList != null) {
					goodsInfo.put("goodsType", "1");
					Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
					String isFree = (String) nperMap.get("isFree");
					if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
						nperMap.put("freeAmount", nperMap.get("amount"));
					}
					goodsInfo.put("nperMap", nperMap);
				}

				goodsList.add(goodsInfo);
			}
			data.put("goodsList", goodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", data);
			return resp.toString();
		} catch (Exception e) {
			logger.error("/fanbei-web/activity/getFlashSaleGoods error:", e);
			return H5CommonResponse.getNewInstance(false, "获取数据失败", null, "").toString();
		}
	}

    private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
	List<Object> bannerList = new ArrayList<Object>();
	for (AfResourceDo afResourceDo : bannerResclist) {
	    Map<String, Object> data = new HashMap<String, Object>();
	    data.put("imageUrl", afResourceDo.getValue());
	    data.put("titleName", afResourceDo.getName());
	    data.put("type", afResourceDo.getValue1());
	    data.put("content", afResourceDo.getValue2());
	    data.put("sort", afResourceDo.getSort());
	    bannerList.add(data);
	}

	return bannerList;
    }

    private AfGoodsQuery getCheckParam(HttpServletRequest request) {
	Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(request.getParameter("pageNo")), 1);
	AfGoodsQuery query = new AfGoodsQuery();
	query.setPageNo(pageNo);
	return query;
    }

    @RequestMapping(value = "/getBookingRushGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String GetBookingRushGoods(HttpServletRequest request, HttpServletResponse response) {

	H5CommonResponse resp = H5CommonResponse.getNewInstance();
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiWebContext context = new FanbeiWebContext();
	try {
	    context = doWebCheck(request, false);
	    String userName = context.getUserName();
	    Long userId = convertUserNameToUserId(userName);
	    // 商品展示
	    AfGoodsQuery query = getCheckParam(request);
	    List<AfEncoreGoodsDto> list = afGoodsService.selectBookingRushGoods(query);
	    List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
	    // 获取借款分期配置信息
	    AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	    JSONArray array = JSON.parseArray(resource.getValue());
	    if (array == null) {
		throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	    }
	    // Iterator<Object> it = array.iterator();
	    // while (it.hasNext()) {
	    // JSONObject json = (JSONObject) it.next();
	    // if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
	    // it.remove();
	    // break;
	    // }
	    // }
	    for (AfEncoreGoodsDto goodsDo : list) {
		Map<String, Object> goodsInfo = new HashMap<String, Object>();
		goodsInfo.put("goodName", goodsDo.getName());
		goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
		goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
		goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
		goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
		goodsInfo.put("goodsId", goodsDo.getRid());
		goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
		goodsInfo.put("goodsType", "0");
		// 是否预约
		if (null == userId) {
		    goodsInfo.put("reserve", "N");
		} else {
		    AfUserGoodsSmsDo afUserGoodsSmsDo = new AfUserGoodsSmsDo();

		    afUserGoodsSmsDo.setGoodsId(goodsDo.getRid());
		    afUserGoodsSmsDo.setUserId(userId);
		    AfUserGoodsSmsDo afUserGoodsSms = afUserGoodsSmsService.selectByGoodsIdAndUserId(afUserGoodsSmsDo);
		    if (null != afUserGoodsSms) {
			goodsInfo.put("reserve", "Y");
		    } else {
			goodsInfo.put("reserve", "N");
		    }
		}
		// 如果是分期免息商品，则计算分期
		Long goodsId = goodsDo.getRid();
		AfSchemeGoodsDo schemeGoodsDo = null;
		try {
		    schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
		} catch (Exception e) {
		    logger.error(e.toString());
		}
		JSONArray interestFreeArray = null;
		if (schemeGoodsDo != null) {
		    AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
		    String interestFreeJson = interestFreeRulesDo.getRuleJson();
		    if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
			interestFreeArray = JSON.parseArray(interestFreeJson);
		    }
		}
		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId, "0");
		if (nperList != null) {
		    goodsInfo.put("goodsType", "1");
		    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
		    String isFree = (String) nperMap.get("isFree");
		    if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
			nperMap.put("freeAmount", nperMap.get("amount"));
		    }
		    goodsInfo.put("nperMap", nperMap);
		}

		goodsList.add(goodsInfo);
	    }
	    data.put("goodsList", goodsList);
	    return H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
	} catch (Exception e) {
	    logger.error(e.getMessage(), e);
	    return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
	}

    }

    @RequestMapping(value = "/checkGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String CheckGoods(HttpServletRequest request, HttpServletResponse response) {

    	if(StringUtils.isBlank(request.getParameter("goodsId"))|| StringUtils.isBlank(request.getParameter("activityId")))
    		throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);

		Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), 0l);
		Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 0l);

		Integer sumCount = afSeckillActivityService.getSecKillGoodsStock(goodsId, activityId);
		if (null == sumCount || sumCount <= 0) {
			return H5CommonResponse.getNewInstance(false, "您来晚了，商品已抢光！", "", goodsId).toString();
		}
		return H5CommonResponse.getNewInstance(true, "成功", "", goodsId).toString();
    }

    @RequestMapping(value = "/reserveGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String ReserveGoods(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			if(StringUtils.isBlank(request.getParameter("goodsId"))|| StringUtils.isBlank(request.getParameter("activityId")))
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);

			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long userId = convertUserNameToUserId(userName);
			Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), 0l);
			Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 0l);
			String goodsName = "商品名称";
			AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
			if (null != afGoodsDo) {
			goodsName = afGoodsDo.getName();
			}
			AfUserGoodsSmsDo afUserGoodsSmsDo = new AfUserGoodsSmsDo();
			afUserGoodsSmsDo.setGoodsId(goodsId);
			afUserGoodsSmsDo.setUserId(userId);
			afUserGoodsSmsDo.setIsDelete(0l);
			afUserGoodsSmsDo.setGoodsName(goodsName);
			afUserGoodsSmsDo.setActivityId(activityId);
			AfUserGoodsSmsDo afUserGoodsSms = afUserGoodsSmsService.selectBookingByGoodsIdAndUserId(afUserGoodsSmsDo);
			if (null != afUserGoodsSms) {
				return H5CommonResponse.getNewInstance(false, "商品已预约").toString();
			}
			try {
				int flag = afUserGoodsSmsService.insertByGoodsIdAndUserId(afUserGoodsSmsDo);
				if (flag <= 0) {
					return H5CommonResponse.getNewInstance(false, "预约失败").toString();
				}
				} catch (Exception e) {
				return H5CommonResponse.getNewInstance(false, "预约失败" + e.toString()).toString();
				}
			return H5CommonResponse.getNewInstance(true, "设置提醒成功，商品开抢后将通过短信通知您", "", goodsId).toString();
		} catch (FanbeiException e) {
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
			return H5CommonResponse.getNewInstance(false, "登陆之后才能进行查看", notifyUrl, null).toString();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return H5CommonResponse.getNewInstance(false, "预约失败" + e.toString()).toString();
		}

    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
	// TODO Auto-generated method stub
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
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
    }

    private Long convertUserNameToUserId(String userName) {
	Long userId = null;
	if (!StringUtil.isBlank(userName)) {
	    AfUserDo user = afUserService.getUserByUserName(userName);
	    if (null != user) {
		userId = user.getRid();
	    }

	}
	return userId;
    }

}
