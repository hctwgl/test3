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
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午9:34:40
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("changeUserAddressApi")
public class ChangeUserAddressApi implements ApiHandle {

	@Resource
	AfUserAddressService afUserAddressService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long addressId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("addressId"), 0);

		String province = ObjectUtils.toString(requestDataVo.getParams().get("province"));
		String city = ObjectUtils.toString(requestDataVo.getParams().get("city"));
		String county = ObjectUtils.toString(requestDataVo.getParams().get("county"));
		String address = ObjectUtils.toString(requestDataVo.getParams().get("address"));
		String isDefault = ObjectUtils.toString(requestDataVo.getParams().get("isDefault"));
		String consignee = ObjectUtils.toString(requestDataVo.getParams().get("consignee"));
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		if (StringUtils.isBlank(province) && StringUtils.isBlank(city) && StringUtils.isBlank(county)
				&& StringUtils.isBlank(address) && StringUtils.isBlank(isDefault) && StringUtils.isBlank(consignee)
				&& StringUtils.isBlank(mobile)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		if(!StringUtils.equals(isDefault, YesNoStatus.YES.getCode())&&!StringUtils.equals(isDefault, YesNoStatus.NO.getCode())){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

		}
		
		Long userId = context.getUserId();
		// 取消上一个默认地址
		if(StringUtils.equals(isDefault, YesNoStatus.YES.getCode())){
			AfUserAddressDo defauleDo = afUserAddressService.selectUserAddressDefaultByUserId(userId);
			if(defauleDo!=null && addressId!=defauleDo.getRid()){
				defauleDo.setIsDefault(YesNoStatus.NO.getCode());
				afUserAddressService.updateUserAddress(defauleDo);
			}
		}else{
			// 不能从一个默认到不默认地址改变
			
			AfUserAddressDo userAddressDo = afUserAddressService.selectUserAddressByrid(addressId);
			if(userAddressDo != null){
				if(!StringUtils.equals(userAddressDo.getIsDefault(), isDefault)){
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CHANG_DEFAULT_ADDRESS_ERROR);
				}
			}
			
			if(afUserAddressService.getCountOfAddressByUserId(userId) <= 1){
				isDefault = YesNoStatus.YES.getCode();
			}
		}
		
		
		AfUserAddressDo addressDo = new AfUserAddressDo();
		addressDo.setRid(addressId);
		addressDo.setUserId(userId);

		if(StringUtils.isNotBlank(address)){
			addressDo.setAddress(address);

		}
		if(StringUtils.isNotBlank(city)){
			addressDo.setCity(city);

		}
		if(StringUtils.isNotBlank(province)){
			addressDo.setProvince(province);

		}
		if(StringUtils.isNotBlank(county)){
			addressDo.setCounty(county);

		}
		if(StringUtils.isNotBlank(consignee)){
			addressDo.setConsignee(consignee);

		}
		if(StringUtils.isNotBlank(isDefault)){
			addressDo.setIsDefault(isDefault);

		}
		if(StringUtils.isNotBlank(mobile)){
			addressDo.setMobile(mobile);

		}
		if(afUserAddressService.updateUserAddress(addressDo)>0){
			return resp;

		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

	}

}
