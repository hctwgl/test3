package com.ald.fanbei.api.web.api.goods;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
/**
 * @date 2017-9-7 17:35:31
 * @author qiaopan
 * @description 搜索淘宝店铺
 *
 */
@Component("getThirdShopsListApi")
public class GetThirdShopsListApi implements ApiHandle {
	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
