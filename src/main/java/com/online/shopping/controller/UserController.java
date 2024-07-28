package com.online.shopping.controller;

import com.online.shopping.assist.Message;
import com.online.shopping.entity.UserEntity;
import com.online.shopping.entity.VerificationToken;
import com.online.shopping.event.RegistrationEvent;
import com.online.shopping.event.listener.RegistrationEventListener;
import com.online.shopping.exception.customUserNotFoundException;
import com.online.shopping.model.PasswordModel;
import com.online.shopping.model.UserModel;
import com.online.shopping.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/online_shopping/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private RegistrationEventListener eventListener;

    // Registration Page
    @GetMapping("/registerhere")
    public String registrationPage() {
        return "registration";
    }

    // Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Forgot Password request Page
    @GetMapping("/forgotpassword")
    public String forgotPasswordPage() {
        return "passwordresetrequest";
    }

    // Password change Page
    @GetMapping("/changepassword")
    public String passwordChangePage(Model model) throws customUserNotFoundException {
        try {
            UserEntity user = returnUser();
            model.addAttribute("user", user.getEmail());
        } catch (Exception e) {
            throw new customUserNotFoundException("User not found");
        }
        return "passwordchange";
    }

    public UserEntity returnUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String email = securityContext.getAuthentication().getName();
        Optional<UserEntity> user = userService.findUserByEmail(email);
        if (user.isEmpty()) {
            return UserEntity.builder().fullName("").build();
        }
        return user.get();
    }

    // user update page
    @GetMapping("/updateaccount")
    public String getUserUpdatePage(@RequestParam(name = "email", required = false) String email) {
        return "updateuseraccount";
    }

    // User information update page
    @GetMapping("/update/{email}")
    public String getUserUpdatePage(@PathVariable(name = "email", required = false) String email, Model model) throws customUserNotFoundException {
        Optional<UserEntity> hasUser = userService.findUserByEmail(email);
        if (hasUser.isEmpty()) {
            throw new customUserNotFoundException("User not found");
        }
        UserEntity user = hasUser.get();
        model.addAttribute("user", user);
        return "updateuseraccount";
    }

    // Page for delete user account
    @GetMapping("/deactivate/{email}")
    public String getDeletionPage(@PathVariable(name = "email", required = false) String email, Model model) throws customUserNotFoundException {
        Optional<UserEntity> hasUser = userService.findUserByEmail(email);
        if (hasUser.isEmpty()) {
            throw new customUserNotFoundException("User not found");
        }
        UserEntity user = hasUser.get();
        model.addAttribute("user", user);
        return "deleteaccount";
    }

    // Register user and send verification email to user with link
    @PostMapping("/addUser")
    public String RegisterAndSendVerificationTokenToUser(@ModelAttribute @Valid UserModel userModel, final HttpServletRequest request, HttpSession session) {
        UserEntity user = userService.addUser(userModel);
        eventPublisher.publishEvent(new RegistrationEvent(user,applicationUrl(request)));
        session.setAttribute("message", new Message("Verification email sent! Please check registered email address"));
        return "redirect:/online_shopping/user/registerhere";
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+
                request.getServerPort()+request.getContextPath();
    }

    // Verifying user with link sent in email
    @GetMapping("/verify_registrationToken")
    public String validateVerificationToken(@RequestParam("token") String token, HttpSession session, HttpServletRequest request) {
        String status = userService.verifyToken(token);
        switch (status) {
            case "Invalid Token" :
                session.setAttribute("message", new Message(status, "http://"+request.getServerName()+":"+
                        request.getServerPort()+"/online_shopping"+"/user/login"));
                return "verification";
            case "Token Expired" :
                status="Token Expired!"+"click the link to resend verification token";
                session.setAttribute("message", new Message(status, "http://"+request.getServerName()+":"+
                        request.getServerPort()+"/online_shopping/user/resendVerificationToken?token="+token));
                return "verification";
        }
        session.setAttribute("message", new Message(status, "http://"+request.getServerName()+":"+
                request.getServerPort()+"/online_shopping"+"/user/login"));
        return "verification";
    }

    // Resend verification link to user
    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam String token, final HttpServletRequest request, HttpSession session) {
        UserEntity user = userService.resendVerificationToken(token);
        eventPublisher.publishEvent(new RegistrationEvent(user, applicationUrl(request)));
        session.setAttribute("message", new Message("Resent verification link to your registered email address"+"\n"+"Go back to login Page", "http://"+request.getServerName()+":"+
                request.getServerPort()+"/online_shopping"+"/user/login"));
        return "verification";
    }

    // Requesting for password reset with email and send reset link
    @PostMapping("/forgotpassword")
    public String forgotPassword(@ModelAttribute PasswordModel passwordModel, HttpServletRequest request) {
        Optional<UserEntity> user = userService.findUserByEmail(passwordModel.getEmail());
        if (user.isEmpty()) {
            return "redirect:/online_shopping/user/forgotpassword?invalid";
        }
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken =  userService.createPasswordResetToken(token, user);
        String url = applicationUrl(request)+"/online_shopping/user/resetpassword?token="+verificationToken.getToken();
        String emailBody = "Hi "+user.get().getFullName()+",\n\n"+
                "Please click the below link to reset your password"+"\n\n"+
                url+"\n\n"+"Thank You!";
        eventListener.sendEmailToUser(user.get().getEmail(), "Password Reset Email",
                emailBody);
        return "redirect:/online_shopping/user/forgotpassword?continue";
    }

    // verify password reset link and direct to reset page if link is valid
    @GetMapping("/resetpassword")
    public String validateResetToken(@RequestParam(name= "token") String token, Model model) {
        Optional<UserEntity> user = userService.validateResetToken(token);
        String email = user.get().getEmail();
        if (user.isEmpty()) {
            return "redirect:/online_shopping/user/forgotpassword?invalidlink";
        }
        model.addAttribute("userEmail",email);
        return "passwordreset";
    }

    // Reset password after validation
    @PostMapping("/resetpassword")
    public String resetNewPassword(@ModelAttribute PasswordModel passwordModel, Model model) throws customUserNotFoundException {
        Optional<UserEntity> user = userService.findUserByEmail(passwordModel.getEmail());
        if (user.isEmpty()) {
            return "redirect:/online_shopping/user/forgotpassword?invalidlink";
        }
        userService.setNewPassword(user.get(), passwordModel);
        return "redirect:/online_shopping/user/login?reset";
    }

    // Change old password using email
    @PostMapping("/changepassword")
    public String changePassword(@ModelAttribute PasswordModel passwordModel) {
        Optional<UserEntity> user = userService.findUserByEmail(passwordModel.getEmail());
        if (user.isPresent()) {
            boolean isPasswordmatched = userService.checkOldPasswordIsValid(user.get(),passwordModel.getOldPassword());
            if (!isPasswordmatched) {
                return "redirect:/online_shopping/user/changepassword?error";
            }
        }
        userService.changePassword(user.get(), passwordModel.getPassword());
        return "redirect:/online_shopping/user/changepassword?success";
    }

    // Update Member information
    @PostMapping("/update/{email}")
    public String updateUser(@PathVariable(name = "email", required = false) String email,@ModelAttribute UserModel userModel, Model model) throws customUserNotFoundException {
        UserEntity user = userService.updateUser(email,userModel);
        model.addAttribute("user", user);
        return "redirect:/online_shopping/user/update/"+user.getEmail()+"?success";
    }

    // De-activate user account
    @GetMapping("/deactivate_account/{email}")
    public String removeUser(@PathVariable("email") String email) throws customUserNotFoundException {
        userService.removeUser(email);
        return "redirect:/online_shopping/user/login?deactivate";
    }

}
