package com.shortvideo.lib.common.http;

import android.content.Context;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.shortvideo.lib.R;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AseUtils {

    private static String encryptKey;
    private static String encryptIV;
    private static String decryptKey;
    private static String decryptIV;
    public static final String AES_KAI = "uyxzV+p+x43WA7ZULKfOTg==";
    public static final String AES_GUAN = "5PqVsJuPM0/a3CifYUQAJw==";

    public static void init(Context context) {
        encryptKey = context.getString(R.string.switch_encrypt_key_part_1) +
                context.getString(R.string.switch_encrypt_key_part_2) +
                context.getString(R.string.switch_encrypt_key_part_3) +
                context.getString(R.string.switch_encrypt_key_part_end);
        encryptIV = context.getString(R.string.switch_encrypt_vi_part_1) +
                context.getString(R.string.switch_encrypt_vi_part_2) +
                context.getString(R.string.switch_encrypt_vi_part_3) +
                context.getString(R.string.switch_encrypt_vi_part_end);
        decryptKey = context.getString(R.string.switch_decrypt_key_part_1) +
                context.getString(R.string.switch_decrypt_key_part_2) +
                context.getString(R.string.switch_decrypt_key_part_3) +
                context.getString(R.string.switch_decrypt_key_part_end);
        decryptIV = context.getString(R.string.switch_decrypt_vi_part_1) +
                context.getString(R.string.switch_decrypt_vi_part_2) +
                context.getString(R.string.switch_decrypt_vi_part_3) +
                context.getString(R.string.switch_decrypt_vi_part_end);
    }

    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String AseEncrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(encryptIV.getBytes(StandardCharsets.UTF_8)));
            byte[] byteData = cipher.doFinal(data.getBytes());
            Logger.i(logEncrypt(data, URLEncoder.encode(new String(Base64.encode(byteData, Base64.NO_WRAP)), "UTF-8")));
            return URLEncoder.encode(new String(Base64.encode(byteData, Base64.NO_WRAP)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.i(logEncrypt(data, data));
        return data;
    }

    /**
     * 解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String AseDecrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(decryptKey.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(decryptIV.getBytes(StandardCharsets.UTF_8)));
            byte[] byteData = Base64.decode(data, Base64.NO_WRAP);
            return new String(cipher.doFinal(byteData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String logEncrypt(String data, String after) {
        return "-----加密开始-----\n-----原数据: " + data + "-----\n-----加密后: " + after + "-----\n-----加密结束-----";
    }
}
