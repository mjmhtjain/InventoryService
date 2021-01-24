package com.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@AllArgsConstructor
public class CartItemValidationEvent {
    private int cartid;
    private int itemid;
    private int quantity;
}
