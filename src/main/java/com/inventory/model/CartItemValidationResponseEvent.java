package com.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CartItemValidationResponseEvent {
    private int cartid;
    private int itemid;
    private int quantity;
    private boolean isValid;
}
