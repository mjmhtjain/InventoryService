package com.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("inventory")
public class Inventory {

    @Id
    @Column("id")
    private Long id;

    @Column("item")
    private String item;

    @Column("quantity")
    private int quantity;

    public Inventory(String item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }
}
