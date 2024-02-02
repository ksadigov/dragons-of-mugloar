package com.bigbank.mugloar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @JsonProperty("adId")
    private String messageId;
    @JsonProperty("message")
    private String messageText;
    private int reward;
    private int expiresIn;
    private Integer encrypted;
    private String probability;
}
