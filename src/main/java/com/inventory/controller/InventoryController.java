package com.inventory.controller;

import com.google.gson.Gson;
import com.inventory.model.CartItemValidationEvent;
import com.inventory.model.CartItemValidationResponseEvent;
import com.inventory.model.Inventory;
import com.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private Inventory dummyInv = new Inventory(-1, "", -1);

    private final String validateCartTopic = "validate_cart_topic";
    private final String validateCartResponseTopic = "validate_cart_response_topic";
    private final String validateCartResponse_key = "validate_cart_response";

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

    @KafkaListener(topics = validateCartTopic)
    public void getFromKafka(String stringifiedEvent) {

        log.info(stringifiedEvent);

        CartItemValidationEvent cartItemValidationEvent = (CartItemValidationEvent)
                jsonConverter.fromJson(stringifiedEvent, CartItemValidationEvent.class);

        Mono<CartItemValidationResponseEvent> response = validateCartItem(cartItemValidationEvent);
        sendCartValidationResponseEvent(response);
    }

    private void sendCartValidationResponseEvent(Mono<CartItemValidationResponseEvent> response) {
        response
//                .log()
                .flatMap (event -> {
                    CompletableFuture<SendResult<String, String>> res = kafkaTemplate
                            .send(validateCartResponseTopic, validateCartResponse_key, jsonConverter.toJson(event))
                            .completable();

                    return Mono.fromFuture(res);
                })
                .doOnNext(result -> {
                    String val = result.getProducerRecord().value();
                    String key = result.getProducerRecord().key();
                    String topic = result.getProducerRecord().topic();
                    Integer partition = result.getProducerRecord().partition();

                    log.info("response given {}, {}, {}, {}", key, val, topic, partition);
                })
                .subscribe();
    }

    private Mono<CartItemValidationResponseEvent> validateCartItem(CartItemValidationEvent cartItemValidationEvent) {
        return
                inventoryRepository
                        .findById(cartItemValidationEvent.getItemId())
                        .switchIfEmpty(Mono.just(dummyInv))
                        .map(inv -> inv.getQuantity())
                        .flatMap(invQuantity -> {
                            return invQuantity >= cartItemValidationEvent.getQuantity() ?
                                    Mono.just(true) : Mono.just(false);
                        })
                        .map(isValid -> new CartItemValidationResponseEvent(
                                cartItemValidationEvent.getItemId(),
                                cartItemValidationEvent.getQuantity(),
                                isValid));
    }

}
