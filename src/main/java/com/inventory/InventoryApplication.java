package com.inventory;

import com.inventory.model.Item;
import com.inventory.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ReactiveMongoOperations operations, ItemRepository itemRepository) {
        return args -> {
            Flux<Item> inserts = Flux.just(
                    new Item("12", "Item1", 12),
                    new Item("13", "Item2", 13),
                    new Item("14", "Item3", 14),
                    new Item("15", "Item4", 15)
            )
                    .flatMap(item -> itemRepository.save(item));

            operations.collectionExists(Item.class)
                    .map(exists -> exists ? operations.dropCollection(Item.class) : Mono.empty())
                    .thenMany(operations.createCollection(Item.class))
                    .thenMany(inserts)
                    .subscribe(System.out::println);

        };
    }
}
