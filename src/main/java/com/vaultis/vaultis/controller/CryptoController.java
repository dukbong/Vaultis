package com.vaultis.vaultis.controller;

import com.vaultis.vaultis.dto.EncryptedMessage;
import com.vaultis.vaultis.dto.SimpleResponse;
import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.exception.EncryptionException;
import com.vaultis.vaultis.service.CryptoService;
import com.vaultis.vaultis.service.KeyFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;
    private final KeyFactoryService keyFactoryService;

    @PostMapping("/encrypt")
    public ResponseEntity<SimpleResponse<EncryptedMessage>> encryptData(@RequestPart("content") String content,
                                                                        @RequestPart("publicKeyFile") MultipartFile publicKeyFile) {
        try {
            PublicKey publicKey = keyFactoryService.extractPublicKey(publicKeyFile);
            EncryptedMessage encryptedMessage = cryptoService.encrypt(new PlainMessage(content), publicKey);
            return ResponseEntity.ok().body(new SimpleResponse<>(encryptedMessage));
        } catch (Exception e) {
            throw new EncryptionException("데이터를 암호화하는데 실패하였습니다.", e);
        }
    }

}