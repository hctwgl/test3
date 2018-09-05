package com.ald.fanbei.api.common.util;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class SecurityReaderUtil {

    public static String  getProperties(){
        InputStream ins = ClassLoader.getSystemResourceAsStream("props/security.properties");
        try {
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
