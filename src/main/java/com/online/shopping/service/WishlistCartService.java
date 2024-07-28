package com.online.shopping.service;

import com.online.shopping.entity.*;
import com.online.shopping.model.Address;

import java.util.List;

public interface WishlistCartService {

    List<Wishlist> getProductsByUser(String user);

    void addProduct(Product product, UserEntity currentUser);

    void deleteWishlistProduct(long id);

    List<Cart> getCartItemsByUser(String user);

    void addToCart(Product product, UserEntity user);

    void deleteCartProduct(long id);

    List<DeliveryAddress> getAddressByActualUser(String user);

    void addAddress(Address address, String user);

    void acceptOrders(DeliveryInformation information, List<Long> productId);

    DeliveryAddress getAddressById(long id);

    List<Cart> getOrderedProducts(List<Long> id);

    List<DeliveryInformation> getOrderDetails();

}
