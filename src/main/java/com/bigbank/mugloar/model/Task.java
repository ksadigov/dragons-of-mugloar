package com.bigbank.mugloar.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task {

    private String taskId;
    private String message;
    private int reward;
    private int expiresIn;
    private Integer encrypted;
    private String probability;
    private int evaluationScore;
}
