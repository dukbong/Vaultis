package com.vaultis.vaultis.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.vaultis.vaultis.util.VaultisUtils.removeWhitespace;

@Service
public class KeyFactoryService {

    public PublicKey extractPublicKey(MultipartFile publicKeyFile) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyFile.getBytes());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("공개키를 불러오는데 실패하였습니다.", e);
        }
    }

    public PrivateKey extractPrivateKey(MultipartFile privateKeyFile) {
        try {
            String keyContent = new String(privateKeyFile.getBytes(), StandardCharsets.UTF_8);
            keyContent = removeWhitespace(keyContent);
            byte[] keyBytes = Base64.getDecoder().decode(keyContent);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("비밀키를 불러오는데 실패하였습니다.", e);
        }
    }

}