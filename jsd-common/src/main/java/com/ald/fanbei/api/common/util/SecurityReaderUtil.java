package com.ald.fanbei.api.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SecurityReaderUtil {

    public static String  getProperties(){
        try {
        	InputStream ins = SecurityReaderUtil.class.getClassLoader().getResourceAsStream("props/security.properties");
            InputStreamReader isr=new InputStreamReader (ins);
            BufferedReader br = new BufferedReader(isr);
            String text="";
            StringBuilder builder=new StringBuilder();
            while ((text=br.readLine())!=null) {
                builder.append(text).append(",");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
