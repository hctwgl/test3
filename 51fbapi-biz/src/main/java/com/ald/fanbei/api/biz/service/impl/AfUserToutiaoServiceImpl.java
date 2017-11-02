package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfUserToutiaoDao;
import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;
import com.ald.fanbei.api.biz.service.AfUserToutiaoService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-05 16:39:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserToutiaoService")
public class AfUserToutiaoServiceImpl implements AfUserToutiaoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserToutiaoServiceImpl.class);
   
    @Resource
    private AfUserToutiaoDao afUserToutiaoDao;

	@Override
	public int creatUser(AfUserToutiaoDo afUserToutiaoDo){
		return afUserToutiaoDao.creatUser(afUserToutiaoDo);
	}

	@Override
	public int uptUser(Long id) {
		return afUserToutiaoDao.uptUser(id);
	}

	@Override
	public AfUserToutiaoDo getUser(String imei, String androidid, String idfa) {
		return afUserToutiaoDao.getUser(imei,androidid,idfa);
	}

	@Override
	public Long uptUserActive(Long rid,Long userIdToutiao, String userNameToutiao) {
		return afUserToutiaoDao.uptUserActive(rid,userIdToutiao,userNameToutiao);
	}

	@Override
	public AfUserToutiaoDo getUserActive(String imei, String androidid, String idfa) {
		return afUserToutiaoDao.getUserActive(imei,androidid,idfa);
	}

	@Override
	public AfUserToutiaoDo getUserOpen(String imei, String androidid, String idfa) {
		return afUserToutiaoDao.getUserOpen(imei,androidid,idfa);
	}

	@Override
	public Long uptUserOpen(Long rid, Long userIdToutiao, String userNameToutiao) {
		return afUserToutiaoDao.uptUserOpen(rid,userIdToutiao,userNameToutiao);
	}
}