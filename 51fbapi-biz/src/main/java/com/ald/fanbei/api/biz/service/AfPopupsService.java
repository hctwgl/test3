package com.ald.fanbei.api.biz.service;

import java.util.HashMap;
import java.util.List;






import com.ald.fanbei.api.dal.domain.AfPopupsDo;




/**
 * 
 * @类描述：类目service
 * @author 车飞鹏 2017年2月16日下午5:24:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPopupsService {


	
	AfPopupsDo selectPopups(Long id);
	
	int updatePopups(AfPopupsDo afPopupsDo);

	int updatePopupsReachAmount(Long id);

}
