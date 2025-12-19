package org.yearup.data;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Category;
import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here

    ShoppingCart addItem(int userId, int productId);

    ShoppingCart updateItem(int userId, int productId, int quantity);

    ShoppingCart clearCart(int userId);

    ShoppingCart deleteItem(int userId);
}
