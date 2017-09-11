package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

/**
 * @author honghzengpei 2017/9/11 16:35
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GetRepaymentInfo  implements ApiHandle{


    @Resource
    AfBorrowBillService afBorrowBillService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        long userId = context.getUserId();

        List<HashMap> list = afBorrowBillService.getBorrowBillNoPaySumByUserId(userId);
        List<HashMap> out = new ArrayList<HashMap>();
        List<HashMap> notOut = new ArrayList<HashMap>();

        for (HashMap map : list){
            int bill_year = Integer.parseInt(map.get("bill_year").toString());
            int bill_month = Integer.parseInt(map.get("bill_month").toString());
            if( isOut(bill_year,bill_month)){
                
            }
            else{

            }
        }
        return null;
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
