package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfRepaymentConfirmVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author honghzengpei 2017/9/25 11:36
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRepaymentConfirmInfoV1Api")
public class GetRepaymentConfirmInfoV1Api implements ApiHandle {

    private final static int EXPIRE_DAY = 2;
    @Resource
    private AfBorrowBillService afBorrowBillService;

    @Resource
    private AfUserAccountService afUserAccountService;

    @Resource
    private AfUserCouponService afUserCouponService;


    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String billIds = ObjectUtils.toString(requestDataVo.getParams().get("billIds"),"");

        //账户关联信息
        AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
        resp.setResponseData(getRepaymentConfirmVo(userDto, billIds, userId));
        return resp;
    }

    private AfRepaymentConfirmVo getRepaymentConfirmVo(AfUserAccountDto userDto, String billIds, Long userId){
        AfRepaymentConfirmVo vo = new AfRepaymentConfirmVo();
        vo.setRebateAmount(userDto.getRebateAmount());
        vo.setJfbAmount(userDto.getJfbAmount().intValue());
        List<Long > ids = new ArrayList<Long>();
        String _billIds[] = billIds.split(",");
        for(String id : _billIds){
            Long _id = Long.parseLong(id);
            ids.add(_id);
        }

        AfBorrowBillDo billDo = afBorrowBillService.getTotalMonthlyBillByIds(userId, ids);
        vo.setRepayAmount(billDo.getBillAmount());
        vo.setBillId(billDo.getBillIds());

        List<AfUserCouponDto> couponDto = afUserCouponService.getUserBillCouponByUserIdAndType(userId, CouponType.BORROWBILL.getCode(), vo.getRepayAmount());
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
                if(StringUtil.isNotBlank(afUserCouponDto.getType())){
                    if(StringUtil.equals("BORROWBILL",afUserCouponDto.getType())){
                        afUserCouponDto.setType("REPAYMENT");
                    }
                }
                userCoupon.setType(afUserCouponDto.getType());
                couponList.add(userCoupon);
            }
            vo.setCouponList(couponList);
        }
        return vo;
    }
}
