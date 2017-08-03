/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午9:35:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteUserAddressApi")
public class DeleteUserAddressApi implements ApiHandle {
	@Resource
	AfUserAddressService afUserAddressService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),
				FanbeiExceptionCode.SUCCESS);
		Long addressId = NumberUtil.objToLongDefault(requestDataVo.getParams()
				.get("addressId"), 0);
		String isDefault = ObjectUtils.toString(
				requestDataVo.getParams().get("isDefault"), null);
		if(addressId == 0 || StringUtils.isBlank(isDefault)){
			return new ApiHandleResponse(requestDataVo.getId(),
					FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		Long userId = context.getUserId();
		if (afUserAddressService.getCountOfAddressByUserId(userId) > 1){ // 地址数量是一个的话 就不删除
			if (afUserAddressService.deleteUserAddress(addressId) > 0) {
				if (StringUtils.equals(isDefault, YesNoStatus.YES.getCode())) {
					if ((afUserAddressService.selectUserAddressByUserId(userId) != null)) {
						if (afUserAddressService.reselectTheDefaultAddress(userId) > 0) {
							return resp;
						}
					}
				} else {
					return resp;
				}
			}
		}else{
			return new ApiHandleResponse(requestDataVo.getId(),
					FanbeiExceptionCode.CHANG_ADDRESS_ERROR);
		}
		return new ApiHandleResponse(requestDataVo.getId(),
				FanbeiExceptionCode.FAILED);
	}

}
