package com.bigbank.mugloar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResultDto {

    private boolean shoppingSuccess;
    private int gold;
    private int lives;
    private int level;
    private int turn;
}
