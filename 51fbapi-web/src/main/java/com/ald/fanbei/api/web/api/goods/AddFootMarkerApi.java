package com.ald.fanbei.api.web.api.goods;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserFootmarkService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：AddFootMarkerApi
 *@author 何鑫 2017年1月19日  15:23:37
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("addFootMarkerApi")
public class AddFootMarkerApi implements ApiHandle{

	@Resource
	private AfUserFootmarkService afUserFootmarkService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

        Long userId = context.getUserId();
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
		
        logger.info("userId=" + userId + ",goodsId=" + goodsId);
        // TODO 商品信息查询，功能待定,商品不存在时返回商品不存在接口
        if(afUserFootmarkService.dealUserFootmark(userId, goodsId)>0){
        	return resp;
        }
        throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
