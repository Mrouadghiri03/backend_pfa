package com.pfa.api.app.service.implementation;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService{

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    

    @Override
    public void sendConfirmationEmail(String name, String to, String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Registration Confirmation");

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("confirmationURL", "http://localhost:8080/api/auth/verify?token=" + token); // Replace with your

            String htmlContent = templateEngine.process("email/emailConfirmationTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

    @Override
    public void sendNotificationEmailToHeadOfBranch(User headOfBranch, User newUser,String token) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(headOfBranch.getEmail());
            helper.setSubject("New User Notification");

            Context context = new Context();
            context.setVariable("headOfBranchName", headOfBranch.getFirstName());
            context.setVariable("userName", newUser.getFirstName()+" "+newUser.getLastName());
            context.setVariable("phoneNumber", newUser.getPhoneNumber());
            context.setVariable("email", newUser.getEmail());
            context.setVariable("branch", newUser.getStudiedBranch().getName());
            context.setVariable("cin", newUser.getCin());
            context.setVariable("inscriptionNumber", newUser.getInscriptionNumber());
            context.setVariable("acceptURL", "http://localhost:8080/api/auth/accept?token=" + token);
            context.setVariable("rejectURL", "http://localhost:8080/api/auth/reject?token=" + token);


            String htmlContent = templateEngine.process("email/emailNotificationToHeadOfBranch", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }
    }

	@Override
	public void sendRejectionEmail(User to, String token) {
		try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            System.out.println(to.getEmail());
            helper.setTo(to.getEmail());
            helper.setSubject("Registration Rejection");

            Context context = new Context();
            context.setVariable("name", to.getFirstName());
            System.out.println(to.getStudiedBranch().getName());
            context.setVariable("branch", to.getStudiedBranch().getName() );
                                                                                                            
            String htmlContent = templateEngine.process("email/emailRejectionTemplate", context);
            helper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("message : " + e.getMessage());
            System.out.println("cause : " + e.getCause());
        }

	}


    

}
