package com.bigbank.mugloar.mapper;

import com.bigbank.mugloar.dto.MessageDto;
import com.bigbank.mugloar.mapper.qualifier.DecodeQualifier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {DecodeQualifier.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MessageMapper {
    public static final MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "rot13Decode")
    @Mapping(target = "messageText", source = "messageText", qualifiedByName = "rot13Decode")
    @Mapping(target = "probability", source = "probability", qualifiedByName = "rot13Decode")
    public abstract MessageDto decodedWithRot13(MessageDto source);

    @Mapping(target = "messageId", source = "messageId", qualifiedByName = "base64Decode")
    @Mapping(target = "messageText", source = "messageText", qualifiedByName = "base64Decode")
    @Mapping(target = "probability", source = "probability", qualifiedByName = "base64Decode")
    public abstract MessageDto decodedWithBase64(MessageDto source);


}
