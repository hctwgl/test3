package com.ald.fanbei.api.web.api.bill;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.sun.org.apache.bcel.internal.generic.RET;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author honghzengpei 2017/9/25 10:46
 * @类描述：获取帐单数据
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBillListByStatusApi")
public class GetBillListByStatusApi implements ApiHandle {
    @Resource
    AfBorrowService afBorrowService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        Long userId = context.getUserId();
        //Integer status = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("status")), 1);  //1 己出，2逾期，3 未出
        List<AfBorrowBillDo> list = afBorrowService.getBorrowBillList("N",userId);
        HashMap map = new HashMap();
        map.put("billList1",getListByStatus(list,1));   //未还，本期
        map.put("billList2",getListByStatus(list,2));   //逾期
        map.put("billList3",getListByStatus(list,3));   //未出
        //map.put("needPay",needPayAmount(list));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //本期还款日
        Date repayDate = afBorrowService.getReyLimitDate("C",new Date());
        map.put("repayDate",simpleDateFormat.format(repayDate));

        //处理己还的
        Map<String,Integer> map22 = afBorrowService.getCurrentTermYearAndMonth("C",new Date());
        List<AfBorrowBillDo> listY = afBorrowService.getBorrowBillListY(userId,map22.get(Constants.DEFAULT_YEAR),map22.get(Constants.DEFAULT_MONTH));
        List<HashMap> mapY = new ArrayList<HashMap>();
        for(AfBorrowBillDo afBorrowBillDo: listY){
            HashMap _map = new HashMap();

            String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
            _map.put("date",date);
            _map.put("name",afBorrowBillDo.getName());
            _map.put("totalAmount",afBorrowBillDo.getBillAmount());
            mapY.add(_map);

        }
        map.put("billList4",mapY);

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(map);
        return resp;
    }


    private BigDecimal needPayAmount(List<AfBorrowBillDo> list){
        BigDecimal ret = BigDecimal.ZERO;
        for(AfBorrowBillDo afBorrowBillDo : list){
            if(afBorrowBillDo.getStatus().equals("Y")){
                continue;
            }
            if(afBorrowBillDo.getOverdueStatus().equals("Y") || isOut(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth())){
                ret = ret.add(afBorrowBillDo.getBillAmount());
            }
        }
        return  ret;
    }


    private List<HashMap> getListByStatus(List<AfBorrowBillDo> list, int status){
        List<HashMap> _list = new ArrayList<HashMap>();
        for(AfBorrowBillDo afBorrowBillDo : list){
            HashMap map = getBillByStatus(afBorrowBillDo,status);
            if(map !=null && map.size()>0){
                _list.add(map);
            }
        }
        return _list;
    }

    /**
     *
     * @param status  1 己出，2 逾期，3 未出
     * @return
     */
    private HashMap getBillByStatus(AfBorrowBillDo afBorrowBillDo,int status){
        HashMap map = new HashMap();
        if(afBorrowBillDo.getStatus().equals("Y")){
            return map;
        }
        if(status ==1){
            if(afBorrowBillDo.getOverdueStatus().equals("Y")){
                return map;
            }
            if(isOut(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth())){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date",date);
                map.put("name",afBorrowBillDo.getName());
                map.put("totalAmount",afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        else if(status ==2){
            if(afBorrowBillDo.getOverdueStatus().equals("Y")){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date",date);
                map.put("name",afBorrowBillDo.getName());
                map.put("totalAmount",afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        else if (status ==3){
            if(!isOut(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth())){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("date",date);
                map.put("name",afBorrowBillDo.getName());
                map.put("totalAmount",afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        return map;
    }


    /**
     * 判断是否是已出帐单
     * @param year
     * @param month
     * @return
     */
    private boolean isOut(int year,int month){
        month = month +1;
        if(month>12){
            year = year+1;
            month =1;
        }
        Date d = new Date();
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
