package server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShoppingListController {

    @GetMapping("/{id}")
    public String getShoppingList(@PathVariable Long id) {
        return "Requested shopping list with ID: " + id;
    }
}
