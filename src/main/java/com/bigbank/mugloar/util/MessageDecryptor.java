package com.bigbank.mugloar.util;

import com.bigbank.mugloar.dto.MessageDto;
import com.bigbank.mugloar.mapper.MessageMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageDecryptor {
    public static List<MessageDto> decryptMessages(List<MessageDto> messageDtos) {
        return messageDtos.stream()
                .map(MessageDecryptor::decryptMessageIfNeeded)
                .toList();
    }

    private static MessageDto decryptMessageIfNeeded(MessageDto messageDto) {
        return Optional.ofNullable(messageDto.getEncrypted())
                .map(encrypted -> switch (encrypted) {
                    case 1 -> MessageMapper.INSTANCE.decodedWithBase64(messageDto);
                    case 2 -> MessageMapper.INSTANCE.decodedWithRot13(messageDto);
                    default -> messageDto;
                }).orElse(messageDto);
    }
}
