package ${packageName}.web.sys.controller;

import javax.annotation.Resource;

import com.ald.fanbei.admin.web.common.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ${packageName}.biz.service.${ClassName}Service;

 /**
 * ${functionName}Controller
 * 
 * @author ${classAuthor}
 * @version 1.0.0 初始化
 * @date ${classDate}
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class ${ClassName}Controller extends BaseController {

	@Resource
	private ${ClassName}Service ${className}Service;

}
