package com.bigbank.mugloar.controller;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final MugloarApiClient mugloarApiClient;

    @GetMapping("/new/game")
    public ResponseEntity<GameStateDto> test() {
        GameStateDto gameStateDto = mugloarApiClient.startNewGame();
        return new ResponseEntity<>(gameStateDto, HttpStatus.OK);
    }
}
