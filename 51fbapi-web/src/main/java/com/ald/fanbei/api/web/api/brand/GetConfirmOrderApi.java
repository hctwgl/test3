/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.BoluomeConfirmOrderVo;

/**
 * 
 * @类描述：获取菠萝觅确认订单信息
 * @author xiaotianjian 2017年3月25日下午9:33:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfirmOrderApi")
public class GetConfirmOrderApi implements ApiHandle {

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService; 
	@Resource
	AfUserBankcardService afUserBankcardService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> params = requestDataVo.getParams();
		Long orderId = NumberUtil.objToLongDefault(params.get("orderId"), null);
		if (orderId == null) {
			logger.error("orderId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo ==  null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		Long userId = orderInfo.getUserId();
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserBankcardDo bankInfo = afUserBankcardService.getUserMainBankcardByUserId(userId);
		BoluomeConfirmOrderVo vo = buildConfirmOrderVo(orderInfo,userDto, authDo, bankInfo);
		resp.setResponseData(vo);
		return resp;
	}
	
	private BoluomeConfirmOrderVo buildConfirmOrderVo(AfOrderDo orderInfo, AfUserAccountDto userDto, AfUserAuthDo authDo, AfUserBankcardDo bankInfo){
		BoluomeConfirmOrderVo vo = new BoluomeConfirmOrderVo();
		vo.setRid(orderInfo.getRid());
		vo.setGoodsName(orderInfo.getGoodsName());
		vo.setSaleAmount(orderInfo.getSaleAmount());
		vo.setRebateAmount(orderInfo.getRebateAmount());
		vo.setGmtPayEnd(orderInfo.getGmtPayEnd());
		vo.setCurrentTime(new Date());
		vo.setBankcardStatus(authDo.getBankcardStatus());
    	vo.setRealName(userDto.getRealName());
        if(StringUtil.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())){
        	String publicKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        	vo.setYdKey(publicKey);
        	vo.setYdUrl(ConfigProperties.get(Constants.CONFKEY_YOUDUN_NOTIFY));
        	vo.setIdNumber(Base64.encodeString(userDto.getIdNumber()));
        	
        } else {
        	vo.setBankId(bankInfo.getRid());
        	vo.setBankName(bankInfo.getBankName());
        	vo.setBankIcon(bankInfo.getBankIcon());
        }
		vo.setMobileStatus(authDo.getMobileStatus());
		vo.setRealNameStatus(authDo.getRealnameStatus());
		vo.setTeldirStatus(authDo.getTeldirStatus());
		vo.setTotalAmount(userDto.getAuAmount());
		vo.setUseableAmount(userDto.getAuAmount().subtract(userDto.getUsedAmount()).subtract(userDto.getFreezeAmount()));
		vo.setZmStatus(authDo.getZmStatus());
		vo.setGmtZm(authDo.getGmtZm());
		if(StringUtil.equals(authDo.getRealnameStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())){
			String authParamUrl =  ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
			vo.setZmxyAuthUrl(authParamUrl);
		}
		vo.setZmScore(authDo.getZmScore());
		vo.setRealNameScore(authDo.getRealnameScore());
		return vo;
	}

}
