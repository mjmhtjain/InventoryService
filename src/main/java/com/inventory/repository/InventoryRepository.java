package com.inventory.repository;

import com.inventory.model.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InventoryRepository extends
        ReactiveCrudRepository<Inventory, Integer> {
}
