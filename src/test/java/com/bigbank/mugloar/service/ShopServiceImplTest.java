package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.service.OptimizationService;
import com.bigbank.mugloar.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock
    private MugloarApiClient mugloarApiClient;
    @Mock
    private StatisticsService statisticsService;
    @Mock
    private OptimizationService optimizationService;

    @InjectMocks
    private ShopServiceImpl testObj;

    @Test
    void doShopping_ShouldPurchaseOptimalItems() {
        String gameId = "gameId";
        GameStateDto gameStateDto = new GameStateDto();
        gameStateDto.setGameId(gameId);

        List<ItemDto> shopItems = List.of(
                new ItemDto("hpot", "Healing Potion", 50),
                new ItemDto("cs", "Claw Sharpening", 100));
        when(mugloarApiClient.getAllShopItems(gameId)).thenReturn(shopItems);

        List<ItemDto> optimalItems = List.of(shopItems.get(1));
        when(optimizationService.getOptimalItems(shopItems, gameStateDto)).thenReturn(optimalItems);

        PurchaseResultDto purchaseResultDto = new PurchaseResultDto(true, 1, 3, 1, 1);
        when(mugloarApiClient.purchaseItem(anyString(), anyString())).thenReturn(purchaseResultDto);

        testObj.doShopping(gameStateDto);

        verify(mugloarApiClient).getAllShopItems(gameId);
        verify(optimizationService).getOptimalItems(shopItems, gameStateDto);
        verify(mugloarApiClient).purchaseItem(gameId, "cs");
    }
}
