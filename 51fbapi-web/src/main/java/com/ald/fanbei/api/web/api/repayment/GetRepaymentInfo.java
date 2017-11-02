package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

/**
 * @author honghzengpei 2017/9/11 16:35
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRepaymentInfoApi")
public class GetRepaymentInfo  implements ApiHandle{


    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    private AfUserCouponService afUserCouponService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        long userId = context.getUserId();
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        List<HashMap> list = afBorrowBillService.getBorrowBillNoPaySumByUserId(userId);
        List<HashMap> out = new ArrayList<HashMap>();
        List<HashMap> notOut = new ArrayList<HashMap>();

        BigDecimal allNoOut = BigDecimal.ZERO;
        for (HashMap map : list) {
            HashMap m = new HashMap();
            int bill_year = Integer.parseInt(map.get("bill_year").toString());
            int bill_month = Integer.parseInt(map.get("bill_month").toString());
            if (isOut(bill_year, bill_month)) {
                m.put("month", bill_month + "月帐单");
                m.put("totalAmount", map.get("totalAmount"));
                m.put("interest", map.get("interest"));
                if(map.get("overdue_status").toString().equals("Y")){
                    m.put("status", "已逾期");
                }
                else {
                    m.put("status", "已出帐单");
                }
                out.add(m);
            } else {
                notOut.add(m);
                allNoOut = allNoOut.add(new BigDecimal(map.get("totalAmount").toString()));
            }
        }
        HashMap ret = new HashMap();
        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        ret.put("rebateAmount",afUserAccountDo.getRebateAmount());

        if (allNoOut.compareTo(BigDecimal.ZERO) > 0) {
            ret.put("noOutMoney", allNoOut);
        }
        ret.put("list", out);

        List<AfUserCouponDto> couponDto = afUserCouponService.getUserCouponByUserIdAndType(userId, CouponType.REPAYMENT.getCode(), BigDecimal.valueOf(200000L));
        ret.put("couponList",couponDto);
        resp.setResponseData(ret);
        return resp;
    }

    private boolean isOut(int year,int month){
        Date  d = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.YEAR,year);
        c1.set(Calendar.MONTH,month-1);
        c1.set(Calendar.DAY_OF_MONTH,10);
        c1.set(Calendar.HOUR_OF_DAY,0);
        c1.set(Calendar.MINUTE,0);
        c1.set(Calendar.SECOND,0);
        SimpleDateFormat  s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String a = s.format(c1.getTime());
        boolean flag = c1.getTime().before(d);
        return flag;
    }

}
