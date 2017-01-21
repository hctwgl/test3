package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfYoudunFaceDo;

/**
 *@类描述：有盾人脸识别类
 *@author 陈金虎 2017年1月21日 下午3:46:46
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
public interface AfYoudunFaceDao {
	/**
	 * 增加有盾人脸识别结果数据
	 * @param afYoudunFaceDo
	 */
	void addYoudunFace(AfYoudunFaceDo afYoudunFaceDo);
}
