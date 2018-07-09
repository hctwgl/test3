package com.ald.fanbei.unit.test;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ald.fanbei.api.dal.dao.DsedUserDao;
import com.ald.fanbei.api.dal.domain.DsedUserDo;

public class DemoTest extends JunitBaseTest {

    @Autowired
    private DsedUserDao userDao;

    @Test
    public void getUserInfo(){
        DsedUserDo userDo = userDao.getById(23213L);
        System.out.println(userDo.getRid());
    }
    
}
