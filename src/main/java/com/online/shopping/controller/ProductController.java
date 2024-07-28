package com.online.shopping.controller;

import com.online.shopping.entity.Product;
import com.online.shopping.entity.UserEntity;
import com.online.shopping.model.ProductModel;
import com.online.shopping.repository.UserRepository;
import com.online.shopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/online_shopping")
public class ProductController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    // User Home-Page
    @GetMapping("/home/page")
    public ModelAndView getAllProducts(Model model){
        UserEntity user = returnUser();
        model.addAttribute("user", user);
        List<Product> products = productService.getAllProducts();
        return new ModelAndView("homepage", "products", products);
    }

    // Get current logged in user
    public UserEntity returnUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String email = securityContext.getAuthentication().getName();
        UserEntity user = userRepository.findUserByEmail(email);
        if (user==null) {
            return UserEntity.builder().fullName("").build();
        }
        return user;
    }

    // Service provider information Page
    @GetMapping("/home/aboutUs")
    public String AboutUs() {
        return "aboutUs";
    }

    // Search product using keyword
    @GetMapping("/home/search")
    public ModelAndView getProductByFilter(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        getAllProducts(model);
        model.addAttribute("keyword", keyword);
        List<Product> products = productService.getProductByFilter(keyword);
        return new ModelAndView("homepage","products",products);
    }

    // Product administration Home-Page
    @GetMapping("/admin/home")
    public ModelAndView adminPage(Model model){
        UserEntity user = returnUser();
        model.addAttribute("user", user);
        List<Product> products = productService.getAllProducts();
        return new ModelAndView("adminpage", "products", products);
    }

    // To get the product registration page
    @GetMapping("/admin/registerproduct")
    public String registerProduct() {
        return "newproduct";
    }

    // Add a new product
    @PostMapping("/admin/addProduct")
    public String addProduct(@ModelAttribute ProductModel productModel) {
        productService.addProduct(productModel);
        return "redirect:/online_shopping/admin/home";
    }

    // Search admin product using keyword
    @GetMapping("admin/search")
    public ModelAndView getAdminProductByFilter(Model model, @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        List<Product> products = productService.getProductByFilter(keyword);
        model.addAttribute("keyword", keyword);
        adminPage(model);
        return new ModelAndView("adminpage","products",products);
    }

    // Get update page after submit update
    @GetMapping("/admin/updateProduct/{productID}")
    public ModelAndView updateProductPage(@PathVariable long productID) {
        Product product =  productService.getProductsById(productID);
        return new ModelAndView("updateproduct","orgProduct", product);
    }

    // Update an existing product
    @GetMapping("admin/update/{productID}")
    public String updateProduct(@PathVariable long productID, @ModelAttribute Product product) {
        productService.updateProduct(productID, product);
        return "redirect:/onlineshopping/admin/home";
    }

    // Delete a product
    @GetMapping("/admin/deleteProduct/{productID}")
    public ModelAndView deleteProduct(@PathVariable long productID) {
        productService.deleteProductById(productID);
        return new ModelAndView("redirect:/onlineshopping/admin/home");
    }

}
