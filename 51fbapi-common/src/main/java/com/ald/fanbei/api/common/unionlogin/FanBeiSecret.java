package com.ald.fanbei.api.common.unionlogin;

import org.apache.commons.codec.binary.Base64;

import static java.lang.System.out;

public class FanBeiSecret {

    public static  void main(String[] args){
        String secret_key="1CA4EDA857AFCC23";
        String phone="15990182307";
        String channel="QwEr";
        String applyInfoStr = "test";
        String userAttrStr = "spring";
        byte[] bb= Codec.encode(secret_key,phone);
       String base64Str= Base64.encodeBase64String(bb);
        String enApplyInfo =  Codec.strEncodBase64(secret_key, applyInfoStr);
        String enUserAttr =  Codec.strEncodBase64(secret_key, userAttrStr);
        System.out.println("encodedStr = " + enApplyInfo);
        System.out.println("encodedStr = " + enUserAttr);
        System.out.println("decodedStr = " + new String(Codec.base64StrDecode(secret_key,enApplyInfo)));
        System.out.println("decodedStr = " + new String(Codec.base64StrDecode(secret_key,base64Str)));

    }
}
