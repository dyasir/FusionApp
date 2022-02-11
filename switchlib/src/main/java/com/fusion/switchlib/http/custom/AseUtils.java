package com.fusion.switchlib.http.custom;

import android.util.Base64;

import com.fusion.switchlib.R;
import com.fusion.switchlib.SwitchApplication;
import com.orhanobut.logger.Logger;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AseUtils {

    private static final String encryptKey = SwitchApplication.getInstance().getString(R.string.switch_encrypt_key_part_1) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_key_part_2) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_key_part_3) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_key_part_end);
    private static final String encryptIV = SwitchApplication.getInstance().getString(R.string.switch_encrypt_vi_part_1) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_vi_part_2) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_vi_part_3) +
            SwitchApplication.getInstance().getString(R.string.switch_encrypt_vi_part_end);
    private static final String decryptKey = SwitchApplication.getInstance().getString(R.string.switch_decrypt_key_part_1) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_key_part_2) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_key_part_3) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_key_part_end);
    private static final String decryptIV = SwitchApplication.getInstance().getString(R.string.switch_decrypt_vi_part_1) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_vi_part_2) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_vi_part_3) +
            SwitchApplication.getInstance().getString(R.string.switch_decrypt_vi_part_end);

    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] AseEncrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(encryptIV.getBytes(StandardCharsets.UTF_8)));
            byte[] byteData = cipher.doFinal(data.getBytes());
            Logger.i(logEncrypt(data, new String(Base64.encode(byteData, Base64.NO_WRAP))));
            return Base64.encode(byteData, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.i(logEncrypt(data, data));
        return data.getBytes();
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
