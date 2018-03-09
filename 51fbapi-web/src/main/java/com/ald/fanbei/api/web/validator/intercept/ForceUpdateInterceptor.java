package com.ald.fanbei.api.web.validator.intercept;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 接口强升拦截器
 * 
 * @author rongbo
 *
 */
@Component("forceUpdateInterceptor")
public class ForceUpdateInterceptor implements Interceptor {

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public void intercept(RequestDataVo reqData, FanbeiContext context, HttpServletRequest request) {
		//406强升需要数据拦截的借钱相关接口
		String apiUrl = "/legalborrow/applyLegalBorrowCash,/legalborrowV2/applyLegalBorrowCash," +
				"/legalborrowV2/confirmLegalRenewalPay,/legalborrow/confirmLegalRenewalPay," +
				"/borrowCash/applyBorrowCashV1,/borrowCash/confirmRenewalPay,/legalborrow/getLegalBorrowCashHomeInfo," +
				"/legalborrowV2/getLegalBorrowCashHomeInfo," +
				"/borrowCash/getBowCashLogInInfo," +
				"/borrowCash/getBorrowCashHomeInfo,";
		if(apiUrl.toLowerCase().contains(request.getRequestURI().toString().toLowerCase()) && context.getAppVersion()<406){
			String afResourceDo = afResourceService.getAfResourceAppVesionV1();
			if (afResourceDo != null && afResourceDo.equals("true") && reqData.getId().endsWith("www")) {
				throw new FanbeiException("version is letter 406", FanbeiExceptionCode.VERSION_ERROR);
			}
		}
	}

	@Override
	public void intercept(Context context) {
		
	}

}
