/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取搜呗主页信息
 * @author xiaotianjian 2017年2月9日上午9:47:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("GetSearchBayHomeInfo")
public class GetSearchBayHomeInfoApi implements ApiHandle {

	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
//		List<XItem> list = taobaoApiUtil.searchTaoBaokeGoods(params);
//		resp.setResponseData(list);
		return resp;
	}


}
