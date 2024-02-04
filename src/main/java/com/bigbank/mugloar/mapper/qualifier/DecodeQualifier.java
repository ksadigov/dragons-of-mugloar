package com.bigbank.mugloar.mapper.qualifier;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class DecodeQualifier {
    @Named("rot13Decode")
    public String rot13Decode(String encryptedText) {
        StringBuilder sb = new StringBuilder(encryptedText.length());
        for (char c : encryptedText.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                char base = (c >= 'a') ? 'a' : 'A';
                c = (char) (((c - base + 13) % 26) + base);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Named("base64Decode")
    public String base64Decode(String encryptedText) {
        return new String(Base64.getDecoder().decode(encryptedText));
    }

}
