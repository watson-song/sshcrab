package com.watsontech.tools.sshcrab2;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {

    public static String encode(String str) {
        if (str==null) return "";

        String base64Str="";
        try {
            byte[] base64Byte = Base64.getEncoder().encode(str.getBytes("utf-8"));
            base64Str = new String(base64Byte);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return base64Str;
    }

    public static String decode(String base64Str) {
        //Base64解密
        String base64StrBack = "";
        try {
            byte[] base64ByteBack = Base64.getDecoder().decode(base64Str.getBytes("utf-8"));
            base64StrBack = new String(base64ByteBack);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return base64StrBack;
    }
}