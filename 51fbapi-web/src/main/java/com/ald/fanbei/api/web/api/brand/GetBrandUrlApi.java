/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 
 * @类描述：获取品牌Url
 * @author xiaotianjian 2017年3月23日上午11:58:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandUrlApi")
public class GetBrandUrlApi implements ApiHandle {

    @Resource
    AfShopService afShopService;
    @Resource
    AfOrderDao afOrderDao;
    @Resource
    BoluomeUtil boluomeUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	Map<String, Object> params = requestDataVo.getParams();

	Long shopId = NumberUtil.objToLongDefault(params.get("shopId"), null);

	if (shopId == null) {
	    logger.error("shopId is empty");
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	}

	AfShopDo shopInfo = afShopService.getShopById(shopId);
	if (shopInfo == null) {
	    logger.error("shopId is invalid");
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
	}

	/* fma_临时关闭掉话费充值功能 */
	if (StringUtil.equals(shopInfo.getType(), "HUAFEI")) {
	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FUNCTION_REPAIRING_ERROR);
	}

	String shopUrl = afShopService.parseBoluomeUrl(shopInfo.getShopUrl(), shopInfo.getPlatformName(), shopInfo.getType(), context.getUserId(), context.getMobile());

	resp.addResponseData("shopUrl", shopUrl);
	return resp;
    }

}
