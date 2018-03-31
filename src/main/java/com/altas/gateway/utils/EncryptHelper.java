package com.altas.gateway.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 加密辅助类
 *
 * Created on 15-7-15.
 *
 */
public class EncryptHelper {

    /**
     * 目前只支持32位小端方式加密MD5，可以选择是否加入混淆串
     *
     * @param src
     * @param withSalt
     * @return
     */
    public static String encryptMD5(String src, boolean withSalt) {

        final char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                'd', 'e', 'f'};
        final String salt = "`128&90)(*^%$34!56@7#~";
        final String NULL_STR = "NULL";

        if(src == null || src.isEmpty()){
            src = NULL_STR;
        }
        String s = null;
        if(withSalt)
            src += salt;
        byte[] source = src.getBytes();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            // MD5的计算结果是一个128位的长整数
            byte tmp[] = md.digest();
            // 用字节表示就是 16 个字节
            // 每个字节用 16 进制表示的话，使用两个字符，所以表示成16进制需要32个字符
            char str[] = new char[16 * 2];
            //表示转换结果中对应的字符位置
            int k = 0;
            //从第一个字节开始，对MD5的每一个字节转换成16进制字符的转换
            for (int i = 0; i < 16; i++) {
                //取第i个字节
                byte byte0 = tmp[i];
                // 取字节中高4位的数字转换,'>>>'为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                // 取字节中低4位的数字转换
                str[k++] = hexDigits[byte0 & 0xf];
            }
            //换后的结果转换为字符串
            s = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // 加密
    public static String encryptBase64(String str) {

        byte[] bArray;
        String result = null;
        if (str != null) {
            try {
                bArray = Base64.getEncoder().encode(str.getBytes());
                result = new String(bArray, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解密
    public static String decryptBase64(String encrypted) {
        byte[] bArray;
        String result = null;
        if (encrypted != null) {
            try {
                bArray = Base64.getDecoder().decode(encrypted);
                result = new String(bArray, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static byte[] decryptBase64Bytes(String encrypted) {
        byte[] bArray = new byte[0];
        if (encrypted != null) {
            return Base64.getDecoder().decode(encrypted);
        }
        return bArray;
    }

    public static String encryptBytesBase64(byte[] bytes) {
        if (bytes != null) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return "";
    }
}
