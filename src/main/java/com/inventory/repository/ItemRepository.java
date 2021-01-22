package com.inventory.repository;

import com.inventory.model.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ItemRepository extends
        ReactiveCrudRepository<Inventory, Integer> {
}
