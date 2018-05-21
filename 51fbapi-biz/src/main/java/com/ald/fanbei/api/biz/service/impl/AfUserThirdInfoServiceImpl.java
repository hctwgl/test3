package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserThirdInfoDao;
import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;



/**
 * 用户第三方信息表(与微信绑定)ServiceImpl
 * 
 * @author cfp
 * @version 1.0.0 初始化
 * @date 2018-05-21 10:30:52
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserThirdInfoService")
public class AfUserThirdInfoServiceImpl  implements AfUserThirdInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserThirdInfoServiceImpl.class);
   
    @Resource
    private AfUserThirdInfoDao afUserThirdInfoDao;


}