package com.ald.fanbei;
import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;

public class NewTest extends JunitBaseTest {

    @Autowired
    private AfUserDao userDao;
    @Autowired
    private AfUserAccountSenceDao afUserAccountSenceDao;

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
    	accSceneDo.setAuAmount(new BigDecimal(5000));
	    afUserAccountSenceDao.updateById(accSceneDo);
    }
}
