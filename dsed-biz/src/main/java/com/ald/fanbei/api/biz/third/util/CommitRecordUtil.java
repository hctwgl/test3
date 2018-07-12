package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.service.DsedCommitRecordService;
import com.ald.fanbei.api.dal.domain.DsedCommitRecordDo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *@类描述：
 *@author fumeiai 2017年4月24日下午18:18:45
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("commitRecordUtil")
public class CommitRecordUtil {
	@Resource
	DsedCommitRecordService dsedCommitRecordService;
	/**
	 * 
	 * @param 
	 * @return
	 */
	public void addRecord(String type, String relate_id, String content, String url) {
		Date nowdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String commit_time = sdf.format(nowdate);
		DsedCommitRecordDo dsedCommitRecordDo = new DsedCommitRecordDo();
		dsedCommitRecordDo.setType(type);
		dsedCommitRecordDo.setRelate_id(relate_id);
		dsedCommitRecordDo.setContent(content);
		dsedCommitRecordDo.setUrl(url);
		dsedCommitRecordDo.setCommit_time(commit_time);
		dsedCommitRecordDo.setCommit_num(1);
		dsedCommitRecordService.addRecord(dsedCommitRecordDo);
    }
	
	public DsedCommitRecordDo getRecord(String relate_id) {
		return dsedCommitRecordService.getRecordByTypeAndRelateId(relate_id);
    }
	
}
