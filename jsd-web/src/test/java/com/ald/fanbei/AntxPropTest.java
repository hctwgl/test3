package com.ald.fanbei;

import com.ald.fanbei.api.common.util.AesUtil;

public class AntxPropTest {
	public static void main(String[] args) {

    	// dev
        String decryptStr = AesUtil.decryptFromBase64("Ehw14/ML0cbFSiBoVFC1mrzrXB1CR8zrEttPsmC9ATQFZaz1ENJynuLX2tEE8+0BYG4Lm6OCyOZAUtmvKV/R/XfoHlGTdb7s2pdJVAqgoOo=", "Cw5bM6x@6sH$2dlw^3JueH");
        System.out.println(decryptStr);
       
        String dbUrl = "jdbc:mysql://rm-bp1238z9t1f3ir3j4.mysql.rds.aliyuncs.com:3306/agent_jsd_plus";
        System.out.println(AesUtil.encryptToBase64(dbUrl, "Cw5bM6x@6sH$2dlw^3JueH"));
        
        /*String dbUrl = "jdbc:mysql://192.168.112.31:3306/dsed_loan";
        String dbuser = "jsd_user";
        String dbpwd = "jsd_Password";
        System.out.println(AesUtil.encryptToBase64(dbUrl, "testC1b6x@6aH$2dlw"));
        System.out.println(AesUtil.encryptToBase64(dbuser, "testC1b6x@6aH$2dlw"));
        System.out.println(AesUtil.encryptToBase64(dbpwd, "testC1b6x@6aH$2dlw"));*/
        
        //online
        /*String online = decryptFromBase64("j1FIhYNyickGrgtmqKPeS1F7jfGVLd/SWlF10OaN4wK0R6GmKTbA6j7RqF+7x2fK", "Cw5bM6x@6sH$2dlw^3JueH");
        String online1 = decryptFromBase64("cJiZKo2M0HcKZdjgGmv/vQ==", "testC1b6x@6aH$2dlw");
        String online2 = decryptFromBase64("EjxHo0U8fOjuwprOOe5jjg==", "testC1b6x@6aH$2dlw");
        System.out.println(online);*/
    
    }
}
