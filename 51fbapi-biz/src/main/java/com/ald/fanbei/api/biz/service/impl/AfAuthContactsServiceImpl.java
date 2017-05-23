package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.dal.dao.AfAuthContactsDao;
import com.ald.fanbei.api.dal.domain.AfAuthContactsDo;

/**
 * @类现描述：
 * 
 * @author chenjinhu 2017年2月16日 下午2:29:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAuthContactsService")
public class AfAuthContactsServiceImpl implements AfAuthContactsService {

    @Resource
    AfAuthContactsDao afAuthContactsDao;

    @Override
    public int addAuthContacts(List<AfAuthContactsDo> afAuthContactsDos) {
        return afAuthContactsDao.addAuthContacts(afAuthContactsDos);
    }

    @Override
    public List<AfAuthContactsDo> getContactsByUserId(Long userId) {
        return afAuthContactsDao.getContactsByUserId(userId);
    }

}
