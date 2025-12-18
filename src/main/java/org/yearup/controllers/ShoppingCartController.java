package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.*;

import java.security.Principal;

@RestController
@RequestMapping("/cart")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return null;
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @PostMapping("/products/{productId}")
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            return shoppingCartDao.addItem(userId,productId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCart updateCart(Principal principal, @PathVariable int productId, @RequestBody ShoppingCartItem item) {

        {
            try
            {
                String userName = principal.getName();
                User user = userDao.getByUserName(userName);
                int userId = user.getId();

                ShoppingCart cart = shoppingCartDao.getByUserId(userId);

                if (!cart.contains(productId))
                {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not in cart");
                }

                shoppingCartDao.updateItem(userId, productId, item.getQuantity());

                return shoppingCartDao.getByUserId(userId);
            }
            catch(Exception e)
            {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
            }
        }
    }

    @DeleteMapping
    public void deleteCart(Principal principal) {

        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            shoppingCartDao.clearCart(userId);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
