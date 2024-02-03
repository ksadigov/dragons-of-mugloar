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
                .map(MessageDecryptor::decryptMessageIfNeeded)
                .toList();
    }

    private static MessageDto decryptMessageIfNeeded(MessageDto messageDto) {
        if (messageDto.getEncrypted() == null) return messageDto;

        MessageDto decryptedMessageDto = new MessageDto();

        switch (messageDto.getEncrypted()) {
            case 1 -> MessageMapper.INSTANCE.decodedWithBase64(decryptedMessageDto, messageDto);
            case 2 -> MessageMapper.INSTANCE.decodedWithRot13(decryptedMessageDto, messageDto);
        }
        return decryptedMessageDto;
    }
}
