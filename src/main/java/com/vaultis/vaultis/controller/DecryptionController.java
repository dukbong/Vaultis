package com.vaultis.vaultis.controller;

import com.vaultis.vaultis.dto.EncryptedMessage;
import com.vaultis.vaultis.dto.PlainMessage;
import com.vaultis.vaultis.service.DecryptionService;
import com.vaultis.vaultis.service.KeyFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.PrivateKey;

@RestController
@RequiredArgsConstructor
public class DecryptionController {

    private final DecryptionService decryptionService;
    private final KeyFactoryService keyFactoryService;

    @PostMapping("/decryption")
    public ResponseEntity<PlainMessage> decryptionData(@RequestPart("encryptedMessage") String encryptedMessage,
                                                       @RequestPart("privateKeyFile") MultipartFile privateKeyFile) {
        try {
            PrivateKey privateKey = keyFactoryService.extractPrivateKey(privateKeyFile);
            return ResponseEntity.ok().body(decryptionService.decryption(encryptedMessage, privateKey));
        } catch (Exception e) {
            throw new RuntimeException("데이터를 복호화하는데 실패하였습니다.", e);
        }
    }

}
