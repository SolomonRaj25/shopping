package com.online.shopping.service;

import com.online.shopping.entity.*;
import com.online.shopping.model.Address;
import com.online.shopping.repository.CartRepository;
import com.online.shopping.repository.CheckOutRepository;
import com.online.shopping.repository.OrderRepository;
import com.online.shopping.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistCartServiceImpl implements WishlistCartService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CheckOutRepository checkOutRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public DeliveryAddress getAddressById(long id) {
        return checkOutRepository.findById(id).get();
    }

    @Override
    public List<Cart> getOrderedProducts(List<Long> id) {
        List<Cart> cart = cartRepository.findAllById(id);
        return cart;
    }

    @Override
    public void acceptOrders(DeliveryInformation information, List<Long> productId) {

        for (Long id : productId) {
            Cart product = cartRepository.findById(id).get();
            DeliveryInformation orderSummary = DeliveryInformation.builder()
                    .fullName(information.getFullName())
                    .contact(information.getContact())
                    .deliveryTo(information.getDeliveryTo())
                    .product(product.getProductName())
                    .productId(id)
                    .paymentType(information.getPaymentType())
                    .paidAmount(product.getPrice())
                    .orderedDate(String.valueOf(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                    .expectedDelivery(String.valueOf(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))))
                    .build();
            orderRepository.save(orderSummary);
            cartRepository.deleteById(id);
        }
    }

    @Override
    public List<DeliveryInformation> getOrderDetails() {
        return orderRepository.findAll();
    }

    @Override
    public List<Wishlist> getProductsByUser(String user) {
        List<Wishlist> products = wishlistRepository.findByUserName(user);
        if (products.isEmpty()) {
            return new ArrayList<>();
        }
        return products;
    }

    @Override
    public void addProduct(Product product, UserEntity currentUser) {
        Wishlist newProduct = Wishlist.builder().productID(product.getProductID())
                .productName(product.getProductName()).price(product.getPrice())
                .colour(product.getColour()).size(product.getSize())
                .productType(product.getProductType()).genderSelection(product.getGenderSelection())
                .brand(product.getBrand()).imageFile(product.getImageFile()).userName(currentUser.getFullName()).build();
        wishlistRepository.save(newProduct);
    }

    @Override
    public void deleteWishlistProduct(long id) {
        wishlistRepository.deleteById(id);
    }

    @Override
    public List<Cart> getCartItemsByUser(String user) {
        return cartRepository.findByUserName(user);
    }

    @Override
    public void addToCart(Product product, UserEntity user) {
        Cart cart = Cart.builder().productID(product.getProductID())
                .productName(product.getProductName()).price(product.getPrice())
                .colour(product.getColour()).size(product.getSize())
                .brand(product.getBrand()).imageFile(product.getImageFile()).userName(user.getFullName()).build();
        cartRepository.save(cart);
    }
    @Override
    public void deleteCartProduct(long id) {
        cartRepository.deleteById(id);
    }


    @Override
    public List<DeliveryAddress> getAddressByActualUser(String user) {
        return checkOutRepository.findByActualUser(user);
    }

    @Override
    public void addAddress(Address address, String user) {
        DeliveryAddress delivery = DeliveryAddress.builder()
                .fullName(address.getFullName())
                .actualUser(user)
                .phoneNo(address.getPhoneNo())
                .address(address.getBuildingNo()+", "+address.getStreetAreaName()+", "+
                        address.getState()+", "+address.getCity()+"-"+address.getPinCode())
                .build();
        checkOutRepository.save(delivery);
    }

}
