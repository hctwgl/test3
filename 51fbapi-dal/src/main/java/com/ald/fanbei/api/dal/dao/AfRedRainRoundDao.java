package com.ald.fanbei.api.dal.dao;

import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;

/**
 * @author ZJF 2017年3月1日 上午10:10:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRedRainRoundDao {

	AfRedRainRoundDo fetch(AfRedRainRoundDo paramRound);
	
    int update(AfRedRainRoundDo afRedRainRoundDo);
    
    List<AfRedRainRoundDo> queryByGmtStartInToday(Map<String,Object> params);
}
