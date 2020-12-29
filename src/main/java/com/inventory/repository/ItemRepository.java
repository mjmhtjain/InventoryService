package com.inventory.repository;

import com.inventory.model.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {
}
