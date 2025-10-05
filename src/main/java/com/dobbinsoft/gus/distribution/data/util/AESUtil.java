package com.dobbinsoft.gus.distribution.data.util;

import com.dobbinsoft.gus.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * 加密对象为JSON字符串，然后进行AES加密
     */
    public static String encrypt(Object obj, String key) {
        if (obj == null) {
            return null;
        }
        String jsonString = JsonUtil.convertToString(obj);
        return encrypt(jsonString, key);
    }

    /**
     * 解密AES加密的字符串，然后反序列化为对象
     */
    public static <T> T decrypt(String encryptedData, String key, Class<T> clazz) {
        if (encryptedData == null) {
            return null;
        }
        String jsonString = decrypt(encryptedData, key);
        return JsonUtil.convertValue(jsonString, clazz);
    }

    /**
     * AES加密字符串
     */
    public static String encrypt(String data, String key) {
        if (data == null) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密字符串
     */
    public static String decrypt(String encryptedData, String key) {
        if (encryptedData == null) {
            return null;
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败", e);
        }
    }
} 