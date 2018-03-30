package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.dal.dao.AfResourceH5ItemDao;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * h5商品资源管理ServiceImpl
 *
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afResourceH5ItemService")
public class AfResourceH5ItemServiceImpl implements AfResourceH5ItemService {

	@Resource
	AfResourceH5ItemDao afResourceH5ItemDao;

	@Override
	public List<AfResourceH5ItemDo> selectByModelId(Long modelId){
		return afResourceH5ItemDao.selectByModelId(modelId);
	}



}