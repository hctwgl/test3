package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.AfPushManageService;
import com.ald.fanbei.api.dal.dao.AfPushManageDao;
import com.ald.fanbei.api.dal.domain.AfPushManageDo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author guoshuaiqiang 2017年10月12日下午2:26:48
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afPushManageService")
public class AfPushManageServiceImpl implements AfPushManageService {

    @Resource
    private AfPushManageDao afPushManageDao;

    @Override
    public AfPushManageDo getPushById(Long pushId) {
        return afPushManageDao.getById(pushId);
    }

    @Override
    public int updatePushManage(AfPushManageDo afPushManageDo) {

        return afPushManageDao.updateById(afPushManageDo);
    }

}
