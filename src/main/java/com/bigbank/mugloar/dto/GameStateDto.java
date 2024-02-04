package com.bigbank.mugloar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStateDto {

    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
    private int turn;
}
