package com.inventory.repository;

import com.inventory.model.Inventory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InventoryRepository extends
        ReactiveCrudRepository<Inventory, Integer> {

    @Query("TRUNCATE TABLE inventory RESTART IDENTITY;")
    Mono<Void> truncate();
}
