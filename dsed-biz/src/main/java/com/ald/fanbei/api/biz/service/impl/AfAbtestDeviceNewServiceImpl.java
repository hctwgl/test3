package com.ald.fanbei.api.biz.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAbtestDeviceNewDao;
import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;
import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;



/**
 * 用户设备号记录表ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-06 19:59:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAbtestDeviceNewService")
public class AfAbtestDeviceNewServiceImpl extends ParentServiceImpl<AfAbtestDeviceNewDo, Long> implements AfAbtestDeviceNewService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAbtestDeviceNewServiceImpl.class);
   
    @Resource
    private AfAbtestDeviceNewDao afAbtestDeviceNewDao;

		@Override
	public BaseDao<AfAbtestDeviceNewDo, Long> getDao() {
		return afAbtestDeviceNewDao;
	}

		@Override
		public void addUserDeviceInfo(AfAbtestDeviceNewDo abTestDeviceDo) {
		    // TODO Auto-generated method stub
		    Date day=new Date();   
		    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
		    String c=sdf.format(day);
		    int date = Integer.parseInt(c);
		    abTestDeviceDo.setLoginDate(date);
		    afAbtestDeviceNewDao.addUserDeviceInfo(abTestDeviceDo);
		}
}