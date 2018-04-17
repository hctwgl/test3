package com.ald.fanbei;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NewTest extends JunitBaseTest {

    @Autowired
    private AfUserDao userDao;


    @Test
    public void getUserInfo(){
        AfUserDo userDo = userDao.getUserByMobile("13018933980");
        System.out.println(userDo.getRid());
    }
}
