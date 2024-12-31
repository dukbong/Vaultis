package com.vaultis.vaultis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptedMessage {

    private String encryptedTitle;
    private String encryptedContent;
    private String encryptedAESKey;
    private String iv;

    // JWT에서 영감을 얻음
    public String toString() {
        return encryptedAESKey + "." + iv + "." + encryptedTitle + "." + encryptedContent;
    }

}
