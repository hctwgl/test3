package com.ald.fanbei.api.web.api.repayment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfRepaymentConfirmVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;

/**
 * 
 *@类描述：获取还款确认页面Api
 *@author 何鑫 2017年2月18日  16:46:23
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRepaymentConfirmInfoApi")
public class GetRepaymentConfirmInfoApi implements ApiHandle{

	private final static int EXPIRE_DAY = 2;
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfUserCouponService afUserCouponService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long billId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("billId")), 0l);
		int billYear = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("billYear")), 0);
		int billMonth = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("billMonth")), 0);
		//账户关联信息
		AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
		resp.setResponseData(getRepaymentConfirmVo(userDto, billId, userId, billYear, billMonth));
		return resp;
	}
	
	private AfRepaymentConfirmVo getRepaymentConfirmVo(AfUserAccountDto userDto,Long billId,Long userId,
			int billYear,int billMonth ){
		AfRepaymentConfirmVo vo = new AfRepaymentConfirmVo();
		vo.setRebateAmount(userDto.getRebateAmount());
		vo.setJfbAmount(userDto.getJfbAmount().intValue());
		if(billId>0){//单笔还款
			AfBorrowBillDo billDo = afBorrowBillService.getBorrowBillById(billId);
			vo.setRepayAmount(billDo.getBillAmount());
			vo.setBillId(billDo.getRid()+"");
		}else{//月账单还款
			AfBorrowBillDo billDo = afBorrowBillService.getTotalMonthlyBillByUserId(userId, billYear, billMonth);
			vo.setRepayAmount(billDo.getBillAmount());
			vo.setBillId(billDo.getBillIds());
		}
		List<AfUserCouponDto> couponDto = afUserCouponService.getUserCouponByUserIdAndType(userId, CouponType.REPAYMENT.getCode(), vo.getRepayAmount());
		if(null != couponDto && couponDto.size()>0){
			AfUserCouponDto coupon = couponDto.get(0);
			vo.setCouponAmount(coupon.getAmount());
			vo.setCouponId(coupon.getRid());
			vo.setCouponName(coupon.getName());
			List<AfUserCouponVo> couponList = new ArrayList<AfUserCouponVo>();
			for (AfUserCouponDto afUserCouponDto : couponDto) {
				AfUserCouponVo userCoupon = new AfUserCouponVo();
				userCoupon.setRid(afUserCouponDto.getRid());
				userCoupon.setAmount(afUserCouponDto.getAmount());
				userCoupon.setGmtEnd(afUserCouponDto.getGmtEnd());
				Date gmtEnd = userCoupon.getGmtEnd();
				// 如果当前时间离到期时间小于48小时,则显示即将过期
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
				Date twoDay = cal.getTime();
				if(gmtEnd != null){
					if(twoDay.after(gmtEnd)) {
						userCoupon.setWillExpireStatus("Y");
					} else {
						userCoupon.setWillExpireStatus("N");
					}
				} else {
					userCoupon.setWillExpireStatus("N");
				}
				userCoupon.setGmtStart(afUserCouponDto.getGmtStart());
				userCoupon.setLimitAmount(afUserCouponDto.getLimitAmount());
				userCoupon.setName(afUserCouponDto.getName());
				userCoupon.setStatus(afUserCouponDto.getStatus());
				userCoupon.setUseRule(afUserCouponDto.getUseRule());
				userCoupon.setType(afUserCouponDto.getType());
				couponList.add(userCoupon);
			}
			vo.setCouponList(couponList);
		}
		return vo;
	}
	
}
