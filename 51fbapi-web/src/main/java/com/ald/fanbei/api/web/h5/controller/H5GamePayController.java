package com.ald.fanbei.api.web.h5.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;
import com.ald.fanbei.api.web.common.H5CommonResponse;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: 双十一砍价 H5
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:28:04
 *
 */
@RestController
@RequestMapping(value = "/game/pay", produces = "application/json;charset=UTF-8")
public class H5GamePayController extends H5Controller {

    @Autowired
    private AfSupGameService afSupGameService;

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public H5CommonResponse getGoodsList(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiH5Context context = doH5Check(request, false);
	try {
	    String type = request.getParameter("type");

	    if (StringUtils.isNotBlank(type)) {
		List<GameGoods> hotList = afSupGameService.getHotGoodsList(type);
		List<GameGoodsGroup> groupList = afSupGameService.getGoodsList(type);

		data.put("hotList", hotList);
		data.put("groupList", groupList);

		return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
	    } else {
		return H5CommonResponse.getNewInstance(false, "参数错误");
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/goods" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取游戏列表失败");
	}
    }

}
