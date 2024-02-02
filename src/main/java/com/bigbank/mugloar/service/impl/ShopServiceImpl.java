package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.service.OptimizationService;
import com.bigbank.mugloar.service.ShopService;
import com.bigbank.mugloar.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final MugloarApiClient mugloarApiClient;
    private final StatisticsService statisticsService;
    private final OptimizationService optimizationService;

    @Override
    public void doShopping(GameStateDto gameStateDto) {
        var shopItems = getAllItems(gameStateDto.getGameId());
        var optimalItems = optimizationService.chooseOptimalItems(shopItems, gameStateDto);
        optimalItems.forEach(shopItemDto -> buyItem(gameStateDto, shopItemDto));
    }

    private List<ItemDto> getAllItems(String gameId) {
        return mugloarApiClient.getAllShopItems(gameId);
    }

    private PurchaseResultDto purchaseItem(String gameId, String itemId) {
        return mugloarApiClient.purchaseItem(gameId, itemId);
    }

    private void buyItem(GameStateDto gameStateDto, ItemDto shopItemDto) {
        var purchaseResultDto = purchaseItem(gameStateDto.getGameId(), shopItemDto.getId());
        if (purchaseResultDto.isShoppingSuccess()) {
            statisticsService.updateGameStateStatsAfterShopping(gameStateDto, purchaseResultDto);
        }
    }
}
