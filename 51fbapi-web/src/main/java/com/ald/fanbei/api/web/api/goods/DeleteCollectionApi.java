/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：删除收藏
 * @author suweili 2017年2月25日下午2:04:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteCollectionApi")
public class DeleteCollectionApi implements ApiHandle {

	@Resource
	AfuserCollectionService afuserCollectionService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		
		Long collectionId = NumberUtil.objToLongDefault(params.get("collectionId"), 1);
		
		AfUserCollectionDo afUserCollectionDo = new AfUserCollectionDo();
		afUserCollectionDo.setRid(collectionId);
		afUserCollectionDo.setUserId(userId);
		if(afuserCollectionService.deleteUserCollectionGoods(afUserCollectionDo)>0){
			return resp;
		}
        throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
