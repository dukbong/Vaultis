package com.vaultis.vaultis.controller;

import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.service.CryptoService;
import com.vaultis.vaultis.service.KeyFactoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CryptoControllerTest {

    @Autowired
    CryptoService cryptoService;
    @Autowired
    KeyFactoryService keyFactoryService;

    @Test
    void testEncryptDataWithRealPublicKeyFile() throws Exception {
        // 1. 테스트 데이터 준비
        PlainMessage request = new PlainMessage();
        request.setContent("Test Content");

        // 2. RSA 공개 키 생성 및 파일로 저장
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // RSA 키 길이
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("Public Key:");
        System.out.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));

        System.out.println("\nPrivate Key:");
        System.out.println(Base64.getEncoder().encodeToString(privateKey.getEncoded()));

        File publicKeyFile = File.createTempFile("publicKey", ".pub");
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile)) {
            fos.write(publicKey.getEncoded());
        }

        // 3. 공개 키 파일 읽기
        MultipartFile multipartFile = new MultipartFile() {
            @Override
            public String getName() {
                return publicKeyFile.getName();
            }

            @Override
            public String getOriginalFilename() {
                return publicKeyFile.getName();
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public boolean isEmpty() {
                return publicKeyFile.length() == 0;
            }

            @Override
            public long getSize() {
                return publicKeyFile.length();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return Files.readAllBytes(publicKeyFile.toPath());
            }

            @Override
            public InputStream getInputStream() throws FileNotFoundException {
                return new FileInputStream(publicKeyFile);
            }

            @Override
            public void transferTo(File dest) throws IOException {
                Files.copy(publicKeyFile.toPath(), dest.toPath());
            }
        };

        PublicKey extractedPublicKey = keyFactoryService.extractPublicKey(multipartFile);
        assertNotNull(extractedPublicKey, "Public key should not be null");

//        // 4. 데이터 암호화 (AES + RSA)
//        EncryptedMessage encryptedData = cryptoService.encrypt(request, extractedPublicKey);
//
//        // 5. 결과 출력 (디버깅)
//        System.out.println("Encrypted Title: " + encryptedData.getEncryptedTitle());
//        System.out.println("Encrypted Content: " + encryptedData.getEncryptedContent());
//        System.out.println("Encrypted AES Key: " + encryptedData.getEncryptedAESKey());
//
////        // 6. AES 키 복호화 및 내용 복호화
//        SecretKey decryptedAESKey = decryptAESKeyWithRSA(Base64.getDecoder().decode(encryptedData.getEncryptedAESKey()), privateKey);
////
//        String decryptedTitle = decryptWithAES(Base64.getDecoder().decode(encryptedData.getEncryptedTitle()), decryptedAESKey);
//        String decryptedContent = decryptWithAES(Base64.getDecoder().decode(encryptedData.getEncryptedContent()), decryptedAESKey);
////
//        System.out.println("Decrypted Title: " + decryptedTitle);
//        System.out.println("Decrypted Content: " + decryptedContent);
//
//        // 7. 복호화된 값 검증
//        assertEquals(request.getTitle(), decryptedTitle, "Decrypted title should match the original title");
//        assertEquals(request.getContent(), decryptedContent, "Decrypted content should match the original content");
        publicKeyFile.delete();
    }

    private SecretKey decryptAESKeyWithRSA(byte[] encryptedAESKey, PrivateKey rsaPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  // 명시적으로 패딩 방식 지정
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] decryptedAESKey = cipher.doFinal(encryptedAESKey);
        return new SecretKeySpec(decryptedAESKey, "AES");  // 복호화된 바이트 배열을 AES 키로 변환
    }

    private String decryptWithAES(byte[] encryptedData, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData);
    }
}