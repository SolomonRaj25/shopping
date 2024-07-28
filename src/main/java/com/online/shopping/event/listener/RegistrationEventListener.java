package com.online.shopping.event.listener;

import com.online.shopping.entity.UserEntity;
import com.online.shopping.event.RegistrationEvent;
import com.online.shopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationEventListener implements ApplicationListener<RegistrationEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void onApplicationEvent(RegistrationEvent event) {
        // Sent verification token to user with Url
        UserEntity user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationToken(user,token);
        // Send Email to user
        String url = event.getApplicationUrl()+"/online_shopping/user/verify_registrationToken?token="+token;
        String subject = "User Verification Email";
        String emailBody = "Hi "+user.getFullName()+",\n\n"+
                "Thank you for registering with us, Please click the below link to complete your registration"+"\n\n"+
                url+"\n\n"+"Thank You!";
        sendEmailToUser(user.getEmail(), subject, emailBody);
    }

    public void sendEmailToUser(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("solomonraj257@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
