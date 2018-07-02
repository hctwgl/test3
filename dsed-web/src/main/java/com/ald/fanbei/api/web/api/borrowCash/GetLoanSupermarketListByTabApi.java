package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfLoanSupermarketDto;
import com.ald.fanbei.api.dal.domain.query.AfBusinessAccessRecordQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.common.util.DateUtil;


/**
 * @author 沈铖 2017/7/5 下午4:37
 * @类描述: GetLoanSupermarketListByTabApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLoanSupermarketListByTabApi")
public class GetLoanSupermarketListByTabApi implements ApiHandle {

    @Resource
    private AfLoanSupermarketDao afLoanSupermarketDao;
    @Resource
    AfGameDao afGameDao;
    @Resource
    AfBusinessAccessRecordsDao afBusinessAccessRecordsDao;
    @Resource
    AfGameConfDao afGameConfDao;
    @Resource
    AfCouponDao afCouponDao;
    @Resource
    AfBusinessAccessRecordsService afBusinessAccessRecordsService;
    @Resource
    AfResourceDao afResourceDao;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String label = ObjectUtils.toString(requestDataVo.getParams().get("label"), null);
        String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"), null);
        int systemType=3;
        if(StringUtil.isNotEmpty(osType)){
            if(StringUtil.equals("iOS",osType)){
                systemType=1;
            }else if(StringUtil.equals("Android",osType)){
                systemType=2;
            }
        }
        List<AfLoanSupermarketDto> afLoanSupermarketDtoList = new ArrayList<AfLoanSupermarketDto>();
        List<AfLoanSupermarketDo> sourceSupermarketList = afLoanSupermarketDao.getLoanSupermarketByLabel(label,systemType+"");
        AfResourceDo afResourceDo = new AfResourceDo();
        afResourceDo = afResourceDao.getSingleResourceBytype("unionregister");
        String unionRegisterUrl = afResourceDo.getValue();
        HashMap<String,String> hashMap = new HashMap<String,String>();
        for(int n=0;n<sourceSupermarketList.size();n++){
            AfLoanSupermarketDto afLoanSupermarketDto = getDto(sourceSupermarketList.get(n),unionRegisterUrl,true);
            afLoanSupermarketDtoList.add(afLoanSupermarketDto);
        }
        //查询是否存在借贷超市活动
        AfGameDo afGameDo = afGameDao.getByCode("loan_supermaket_sign");
        boolean flag = true;
        if(afGameDo == null){
            flag = false;
        }else {//判断活动是否过期
            Date gmtStart = afGameDo.getGmtStart();
            Date gmtEnd = afGameDo.getGmtEnd();
            if (DateUtil.beforeDay(new Date(), gmtStart)) {
                flag = false;
            }
            if (DateUtil.afterDay(new Date(), gmtEnd)) {
                flag = false;
            }
        }
        List<AfLoanSupermarketDto> desSupermarketList = dealLoanSupermarkets(afLoanSupermarketDtoList,context,flag,afGameDo);
        //判断是否已签到，签到天数
        if(flag){
            hashMap = popupsHint(context,afGameDo);
        }
        resp.addResponseData("supermarketList",desSupermarketList);
        resp.addResponseData("popupsHint",hashMap);
        return resp;
    }

    public List<AfLoanSupermarketDto> dealLoanSupermarkets(List<AfLoanSupermarketDto> sourceSupermarketList,FanbeiContext context,boolean flag,AfGameDo afGameDo) {
    	List<AfLoanSupermarketDto> desSupermarketList = new ArrayList<AfLoanSupermarketDto>();
    	for (AfLoanSupermarketDto tempLoanMarket : sourceSupermarketList) {
    		String linkUrl = StringUtil.null2Str(tempLoanMarket.getLinkUrl()).replaceAll("\\*", "\\&");
            AfLoanSupermarketDto afLoanSupermarketDto = getDto(tempLoanMarket,"",false);
            afLoanSupermarketDto.setLinkUrl(linkUrl);
            if(flag){
                AfLoanSupermarketDto Dto = new AfLoanSupermarketDto();
                AfBusinessAccessRecordQuery query = new AfBusinessAccessRecordQuery();
                query.setBeginTime(afGameDo.getGmtStart());
                query.setEndTime(afGameDo.getGmtEnd());
                query.setUserId(context.getUserId());
                List<AfBusinessAccessRecordsDo> list = afBusinessAccessRecordsDao.getTotalSignList(query);
                if(null != list && list.size()>0){
                    for (AfBusinessAccessRecordsDo afBusinessAccessRecordsDoList : list) {
                        if(afBusinessAccessRecordsDoList.getRefId().equals(tempLoanMarket.getId())){
                            afLoanSupermarketDto.setRemake("sign");
                        }
                    }
                }
            }
            desSupermarketList.add(afLoanSupermarketDto);
		}
		return desSupermarketList;

	}

    public AfLoanSupermarketDto getDto(AfLoanSupermarketDo tempLoanMarket,String unionRegisterUrl,boolean unionProcess){
        AfLoanSupermarketDto afLoanSupermarketDto = new AfLoanSupermarketDto();
        String linkUrl = tempLoanMarket.getLinkUrl();
        String lsmNo = tempLoanMarket.getLsmNo();
        String iconUrl = tempLoanMarket.getIconUrl();
        String lsmName = tempLoanMarket.getLsmName();
        String lsmIntro = tempLoanMarket.getLsmIntro();
        String Label = tempLoanMarket.getLabel();
        String marketPoint = tempLoanMarket.getMarketPoint();
        Long id = tempLoanMarket.getId();
        afLoanSupermarketDto.setRemake("");
        afLoanSupermarketDto.setLabel(Label);
        afLoanSupermarketDto.setLsmIntro(lsmIntro);
        afLoanSupermarketDto.setIconUrl(iconUrl);
        afLoanSupermarketDto.setLsmName(lsmName);
        afLoanSupermarketDto.setMarketPoint(marketPoint);
        afLoanSupermarketDto.setLsmNo(lsmNo);
        afLoanSupermarketDto.setId(id);
        int isUnionLogin = tempLoanMarket.getIsUnionLogin();
        if(unionProcess){
            if(isUnionLogin==1){
                linkUrl = unionRegisterUrl + "?lsmNo=" + lsmNo;
            }
        }
        afLoanSupermarketDto.setLinkUrl(linkUrl);
        return afLoanSupermarketDto;
    }

    public HashMap<String,String> popupsHint(FanbeiContext context,AfGameDo gameDo){
        List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> map = new HashMap<String,String>();
        int count = 0;
        String prompt = "";
        AfBusinessAccessRecordQuery query = new AfBusinessAccessRecordQuery();
        query.setBeginTime(gameDo.getGmtStart());
        query.setEndTime(gameDo.getGmtEnd());
        query.setUserId(context.getUserId());
        query.setRefType(AfBusinessAccessRecordsRefType.LOANSUPERMARKET.getCode());
        int signDays = afBusinessAccessRecordsService.getSignDays(query);
        map.put("numDays",String.valueOf(signDays));
        boolean signed = afBusinessAccessRecordsService.checkIsSignToday(context.getUserId());
        if(signed){
            map.put("flag","true");
        }else{
            map.put("flag","false");
        }
        if(signed) {
            List<AfGameConfDo> confList = afGameConfDao.getByGameId(gameDo.getRid());
            if (confList != null && confList.size() > 0) {
                if (1 <= signDays && signDays < 5) {
                    count = 5 - signDays;
                    prompt = "继续签到"+count+"天即可获得" + "20元红包和3元现金";
                } else if (5 <= signDays && signDays < 10) {
                    count = 10 - signDays;
                    prompt = "继续签到"+count+"天即可获得" + "50元红包和15元现金";
                } else if (10 <= signDays && signDays < 15) {
                    count = 15 - signDays;
                    prompt = "继续签到"+count+"天即可获得" + "75元红包和25元现金，并且有机会获得888现金";
                }
            }
        }
        map.put("prompt",prompt);
        return map;
    }


}
