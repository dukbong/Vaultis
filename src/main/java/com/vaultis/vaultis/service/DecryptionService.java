package com.vaultis.vaultis.service;

import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.exception.DecryptionException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.regex.Pattern;

import static com.vaultis.vaultis.util.VaultisUtils.removeWhitespace;

@Service
public class DecryptionService {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    public PlainMessage decryption(String encryptedMessage, PrivateKey privateKey) {
        try {
            String[] encryptedArr = encryptedMessage.split("\\.");
            if(encryptedArr.length != 3) {
                throw new DecryptionException("복호화를 위한 필수 데이터가 부족합니다.", new IllegalArgumentException());
            }
            SecretKey aesKey = decryptAESKey(encryptedArr[0], privateKey);
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(removeWhitespace(encryptedArr[1])));
            String decryptedContent = decryptWithAES(encryptedArr[2], aesKey, iv);
            return new PlainMessage(decryptedContent);
        } catch (Exception e) {
            throw new DecryptionException("복호화에 실패하였습니다.", e);
        }
    }

    private SecretKey decryptAESKey(String base64EncodedAESKey, PrivateKey rsaPrivateKey) {
        return decodeWithRSA(base64EncodedAESKey, rsaPrivateKey);
    }

    private SecretKey decodeWithRSA(String base64Data, PrivateKey privateKey) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(removeWhitespace(base64Data));
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new SecretKeySpec(decryptedData, AES_ALGORITHM);
        } catch (Exception e) {
            throw new DecryptionException("AES 키 복호화에 실패하였습니다.", e);
        }
    }

    private String decryptWithAES(String base64EncodedData, SecretKey aesKey, IvParameterSpec iv) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(removeWhitespace(base64EncodedData));
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, iv);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new DecryptionException("데이터 복호화에 실패하였습니다.", e);
        }
    }

}
