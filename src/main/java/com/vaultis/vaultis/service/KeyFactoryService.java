package com.vaultis.vaultis.service;

import static com.vaultis.vaultis.util.VaultisUtils.removeWhitespace;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KeyFactoryService {

    public PublicKey extractPublicKey(MultipartFile publicKeyFile) {
    	return (PublicKey)extractKey(publicKeyFile, "public");
    }

    public PrivateKey extractPrivateKey(MultipartFile privateKeyFile) {
        return (PrivateKey)extractKey(privateKeyFile, "private");
    }
    
    private Key extractKey(MultipartFile keyFile, String keyType) {
        try {
            String keyContent = new String(keyFile.getBytes(), StandardCharsets.UTF_8);
            keyContent = removeWhitespace(keyContent); // 공백을 제거
            byte[] keyBytes = Base64.getDecoder().decode(keyContent); // Base64로 디코딩

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            if ("public".equalsIgnoreCase(keyType)) {
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
                return keyFactory.generatePublic(keySpec); // PublicKey 반환
            } else if ("private".equalsIgnoreCase(keyType)) {
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                return keyFactory.generatePrivate(keySpec); // PrivateKey 반환
            } else {
                throw new IllegalArgumentException("지원되지 않는 키 타입입니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException(keyType + "키를 불러오는데 실패하였습니다.", e);
        }
    }

}