package ${packageName}.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ${packageName}.dal.dao.BaseDao;
import ${packageName}.dal.dao.${ClassName}Dao;
import ${packageName}.dal.domain.${ClassName}Do;
import ${packageName}.biz.service.${ClassName}Service;



/**
 * ${functionName}ServiceImpl
 * 
 * @author ${classAuthor}
 * @version 1.0.0 初始化
 * @date ${classDate}
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("${className}Service")
public class ${ClassName}ServiceImpl extends ParentServiceImpl<${ClassName}Do, Long> implements ${ClassName}Service {
	
    private static final Logger logger = LoggerFactory.getLogger(${ClassName}ServiceImpl.class);
   
    @Resource
    private ${ClassName}Dao ${className}Dao;

		@Override
	public BaseDao<${ClassName}Do, Long> getDao() {
		return ${className}Dao;
	}
}