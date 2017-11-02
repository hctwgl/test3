package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfH5BoluomeActivityService;
import com.ald.fanbei.api.dal.dao.AfH5BoluomeActivityDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;


@Service("AfH5BoluomeActivityService")
public class AfH5BoluomeActivityServiceImpl  implements AfH5BoluomeActivityService{
 @Resource
 AfH5BoluomeActivityDao afH5BoluomeActivityDao;
	@Override
	public int saveUserLoginInfo(AfBoluomeActivityUserLoginDo afBoluomeActivityUserLogin) {
		// TODO Auto-generated method stub
		return afH5BoluomeActivityDao.saveUserLoginInfo(afBoluomeActivityUserLogin);
	}

}
