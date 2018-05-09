package com.ald.fanbei.api.biz.third.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.RiskTrackerService;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.RiskTrackerDo;
import com.alibaba.fastjson.JSON;

@Component
public class RiskRequestProxy {
    private final Logger riskLogger = LoggerFactory.getLogger("FBRISK_BI");//app原生接口入口日志
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String TRACK_BEGIN_FORMAT = "%s => begin,request url:%s ,params: %s";
    private static final String TRACK_END_FORMAT = "%s => end, request url:%s , params: %s , result: %s ";
    private static final String TRACK_ERROR_FORMAT = "%s => error, request url:%s , params:%s ,";
    @Resource
    RiskTrackerService riskTrackerService;
    private static final String TRACK_PREFIX = "track_";

    public String post(String url, Map<String, String> params) {
        String consumerNo=StringUtil.isEmpty(params.get("consumerNo"))?"consumerNo":params.get("consumerNo");//用户id
        String orderNo= StringUtil.isEmpty(params.get("orderNo"))?"orderNo":params.get("orderNo");//如果没有客户单号，一般不可能

        String trackId =TRACK_PREFIX + new Date().getTime();
        String result = "";
        Calendar calStart = Calendar.getInstance();
        RiskTrackerDo riskTrackerDo = new RiskTrackerDo();
        logger.info(String.format(TRACK_BEGIN_FORMAT, trackId, url, JSON.toJSONString(params)));
        try {
            riskTrackerDo.setGmtCreate(new Date());
            String paramsStr=JSON.toJSONString(params);
            riskTrackerDo.setParams(paramsStr.length()>2048?paramsStr.substring(0,2000)+"|参数过长，无法处理":paramsStr);
            riskTrackerDo.setTrackId(trackId);
            riskTrackerDo.setUrl(url);
            result = HttpUtil.post(url, params);
            try{
                if (StringUtil.isEmpty(result)) {//返回结果为空，直接进数据库
                    riskTrackerDo.setResult("empty result!");
                    riskTrackerService.saveRecord(riskTrackerDo);
                }

                Calendar calEnd = Calendar.getInstance();
                riskLogger.info(StringUtil.appendStrs("	",url,
                        "	",trackId,
                        "	",consumerNo,
                        "	",orderNo,
                        "	",(calEnd.getTimeInMillis() - calStart.getTimeInMillis()),
                        "	", JSON.toJSONString(params),
                        "	",result));//其他的全部进行埋点
            }catch (Exception e){
                //不影响之前的逻辑
                logger.error(String.format("I_"+TRACK_ERROR_FORMAT, trackId, url, params), e);
            }

        } catch (Exception e) {
            //出现异常直接进入数据库
            logger.error(String.format(TRACK_ERROR_FORMAT, trackId, url, params), e);
            riskTrackerDo.setResult("http request exception!");
            riskTrackerService.saveRecord(riskTrackerDo);
            throw  e;
        }
        return result;
    }
}
