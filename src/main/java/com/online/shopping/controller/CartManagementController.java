package com.online.shopping.controller;

import com.online.shopping.entity.*;
import com.online.shopping.model.Address;
import com.online.shopping.service.ProductService;
import com.online.shopping.service.WishlistCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("online_shopping")
public class CartManagementController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductController productController;

    @Autowired
    private WishlistCartService wishlistCartService;


    // Get user wishlist items
    @GetMapping("/user/wishlist")
    public ModelAndView userWishlist() {
        String currentUser=productController.returnUser().getFullName();
        List<Wishlist> products = wishlistCartService.getProductsByUser(currentUser);
        return new ModelAndView("wishlist","products",products);
    }

    // Add product to wishlist
    @GetMapping("/user/add_wishlist/{productId}")
    public String addWishlistBasedOnId(@PathVariable(name = "productId") long id) {
        Product product = productService.getProductsById(id);
        UserEntity currentUser = productController.returnUser();
        wishlistCartService.addProduct(product, currentUser);
        return "redirect:/online_shopping/home/page";
    }

    // To Remove item from wishlist
    @GetMapping("/user/delete/wishlist/{id}")
    public String deleteWishlistProduct(@PathVariable("id") long id) {
        wishlistCartService.deleteWishlistProduct(id);
        return "redirect:/online_shopping/user/wishlist";
    }

    // Get cart items
    @GetMapping("/user/cart")
    public ModelAndView userCartDetails(Model model) {
        String currentUser = productController.returnUser().getFullName();
        List<Cart> cart = wishlistCartService.getCartItemsByUser(currentUser);
        int price=0;
        List<Long>productIDList = new ArrayList<>();
        for (Cart product:cart) {
            try {
                int productPrice = Integer.parseInt(product.getPrice().substring(1).trim());
                price=price+productPrice;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
            productIDList.add(product.getProductID());
        }
        model.addAttribute("price",price);
        model.addAttribute("productId",productIDList);
        return new ModelAndView("usercart","cart",cart);
    }

    // To add product to cart
    @GetMapping("/user/cart/{id}")
    public String addToCart(@PathVariable("id") long id) {
        Product product = productService.getProductsById(id);
        UserEntity user = productController.returnUser();
        wishlistCartService.addToCart(product, user);
        return "redirect:/online_shopping/user/cart";
    }

    // Delete cart item
    @GetMapping("/user/delete/cart/{id}")
    public String deleteCartProduct(@PathVariable("id") long id) {
        wishlistCartService.deleteCartProduct(id);
        return "redirect:/online_shopping/user/cart";
    }

    // Buy product from cart
    @GetMapping("/user/checkout")
    public ModelAndView deliveryAddress(@RequestParam(name = "productId") List<Long> id, Model model) {
        model.addAttribute("productId", id);
        String currentUser = productController.returnUser().getFullName();
        List<DeliveryAddress> delivery =  wishlistCartService.getAddressByActualUser(currentUser);
        return new ModelAndView("payment","delivery",delivery);
    }

    // Add  delivery address
    @PostMapping("/user/addAddress")
    public String addDeliveryAddress(@ModelAttribute Address address, @RequestParam(name = "productId", required = false) List<Long> id) {
        String user = productController.returnUser().getFullName();
        wishlistCartService.addAddress(address,user);
        return "redirect:/online_shopping/user/checkout";
    }

    // Order summary and payment page
    @GetMapping("/user/payment")
    public ModelAndView getPaymentPage(@RequestParam(name = "productId", required = false) List<Long> productId, @RequestParam(name = "addressId", required = false) DeliveryAddress address, Model model) {
        List<Cart> order = wishlistCartService.getOrderedProducts(productId);
        List<Long> prodId = new ArrayList<>();
        for (Cart product : order) {
            prodId.add(product.getProductID());
        }
        Long addressId = address.getAddressId();
        model.addAttribute("productId", prodId);
        model.addAttribute("order", order);
        model.addAttribute("addressId", addressId);
        DeliveryAddress delivery =  wishlistCartService.getAddressById(addressId);
        return new ModelAndView("payment2","delivery",delivery);
    }

    // Confirm order
    @PostMapping("/user/placeorders")
    public String acceptOrders(@ModelAttribute DeliveryInformation information, ModelAndView model, @RequestParam(name = "productId", required = false) List<Long> productId) {
        wishlistCartService.acceptOrders(information, productId);
        return "redirect:/online_shopping/user/orders";
    }

    // User order-list
    @GetMapping("/user/orders")
    public ModelAndView userOrders() {
        List<DeliveryInformation> info = wishlistCartService.getOrderDetails();
        return new ModelAndView("myorders","info",info);
    }

}
