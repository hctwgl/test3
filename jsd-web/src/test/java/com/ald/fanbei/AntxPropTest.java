package com.ald.fanbei;

import com.ald.fanbei.api.common.util.AesUtil;

public class AntxPropTest {
	public static void main(String[] args) {

    	// dev
        String decryptStr = AesUtil.decryptFromBase64("OZAtwQcAyH5+BVcIbdZW6euzZCntyUxwH743Udhr9+LU+d0IhGa6JAh4bXHXkgeqEAD/gApVuKDhPmIPoVSEBQ==", "testC1b6x@6aH$2dlw");
        System.out.println(decryptStr);
       
        String dbUrl = "jdbc:mysql://192.168.106.31:3306/jsd_loan";
        String dbuser = "jsd_user";
        String dbpwd = "jsd_Password";
        System.out.println(AesUtil.encryptToBase64(dbUrl, "testC1b6x@6aH$2dlw"));
        System.out.println(AesUtil.encryptToBase64(dbuser, "testC1b6x@6aH$2dlw"));
        System.out.println(AesUtil.encryptToBase64(dbpwd, "testC1b6x@6aH$2dlw"));
        
        //online
        /*String online = decryptFromBase64("j1FIhYNyickGrgtmqKPeS1F7jfGVLd/SWlF10OaN4wK0R6GmKTbA6j7RqF+7x2fK", "Cw5bM6x@6sH$2dlw^3JueH");
        String online1 = decryptFromBase64("cJiZKo2M0HcKZdjgGmv/vQ==", "testC1b6x@6aH$2dlw");
        String online2 = decryptFromBase64("EjxHo0U8fOjuwprOOe5jjg==", "testC1b6x@6aH$2dlw");
        System.out.println(online);*/
    
    }
}
