package com.ald.fanbei;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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
    }

    /**
     * @return the suite of tests being tested
     * @throws UnsupportedEncodingException 
     */
    public static Test suite() throws UnsupportedEncodingException
    {
	
	System.out.println(URLDecoder.decode("https%3A%2F%2Fyapp.51fanbei.com%2Ffanbei-web%2Factivity%2FbarginIndex%3FuserName%3D13666745600", "utf-8"));
	System.out.println(URLDecoder.decode("http%3A%2F%2Fktestapp.51fanbei.com%2Ffanbei-web%2Factivity%2FbarginIndex%3FuserName%3D13666745600", "utf-8"));
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
