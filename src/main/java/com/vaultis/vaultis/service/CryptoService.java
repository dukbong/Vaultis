package com.vaultis.vaultis.service;

import com.vaultis.vaultis.dto.EncryptedMessage;
import com.vaultis.vaultis.dto.EncryptedMessageStruct;
import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.exception.EncryptionException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    public EncryptedMessage encrypt(PlainMessage plainMessage, PublicKey publicKey) {
        try {
            SecretKey aesKey = generateAESKey();
            IvParameterSpec iv = generateRandomIV();

            String encryptedContent = encryptWithAES(plainMessage.getContent(), aesKey, iv);
            String encryptedAESKey = encryptAESKey(aesKey, publicKey);
            String ivBase64 = Base64.getEncoder().encodeToString(iv.getIV());
            EncryptedMessageStruct encryptedMessage = new EncryptedMessageStruct(encryptedContent, encryptedAESKey, ivBase64);
            return new EncryptedMessage(encryptedMessage.toString());
        } catch (Exception e) {
            throw new EncryptionException("암호화 작업이 실패하였습니다.", e);
        }
    }

    private SecretKey generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(AES_KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new EncryptionException("AES 키를 생성하는데 실패하였습니다.", e);
        }
    }

    // AES의 약점을 보완
    private IvParameterSpec generateRandomIV() {
        try {
            byte[] iv = new byte[IV_SIZE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            return new IvParameterSpec(iv);
        } catch (Exception e) {
            throw new EncryptionException("IV를 생성하는데 실패하였습니다.", e);
        }
    }

    private String encryptWithAES(String data, SecretKey aesKey, IvParameterSpec iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new EncryptionException("데이터를 암호화하는데 실패하였습니다.", e);
        }
    }

    private String encryptAESKey(SecretKey aesKey, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedAESKey = cipher.doFinal(aesKey.getEncoded());
            return Base64.getEncoder().encodeToString(encryptedAESKey);
        } catch (Exception e) {
            throw new EncryptionException("AES 키를 암호화하는데 실패하였습니다.", e);
        }
    }
}
