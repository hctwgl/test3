package com.ald.fanbei.api.web.api.recommend;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @类描述：活动全国数据列表
 * @author  2017年hongzhengpei08月10日上午11:32:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
@Component("getRecommendListSortApi")
public class GetRecommendListSortApi implements ApiHandle {

    @Resource
    AfRecommendUserService afRecommendUserService;

    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request){
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Date start = getDate(false);
        Date end = getDate(true);



        List<HashMap> list = afRecommendUserService.getRecommendListSort(start,end);
        resp.setResponseData(list);
        return  resp;
    }


    private Date getDate(boolean isLast){
        if(!isLast) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
            //将小时至0
            c.set(Calendar.HOUR_OF_DAY, 0);
            //将分钟至0
            c.set(Calendar.MINUTE, 0);
            //将秒至0
            c.set(Calendar.SECOND, 0);
            //将毫秒至0
            c.set(Calendar.MILLISECOND, 0);
            Date start = c.getTime();
            return  start;
        }

        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至0
        ca.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至0
        ca.set(Calendar.MINUTE, 59);
        //将秒至0
        ca.set(Calendar.SECOND,59);
        //将毫秒至0
        ca.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        Date last = ca.getTime();
        return  last;
    }
}