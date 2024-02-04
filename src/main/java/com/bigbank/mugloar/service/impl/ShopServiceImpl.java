package com.bigbank.mugloar.service.impl;

import com.bigbank.mugloar.client.MugloarApiClient;
import com.bigbank.mugloar.dto.GameStateDto;
import com.bigbank.mugloar.dto.ItemDto;
import com.bigbank.mugloar.dto.PurchaseResultDto;
import com.bigbank.mugloar.mapper.GameStateMapper;
import com.bigbank.mugloar.service.OptimizationService;
import com.bigbank.mugloar.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {
    private final MugloarApiClient mugloarApiClient;
    private final OptimizationService optimizationService;

    @Override
    public GameStateDto doShopping(GameStateDto initialGameStateDto) {
        List<ItemDto> shopItems = getAllItems(initialGameStateDto.getGameId());
        List<ItemDto> optimalItems = optimizationService.getOptimalItems(shopItems, initialGameStateDto);

        return optimalItems.stream()
                .reduce(initialGameStateDto, this::attemptToPurchaseItem,
                        (state1, state2) -> state2);
    }

    private List<ItemDto> getAllItems(String gameId) {
        return mugloarApiClient.getAllShopItems(gameId);
    }

    private PurchaseResultDto purchaseItem(String gameId, String itemId) {
        return mugloarApiClient.purchaseItem(gameId, itemId);
    }

    private GameStateDto attemptToPurchaseItem(GameStateDto gameStateDto, ItemDto shopItemDto) {
        PurchaseResultDto purchaseResultDto = purchaseItem(gameStateDto.getGameId(), shopItemDto.getId());
        if (purchaseResultDto.isShoppingSuccess()) {
            return GameStateMapper.INSTANCE.toGameStateDto(gameStateDto, purchaseResultDto);
        }
        return gameStateDto;
    }
}
