package com.hotcast.vr.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;


/**
 * Created by liurongzhi on 2016/1/27.
 */
public class Md5Utils {
    public static String getMd5(String value) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance(StringInfo.md5).digest(value.getBytes(StringInfo.utf));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
