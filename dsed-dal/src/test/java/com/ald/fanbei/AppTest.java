package com.ald.fanbei;

import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;
import com.alibaba.fastjson.JSON;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        
        String upsValue = "{\"dailyLimit\":5,\"isMaintain\":0,\"limitDown\":0,\"limitUp\":0.5,\"maintainEndtime\":\"\",\"maintainStarttime\":\"\"}";
	UpsBankStatusDto bankStatus = JSON.parseObject(upsValue.toString(), UpsBankStatusDto.class);
	System.out.print(bankStatus.toString());
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
