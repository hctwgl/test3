package com.ald.fanbei.api.web.api.bill;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
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
    AfUserOutDayDao afUserOutDayDao;
    @Resource
    AfBorrowService afBorrowService;
    @Resource
    AfBorrowBillDao afBorrowBillDao;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        Long userId = context.getUserId();
        //Integer status = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("status")), 1);  //1 己出，2逾期，3 未出
        List<AfBorrowBillDo> list = afBorrowService.getBorrowBillList("N",userId);
        HashMap map = new HashMap();
        List<HashMap> billList1 = getListByStatus(list,1);
        List<HashMap> billList2 = getListByStatus(list,2);
        List<HashMap> billList3 = getListByStatus(list,3);
        map.put("billList1",billList1);   //未还，本期
        map.put("billList2",billList2);   //逾期
        map.put("billList3",billList3);   //未出
        //map.put("needPay",needPayAmount(list));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //本期还款日
        Date repayDate = afBorrowService.getReyLimitDate("C",new Date());

        map.put("repayDate",simpleDateFormat.format(repayDate));

        Calendar n = Calendar.getInstance();
        n.add(Calendar.MONTH,-1);
        String _n =simpleDateFormat.format(n.getTime());

        String[] __n = _n.split("-");
        AfBorrowTotalBillDo afBorrowTotalBillDo = afBorrowBillDao.getBorrowBillTotalNow(userId,Integer.parseInt( __n[0]),Integer.parseInt(__n[1]));
        if(afBorrowTotalBillDo !=null){
            map.put("repayDate",simpleDateFormat.format(afBorrowTotalBillDo.getGmtPayTime()));
        }
        else {
            String[] d = map.get("repayDate").toString().split("-");
            AfUserOutDayDo afUserOutDayDo = afUserOutDayDao.getUserOutDayByUserId(userId);
            if (afUserOutDayDo != null) {

                String payDay = "";
                if (afUserOutDayDo.getPayDay() >= 10) {
                    payDay = afUserOutDayDo.getPayDay().toString();
                } else {
                    payDay = "0" + afUserOutDayDo.getPayDay().toString();
                }
                map.put("repayDate", d[0] + "-" + d[1] + "-" + payDay);
            }
        }


        //处理己还的
        Map<String,Integer> map22 = afBorrowService.getCurrentTermYearAndMonth("C",new Date());
        List<AfBorrowBillDo> listY = afBorrowService.getBorrowBillListY(userId,map22.get(Constants.DEFAULT_YEAR),map22.get(Constants.DEFAULT_MONTH));
        List<HashMap> mapY = new ArrayList<HashMap>();
        for(AfBorrowBillDo afBorrowBillDo: listY){
            HashMap _map = new HashMap();

            String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
            _map.put("billId",afBorrowBillDo.getRid());
            _map.put("date",date);
            _map.put("name",afBorrowBillDo.getName());
            _map.put("totalAmount",afBorrowBillDo.getBillAmount());
            mapY.add(_map);

        }
        map.put("billList4",mapY);

        map.put("msg1","未还款帐单共"+billList1.size()+"笔,￥"+getAmount(billList1));
        map.put("msg2","逾期未还款帐单共"+billList2.size()+"笔,￥"+getAmount(billList2));
        map.put("msg3","未出帐单共"+billList3.size()+"笔,￥"+getAmount(billList3));
        map.put("msg4","已还款帐单共"+mapY.size()+"笔,￥"+getAmount(mapY));

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(map);
        return resp;
    }

    private BigDecimal getAmount(List<HashMap> list){
        BigDecimal ret = BigDecimal.ZERO;
        for(HashMap map :list){
            ret =  ret.add((BigDecimal) map.get("totalAmount"));
        }
        return ret;
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
            return null;
        }
        if(status ==1){
            if(afBorrowBillDo.getOverdueStatus().equals("Y")){
                return null;
            }
//            if(isOut(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth())){
            if(afBorrowBillDo.getIsOut().intValue() ==1){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("billId",afBorrowBillDo.getRid());
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
                map.put("billId",afBorrowBillDo.getRid());
                map.put("date",date);
                map.put("name",afBorrowBillDo.getName());
                map.put("totalAmount",afBorrowBillDo.getBillAmount());
            }
            return map;
        }
        else if (status ==3){
//            if(!isOut(afBorrowBillDo.getBillYear(),afBorrowBillDo.getBillMonth())){
            if(afBorrowBillDo.getIsOut().intValue() == 0){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date = simpleDateFormat.format(afBorrowBillDo.getGmtCreate());
                map.put("billId",afBorrowBillDo.getRid());
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
        if(month > 12){
            year = year +1;
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
