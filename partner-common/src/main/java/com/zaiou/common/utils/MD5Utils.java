package com.zaiou.common.utils;

import com.zaiou.common.enums.ResultInfo;
import com.zaiou.common.exception.BussinessException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: MD5
 * @auther: LB 2018/9/20 22:21
 * @modify: LB 2018/9/20 22:21
 */
@Slf4j
public class MD5Utils {
    private static final String HEX_NUMS_STR = "0123456789ABCDEF";
    private static final String HMAC_MD5_NAME = "HmacMD5";
    private static final int SOLT_LENGTH = 12;

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException caught!" + e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        byte[] byteArray = messageDigest.digest();
        return Hex.toString(byteArray);
    }

    public static String hmacMD5(String str, String solt) {
        byte[] soltBytes;
        if (null == solt) {
            soltBytes = new byte[] {-1};
        } else {
            soltBytes = solt.getBytes(Charset.forName("UTF-8"));
        }
        SecretKeySpec sk = new SecretKeySpec(soltBytes, HMAC_MD5_NAME);
        Mac mac;
        try {
            mac = Mac.getInstance(HMAC_MD5_NAME);
            mac.init(sk);
            byte[] encryBytes = mac.doFinal(str.getBytes(Charset.forName("UTF-8")));
            return byteToHexString(encryBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            throw new BussinessException(ResultInfo.NOT_NULL, HMAC_MD5_NAME);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
            throw new BussinessException(ResultInfo.NOT_NULL, "MD5Salt");
        }
    }

    public static String generateSalt() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWSYZ";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < SOLT_LENGTH; i++) {
            int rand = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(rand));
        }
        return sb.toString();
    }

    /**
     * 将16进制字符串转换成字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteToHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
//		String salt = "TUYBhDzTs027";
//		System.out.println(salt);
//		System.out.println(hmacMD5("12345678", salt));
//		System.out.println(hmacMD5("1234", salt));
        String s=MD5Utils.generateSalt();
        System.out.println(s);
        System.out.println(MD5Utils.hmacMD5("1q2w3e",s));
    }
}
