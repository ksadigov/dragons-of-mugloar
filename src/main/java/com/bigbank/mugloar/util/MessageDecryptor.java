package com.bigbank.mugloar.util;

import com.bigbank.mugloar.dto.MessageDto;
import com.bigbank.mugloar.mapper.MessageMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageDecryptor {

    public static List<MessageDto> decryptMessages(List<MessageDto> messageDtos) {
        return messageDtos.stream()
                .map(messageDto -> {
                    decryptMessageIfNeeded(messageDto);
                    return messageDto;
                })
                .toList();
    }

    private static void decryptMessageIfNeeded(MessageDto messageDto) {
        if (messageDto.getEncrypted() == null) return;

        switch (messageDto.getEncrypted()) {
            case 1 -> MessageMapper.INSTANCE.decodedWithBase64(messageDto, messageDto);
            case 2 -> MessageMapper.INSTANCE.decodedWithRot13(messageDto, messageDto);
        }
    }
}
