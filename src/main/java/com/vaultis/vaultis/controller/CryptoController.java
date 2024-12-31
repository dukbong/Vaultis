package com.vaultis.vaultis.controller;

import com.vaultis.vaultis.dto.EncryptedMessage;
import com.vaultis.vaultis.dto.EncryptedResponse;
import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.exception.EncryptionException;
import com.vaultis.vaultis.service.CryptoService;
import com.vaultis.vaultis.service.KeyFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.PublicKey;

@RestController
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;
    private final KeyFactoryService keyFactoryService;

    @PostMapping("/encrypt")
    public ResponseEntity<EncryptedResponse<String>> encryptData(@RequestPart("title") String title,
                                                                 @RequestPart("content") String content,
                                                                 @RequestPart("publicKeyFile") MultipartFile publicKeyFile) {
        try {
            PublicKey publicKey = keyFactoryService.extractPublicKey(publicKeyFile);
            return ResponseEntity.ok().body(cryptoService.encrypt(new PlainMessage(title, content), publicKey));
        } catch (Exception e) {
            throw new EncryptionException("데이터를 암호화하는데 실패하였습니다.", e);
        }
    }

}