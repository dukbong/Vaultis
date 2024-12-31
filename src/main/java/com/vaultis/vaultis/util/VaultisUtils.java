package com.vaultis.vaultis.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class VaultisUtils {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    // Base64 인코딩된 데이터에서 불필요한 공백 문자나 줄 바꿈 문자를 제거하기 위해 사용
    public static String removeWhitespace(String input) {
        if (StringUtils.hasText(input)) {
            return WHITESPACE_PATTERN.matcher(input).replaceAll("");
        }
        throw new IllegalArgumentException("input must not be null or empty");
    }
    
}
