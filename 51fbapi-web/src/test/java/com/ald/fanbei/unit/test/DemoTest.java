package com.ald.fanbei.unit.test;
import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.alibaba.fastjson.JSON;

public class DemoTest extends JunitBaseTest {

    @Autowired
    private AfUserDao userDao;
    @Autowired
    private AfUserAccountSenceDao afUserAccountSenceDao;
    
    @Autowired
    private AfBorrowLegalService afBorrowLegalService;

    @Test
    public void getUserInfo(){
        AfUserDo userDo = userDao.getUserByMobile("13018933980");
        System.out.println(userDo.getRid());
    }
    
    @Test
    public void updateUserAccountScene(){
    	AfUserAccountSenceDo accSceneDo = new AfUserAccountSenceDo();
    	accSceneDo.setRid(36535776L);
    	accSceneDo.setScene("LOAN_TOTAL");
    	accSceneDo.setAuAmount(new BigDecimal(2000));
	    afUserAccountSenceDao.updateById(accSceneDo);
    }
    
    @Test
    public void getHomeInfo() {
    	System.out.println(JSON.toJSONString(afBorrowLegalService.getHomeInfo(AccountOfTester.俞佳楠.userId), true));
    }
}
