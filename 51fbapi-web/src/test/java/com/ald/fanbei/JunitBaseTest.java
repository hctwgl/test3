package com.ald.fanbei;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test/spring-test.xml","classpath:spring/web/web-main.xml"})
public class JunitBaseTest {

}
