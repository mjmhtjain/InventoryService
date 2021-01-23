package com.inventory.controller;

import com.google.gson.Gson;
import com.inventory.InventoryApplication;
import com.inventory.model.CartItemValidationEvent;
import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private InventoryRepository inventoryRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private Gson jsonConverter;

    public static Logger log = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    public InventoryController(InventoryRepository inventoryRepository, KafkaTemplate<String, String> kafkaTemplate, Gson jsonConverter) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.jsonConverter = jsonConverter;
    }

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

    @KafkaListener(topics = "validate_cart_topic")
    public void getFromKafka(String stringifiedEvent) {

        log.info(stringifiedEvent);

        CartItemValidationEvent cartItemValidationEvent = (CartItemValidationEvent)
                jsonConverter.fromJson(stringifiedEvent, CartItemValidationEvent.class);

        log.info(cartItemValidationEvent.toString());
    }

}
