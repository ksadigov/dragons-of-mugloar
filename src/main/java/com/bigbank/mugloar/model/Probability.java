package com.bigbank.mugloar.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Probability {
    PIECE_OF_CAKE("Piece of cake", 1),
    SURE_THING("Sure thing", 2),
    WALK_IN_THE_PARK("Walk in the park", 3),
    QUITE_LIKELY("Quite likely", 4),
    HMMM("Hmmm....", 5),
    GAMBLE("Gamble", 6),
    RATHER_DETRIMENTAL("Rather detrimental", 7),
    RISKY("Risky", 8),
    PLAYING_WITH_FIRE("Playing with fire", 9),
    SUICIDE_MISSION("Suicide mission", 10),
    IMPOSSIBLE("Impossible", 11),
    UNKNOWN("Unknown", 11);

    private final String message;
    private final int value;

    public static Probability from(String probabilityMessage) {
        return Arrays.stream(Probability.values())
                .filter(probability -> probability.message.equalsIgnoreCase(probabilityMessage))
                .findFirst()
                .orElse(Probability.UNKNOWN);
    }

}
