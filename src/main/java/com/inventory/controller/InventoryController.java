package com.inventory.controller;

import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/find")
    public Flux<Inventory> fetchAll() {
        Flux<Inventory> response = inventoryRepository.findAll();
        return response;
    }

    @GetMapping("/validate/{itemId}/{orderedQuantity}")
    public Mono<Boolean> validate(@PathVariable int itemId, @PathVariable int orderedQuantity) {
        Inventory dummyInv = new Inventory(-1, "", -1);

        Mono<Boolean> valid = inventoryRepository
                .findById(itemId)
                .switchIfEmpty(Mono.just(dummyInv))
                .map(inv -> inv.getQuantity())
                .flatMap(invQuantity -> {
                    if (invQuantity >= orderedQuantity) {
                        return Mono.just(true);
                    } else {
                        return Mono.just(false);
                    }
                });

        return valid;
    }

}
