package com.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CartItemValidationResponseEvent {
    private int itemId;
    private int quantity;
    private boolean isValid;
}
