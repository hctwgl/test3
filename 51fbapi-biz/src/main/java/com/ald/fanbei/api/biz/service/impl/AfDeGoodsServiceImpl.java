package com.ald.fanbei.api.biz.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.dal.dao.AfDeGoodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;

/**
 * 双十一砍价ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:19 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afDeGoodsService")
public class AfDeGoodsServiceImpl extends ParentServiceImpl<AfDeGoodsDo, Long> implements AfDeGoodsService {

    private static final Logger logger = LoggerFactory.getLogger(AfDeGoodsServiceImpl.class);

    @Resource
    private AfDeGoodsDao afDeGoodsDao;
    @Resource
    AfResourceService afResourceService;


	@Override
	public BaseDao<AfDeGoodsDo, Long> getDao() {
		return afDeGoodsDao;
	}
	

	@Override
	public long getActivityEndTime() {
		//afResourceService
		AfResourceDo  afResourceDo =  afResourceService.getConfigByTypesAndSecType("DECUTPRICE", "ENDTIME");
		long endTime =  0 ; 
		if(afResourceDo != null ){
		    String value =   afResourceDo.getValue();
			  //转时间戳
		    SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    try {
			endTime = sdf.parse(value).getTime();
		    } catch (ParseException e) {
			e.printStackTrace();
			logger.info( "decutprice endTime:",endTime);
		    }
		}
		    return endTime;
	}

		@Override
	public long getActivityTotalCount() {
		    // TODO Auto-generated method stub
	       return afDeGoodsDao.getActivityTotalCount();
	}

 
    @Override
    
    public List<UserDeGoods> getUserDeGoodsList(Long userId) {

	return afDeGoodsDao.getUserDeGoodsList(userId);
    }

    @Override
    public AfDeUserGoodsInfoDto getGoodsInfo(AfDeGoodsDo afDeGoodsDo) {
	// TODO Auto-generated method stub
	return afDeGoodsDao.getGoodsInfo(afDeGoodsDo);
    }


    @Override
    public int getIniNum() {
	// TODO Auto-generated method stub
	//afResourceService
	int iniNum =  0 ; 
	  try {
        	AfResourceDo  afResourceDo =  afResourceService.getConfigByTypesAndSecType("DECUTPRICE", "ININUM");
        	
        	 if(afResourceDo != null ){
        	     iniNum =   Integer.parseInt(afResourceDo.getValue());
        	 } 
	       }catch (Exception e) {
		   e.printStackTrace();
		   logger.info( "decutprice iniNum:",iniNum);
	    }
        	return iniNum;
	}
}