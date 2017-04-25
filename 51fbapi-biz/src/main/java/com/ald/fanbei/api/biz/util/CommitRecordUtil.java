package com.ald.fanbei.api.biz.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfCommitRecordService;
import com.ald.fanbei.api.dal.domain.AfCommitRecordDo;



/**
 *@类描述：
 *@author fumeiai 2017年4月24日下午18:18:45
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("commitRecordUtil")
public class CommitRecordUtil {
	@Resource
	AfCommitRecordService afCommitRecordService;
	/**
	 * 
	 * @param 
	 * @return
	 */
	public void addRecord(String type, String relate_id, String content, String url) {
		Date nowdate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		String commit_time = sdf.format(nowdate);
		AfCommitRecordDo afCommitRecordDo = new AfCommitRecordDo();
		afCommitRecordDo.setType(type);
		afCommitRecordDo.setRelate_id(relate_id);
		afCommitRecordDo.setContent(content);
		afCommitRecordDo.setUrl(url);
		afCommitRecordDo.setCommit_time(commit_time);
		afCommitRecordDo.setCommit_num(1);
		afCommitRecordService.addRecord(afCommitRecordDo);
    }
}
